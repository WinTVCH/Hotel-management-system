package hotel.storage;

import hotel.bookings.Booking;
import hotel.bookings.BookingCalendar;
import hotel.bookings.Stay;
import hotel.extras.ExtraService;
import hotel.extras.LocalPartner;
import hotel.extras.TourActivity;
import hotel.finance.Invoice;
import hotel.housekeeping.HousekeepingTask;
import hotel.housekeeping.Inventory;
import hotel.people.AccountType;
import hotel.people.Guest;
import hotel.people.Person;
import hotel.people.Staff;
import hotel.people.UserAccount;
import hotel.pricing.Promotion;
import hotel.rooms.Property;
import hotel.rooms.Room;
import hotel.rooms.RoomRate;
import hotel.structures.AvailabilityIndex;
import hotel.structures.BinarySearcher;
import hotel.structures.HashIndex;
import hotel.structures.MergeSorter;
import hotel.structures.NameTrie;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private Property property = new Property("Putter's Hotel", "Nakhon Ratchasima");
    private List<UserAccount> accounts = new ArrayList<>();
    private HashIndex<String, UserAccount> accountIndex = new HashIndex<>();
    private List<Guest> guests = new ArrayList<>();
    private NameTrie guestNames = new NameTrie();
    private List<Staff> staff = new ArrayList<>();
    private List<RoomRate> rates = new ArrayList<>();
    private List<Promotion> promotions = new ArrayList<>();
    private List<ExtraService> services = new ArrayList<>();
    private List<TourActivity> activities = new ArrayList<>();
    private List<LocalPartner> partners = new ArrayList<>();
    private BookingCalendar calendar = new BookingCalendar();
    private HashIndex<String, Booking> bookingIndex = new HashIndex<>();
    private List<Stay> stays = new ArrayList<>();
    private List<HousekeepingTask> tasks = new ArrayList<>();
    private Inventory inventory = new Inventory();
    private AvailabilityIndex availability = new AvailabilityIndex(property, calendar);
    private Path dataDir;

    public Property getProperty() {
        return property;
    }

    public List<UserAccount> getAccounts() {
        return accounts;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public List<Staff> getStaff() {
        return staff;
    }

    public List<RoomRate> getRates() {
        return rates;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public List<ExtraService> getServices() {
        return services;
    }

    public List<TourActivity> getActivities() {
        return activities;
    }

    public List<LocalPartner> getPartners() {
        return partners;
    }

    public BookingCalendar getCalendar() {
        return calendar;
    }

    public List<Booking> getBookings() {
        return calendar.getBookings();
    }

    public List<Stay> getStays() {
        return stays;
    }

    public List<HousekeepingTask> getTasks() {
        return tasks;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public AvailabilityIndex getAvailability() {
        return availability;
    }

    public void addAccount(UserAccount account) {
        accounts.add(account);
        accountIndex.put(account.getUsername().toLowerCase(), account);
        Person owner = ownerOf(account);
        if (owner != null) {
            owner.setAccount(account);
        }
    }

    public UserAccount findAccount(String username) {
        return username == null ? null : accountIndex.get(username.trim().toLowerCase());
    }

    public Person ownerOf(UserAccount account) {
        if (account.getType() == AccountType.GUEST) {
            return findGuestById(account.getOwnerId());
        }
        return findStaff(account.getOwnerId());
    }

    private UserAccount accountFor(AccountType type, int ownerId) {
        for (UserAccount account : accounts) {
            if (account.getType() == type && account.getOwnerId() == ownerId) {
                return account;
            }
        }
        return null;
    }

    public void addGuest(Guest guest) {
        guests.add(guest);
        guestNames.insert(guest);
        UserAccount account = accountFor(AccountType.GUEST, guest.getGuestId());
        if (account != null) {
            guest.setAccount(account);
        }
    }

    public List<Guest> guestsById() {
        return new MergeSorter<Guest>((a, b) -> Integer.compare(a.getGuestId(), b.getGuestId())).sort(guests);
    }

    public Guest findGuestById(int guestId) {
        return new BinarySearcher<Guest>(Guest::getGuestId).find(guestsById(), guestId);
    }

    public List<Guest> searchGuests(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return new ArrayList<>();
        }
        return guestNames.startsWith(prefix);
    }

    public void addStaff(Staff member) {
        staff.add(member);
        UserAccount account = accountFor(AccountType.STAFF, member.getStaffId());
        if (account != null) {
            member.setAccount(account);
        }
    }

    public Staff findStaff(int staffId) {
        for (Staff member : staff) {
            if (member.getStaffId() == staffId) {
                return member;
            }
        }
        return null;
    }

    public Room findRoom(String number) {
        return property.findRoom(number);
    }

    public Promotion findPromotion(String code) {
        for (Promotion promotion : promotions) {
            if (promotion.getCode().equalsIgnoreCase(code)) {
                return promotion;
            }
        }
        return null;
    }

    public ExtraService findService(String id) {
        for (ExtraService service : services) {
            if (service.getId().equals(id)) {
                return service;
            }
        }
        return null;
    }

    public TourActivity findActivity(String id) {
        for (TourActivity activity : activities) {
            if (activity.getId().equals(id)) {
                return activity;
            }
        }
        return null;
    }

    public LocalPartner findPartner(String id) {
        for (LocalPartner partner : partners) {
            if (partner.getId().equals(id)) {
                return partner;
            }
        }
        return null;
    }

    public void addBooking(Booking booking) {
        booking.setRates(rates);
        calendar.add(booking);
        bookingIndex.put(booking.getCode().toUpperCase(), booking);
    }

    public Booking findBooking(String code) {
        return code == null ? null : bookingIndex.get(code.trim().toUpperCase());
    }

    public void addStay(Stay stay) {
        stays.add(stay);
        stay.getBooking().getGuest().addStay(stay);
    }

    public Stay findStay(Booking booking) {
        for (Stay stay : stays) {
            if (stay.getBooking() == booking) {
                return stay;
            }
        }
        return null;
    }

    public List<Stay> openStays() {
        List<Stay> result = new ArrayList<>();
        for (Stay stay : stays) {
            if (stay.isOpen()) {
                result.add(stay);
            }
        }
        return result;
    }

    public List<Invoice> getInvoices() {
        List<Invoice> result = new ArrayList<>();
        for (Stay stay : stays) {
            if (stay.getInvoice() != null) {
                result.add(stay.getInvoice());
            }
        }
        return result;
    }

    public List<Booking> bookingsOf(Guest guest) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : calendar.getBookings()) {
            if (booking.getGuest() == guest) {
                result.add(booking);
            }
        }
        return result;
    }

    public void addTask(HousekeepingTask task) {
        tasks.add(task);
    }

    public int nextGuestId() {
        int max = 0;
        for (Guest guest : guests) {
            max = Math.max(max, guest.getGuestId());
        }
        return max + 1;
    }

    public String nextBookingCode() {
        int max = 24000;
        for (Booking booking : calendar.getBookings()) {
            max = Math.max(max, Integer.parseInt(booking.getCode().substring(3)));
        }
        return "BK-" + (max + 1);
    }

    public String nextInvoiceNumber() {
        int max = 1000;
        for (Invoice invoice : getInvoices()) {
            max = Math.max(max, Integer.parseInt(invoice.getNumber().substring(4)));
        }
        return "INV-" + (max + 1);
    }

    public int nextTaskId() {
        int max = 0;
        for (HousekeepingTask task : tasks) {
            max = Math.max(max, task.getId());
        }
        return max + 1;
    }

    public void setDataDir(Path dataDir) {
        this.dataDir = dataDir;
    }

    public void save() {
        if (dataDir != null) {
            new DataSaver(this).saveAll(dataDir);
        }
    }

}
