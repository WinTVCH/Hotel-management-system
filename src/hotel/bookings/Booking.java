package hotel.bookings;

import hotel.finance.Payment;
import hotel.people.Guest;
import hotel.pricing.Promotion;
import hotel.rooms.Room;
import hotel.rooms.RoomRate;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private String code;
    private Guest guest;
    private List<Room> rooms = new ArrayList<>();
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BookingStatus status = BookingStatus.CONFIRMED;
    private BookingChannel channel;
    private Promotion promotion;
    private String requests = "";
    private Payment deposit;
    private List<RoomRate> rates = new ArrayList<>();

    public Booking(String code, Guest guest, LocalDate checkIn, LocalDate checkOut, BookingChannel channel) {
        this.code = code;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.channel = channel;
    }

    public String getCode() {
        return code;
    }

    public Guest getGuest() {
        return guest;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BookingChannel getChannel() {
        return channel;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public String getRequests() {
        return requests;
    }

    public void setRequests(String requests) {
        this.requests = requests == null ? "" : requests;
    }

    public Payment getDeposit() {
        return deposit;
    }

    public void setDeposit(Payment deposit) {
        this.deposit = deposit;
    }

    public void setRates(List<RoomRate> rates) {
        this.rates = rates;
    }

    public int nights() {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public double nightlyRate(Room room, LocalDate night) {
        double nightly = room.calculateRate();
        for (RoomRate rate : rates) {
            if (rate.appliesTo(room, night)) {
                nightly = rate.apply(nightly);
            }
        }
        return nightly;
    }

    public double roomSubtotal(Room room) {
        double sum = 0;
        for (LocalDate night = checkIn; night.isBefore(checkOut); night = night.plusDays(1)) {
            sum += nightlyRate(room, night);
        }
        return sum;
    }

    public double subtotal() {
        double sum = 0;
        for (Room room : rooms) {
            sum += roomSubtotal(room);
        }
        return sum;
    }

    public double totalPrice() {
        double total = subtotal();
        if (promotion != null) {
            total = promotion.applyTo(total, nights());
        }
        return Math.max(0, total);
    }

    public double discount() {
        return subtotal() - totalPrice();
    }

    public boolean overlaps(LocalDate from, LocalDate to) {
        return checkIn.isBefore(to) && from.isBefore(checkOut);
    }

    public boolean isActive() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.CHECKED_IN;
    }

    public boolean canCheckIn(LocalDate today) {
        return status == BookingStatus.CONFIRMED && !today.isBefore(checkIn) && today.isBefore(checkOut);
    }

    public boolean canCheckOut() {
        return status == BookingStatus.CHECKED_IN;
    }

    public boolean canCancel(LocalDate today) {
        return status == BookingStatus.CONFIRMED && today.isBefore(checkIn);
    }

    public String getRoomNumbers() {
        List<String> numbers = new ArrayList<>();
        for (Room room : rooms) {
            numbers.add(room.getRoomNumber());
        }
        return String.join(", ", numbers);
    }

    public String getRoomTypes() {
        List<String> types = new ArrayList<>();
        for (Room room : rooms) {
            if (!types.contains(room.getTypeName())) {
                types.add(room.getTypeName());
            }
        }
        return String.join(", ", types);
    }

    public String getDisplayName() {
        return guest.getName();
    }

}
