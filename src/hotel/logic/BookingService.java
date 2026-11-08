package hotel.logic;

import hotel.bookings.Booking;
import hotel.bookings.BookingChannel;
import hotel.bookings.BookingStatus;
import hotel.bookings.GroupBooking;
import hotel.bookings.Stay;
import hotel.errors.BookingConflictException;
import hotel.errors.ValidationException;
import hotel.finance.Invoice;
import hotel.finance.Payment;
import hotel.housekeeping.TaskPriority;
import hotel.people.Guest;
import hotel.pricing.LongStayPromotion;
import hotel.pricing.Promotion;
import hotel.rooms.Room;
import hotel.rooms.RoomStatus;
import hotel.storage.DataStore;
import hotel.structures.MergeSorter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingService {
    private DataStore store;
    private BillingService billing;
    private HousekeepingService housekeeping;

    public BookingService(DataStore store, BillingService billing, HousekeepingService housekeeping) {
        this.store = store;
        this.billing = billing;
        this.housekeeping = housekeeping;
    }

    private void checkDates(LocalDate checkIn, LocalDate checkOut) throws ValidationException {
        if (checkIn == null || checkOut == null) {
            throw new ValidationException("Choose the check-in and check-out dates.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("Check-out must be after check-in.");
        }
    }

    public List<Room> findFreeRooms(LocalDate checkIn, LocalDate checkOut, int guests) throws ValidationException {
        checkDates(checkIn, checkOut);
        List<Room> rooms = store.getAvailability().freeRooms(checkIn, checkOut, guests);
        return new MergeSorter<Room>((a, b) -> Double.compare(a.calculateRate(), b.calculateRate())).sort(rooms);
    }

    public Map<String, Room> cheapestFreeByType(LocalDate checkIn, LocalDate checkOut, int guests) throws ValidationException {
        Map<String, Room> result = new LinkedHashMap<>();
        for (Room room : findFreeRooms(checkIn, checkOut, guests)) {
            result.putIfAbsent(room.getTypeName(), room);
        }
        return result;
    }

    public Booking quote(List<Room> rooms, LocalDate checkIn, LocalDate checkOut, Promotion promotion) {
        Booking booking = new Booking("QUOTE", null, checkIn, checkOut, BookingChannel.WALK_IN);
        booking.setRates(store.getRates());
        for (Room room : rooms) {
            booking.addRoom(room);
        }
        booking.setPromotion(promotion);
        return booking;
    }

    public Promotion bestLongStayPromotion(int nights) {
        Promotion best = null;
        for (Promotion promotion : store.getPromotions()) {
            if (promotion instanceof LongStayPromotion && promotion.applyTo(100, nights) < 100) {
                if (best == null || promotion.applyTo(100, nights) < best.applyTo(100, nights)) {
                    best = promotion;
                }
            }
        }
        return best;
    }

    public Booking book(Guest guest, List<Room> rooms, LocalDate checkIn, LocalDate checkOut, BookingChannel channel, Promotion promotion, String requests, String groupName) throws ValidationException, BookingConflictException {
        if (guest == null) {
            throw new ValidationException("Choose a guest.");
        }
        if (rooms == null || rooms.isEmpty()) {
            throw new ValidationException("Choose at least one room.");
        }
        checkDates(checkIn, checkOut);
        if (checkIn.isBefore(LocalDate.now())) {
            throw new ValidationException("Check-in cannot be in the past.");
        }
        for (Room room : rooms) {
            if (!store.getAvailability().isFree(room, checkIn, checkOut)) {
                throw new BookingConflictException("Room " + room.getRoomNumber() + " is already booked for those dates.");
            }
        }
        String code = store.nextBookingCode();
        Booking booking;
        if (groupName != null && !groupName.isBlank()) {
            booking = new GroupBooking(code, guest, checkIn, checkOut, channel, groupName.trim());
        } else {
            booking = new Booking(code, guest, checkIn, checkOut, channel);
        }
        for (Room room : rooms) {
            booking.addRoom(room);
        }
        booking.setPromotion(promotion);
        booking.setRequests(requests);
        store.addBooking(booking);
        store.save();
        return booking;
    }

    public void payDeposit(Booking booking, Payment payment) throws ValidationException {
        if (!payment.processPayment()) {
            throw new ValidationException(billing.failureMessage(payment));
        }
        booking.setDeposit(payment);
        store.save();
    }

    public Stay checkIn(Booking booking, LocalDate today) throws ValidationException {
        if (booking == null) {
            throw new ValidationException("Choose a booking.");
        }
        if (!booking.canCheckIn(today)) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                throw new ValidationException("Booking " + booking.getCode() + " is " + booking.getStatus().name().toLowerCase().replace('_', ' ') + ".");
            }
            throw new ValidationException("Check-in for " + booking.getCode() + " opens on " + booking.getCheckIn() + ".");
        }
        for (Room room : booking.getRooms()) {
            if (room.getStatus() == RoomStatus.OCCUPIED || room.getStatus() == RoomStatus.OUT_OF_SERVICE) {
                throw new ValidationException("Room " + room.getRoomNumber() + " is not ready yet.");
            }
        }
        Stay stay = new Stay(booking, LocalDateTime.now());
        billing.openInvoice(stay);
        booking.setStatus(BookingStatus.CHECKED_IN);
        for (Room room : booking.getRooms()) {
            room.setStatus(RoomStatus.OCCUPIED);
        }
        store.addStay(stay);
        store.save();
        return stay;
    }

    public Invoice checkOut(Booking booking, Payment payment) throws ValidationException {
        if (booking == null || !booking.canCheckOut()) {
            throw new ValidationException("Only a checked-in booking can check out.");
        }
        Stay stay = store.findStay(booking);
        Invoice invoice = stay.getInvoice();
        if (!invoice.isPaid()) {
            if (payment == null) {
                throw new ValidationException("Pay the balance of " + String.format("%,.2f", invoice.outstanding()) + " first.");
            }
            billing.pay(invoice, payment);
            if (!invoice.isPaid()) {
                throw new ValidationException("A balance of " + String.format("%,.2f", invoice.outstanding()) + " is still due.");
            }
        }
        stay.close(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_OUT);
        for (Room room : booking.getRooms()) {
            room.setStatus(RoomStatus.DIRTY);
            housekeeping.createTask(room, "Clean after check-out", TaskPriority.HIGH);
        }
        store.save();
        return invoice;
    }

    public void cancel(Booking booking, LocalDate today) throws ValidationException {
        if (booking == null) {
            throw new ValidationException("Choose a booking.");
        }
        if (!booking.canCancel(today)) {
            throw new ValidationException("Only confirmed bookings before the check-in day can be cancelled.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        store.save();
    }

    public Booking findByCode(String code) {
        return store.findBooking(code);
    }

    public List<Booking> sorted(String order) {
        MergeSorter<Booking> sorter;
        if (order.equals("Guest name")) {
            sorter = new MergeSorter<>((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        } else if (order.equals("Total price")) {
            sorter = new MergeSorter<>((a, b) -> Double.compare(b.totalPrice(), a.totalPrice()));
        } else {
            sorter = new MergeSorter<>((a, b) -> a.getCheckIn().compareTo(b.getCheckIn()));
        }
        return sorter.sort(store.getBookings());
    }

    public List<Booking> bookingsOf(Guest guest) {
        return new MergeSorter<Booking>((a, b) -> b.getCheckIn().compareTo(a.getCheckIn())).sort(store.bookingsOf(guest));
    }

    public Booking currentBookingOf(Guest guest, LocalDate today) {
        Booking next = null;
        for (Booking booking : store.bookingsOf(guest)) {
            if (booking.getStatus() == BookingStatus.CHECKED_IN) {
                return booking;
            }
            if (booking.getStatus() == BookingStatus.CONFIRMED && booking.getCheckOut().isAfter(today)) {
                if (next == null || booking.getCheckIn().isBefore(next.getCheckIn())) {
                    next = booking;
                }
            }
        }
        return next;
    }

    public List<Booking> arrivals(LocalDate day) {
        return new MergeSorter<Booking>((a, b) -> a.getDisplayName().compareToIgnoreCase(b.getDisplayName())).sort(store.getCalendar().arrivalsOn(day));
    }

    public List<Booking> departures(LocalDate day) {
        return store.getCalendar().departuresOn(day);
    }

    public List<Guest> searchGuests(String prefix) {
        return store.searchGuests(prefix);
    }

    public Guest findGuest(int guestId) {
        return store.findGuestById(guestId);
    }

    public Guest addGuest(String name, String phone, String email, String idNumber) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Enter the guest's name.");
        }
        Guest guest = new Guest(name.trim(), phone == null ? "" : phone.trim(), email == null ? "" : email.trim(), store.nextGuestId(), idNumber == null ? "" : idNumber.trim());
        store.addGuest(guest);
        store.save();
        return guest;
    }

    public List<Stay> openStays() {
        List<Stay> stays = new ArrayList<>(store.openStays());
        return new MergeSorter<Stay>((a, b) -> a.getBooking().getRoomNumbers().compareTo(b.getBooking().getRoomNumbers())).sort(stays);
    }

}
