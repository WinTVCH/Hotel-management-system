package hotel.reports;

import hotel.bookings.Stay;
import hotel.people.Guest;
import hotel.storage.DataStore;
import hotel.structures.MergeSorter;
import java.time.LocalDate;
import java.util.List;

public class GuestReport extends Report {

    public GuestReport(DataStore store, LocalDate from, LocalDate to) {
        super("Guests", store, from, to);
    }

    @Override
    public String render() {
        StringBuilder text = new StringBuilder(header());
        List<Guest> guests = new MergeSorter<Guest>((a, b) -> a.getName().compareToIgnoreCase(b.getName())).sort(store.getGuests());
        text.append(String.format("%-8s %-24s %6s %14s %s%n", "ID", "Name", "Stays", "Paid (THB)", "Account"));
        int repeat = 0;
        for (Guest guest : guests) {
            double paid = 0;
            for (Stay stay : guest.getStays()) {
                if (stay.getInvoice() != null) {
                    paid += stay.getInvoice().paid();
                }
            }
            if (guest.getStays().size() > 1) {
                repeat++;
            }
            text.append(String.format("%-8s %-24s %6d %14s %s%n", guest.getCode(), guest.getName(), guest.getStays().size(), money(paid), guest.hasAccount() ? "yes" : "no"));
        }
        text.append("-".repeat(56)).append("\n");
        text.append("Guests: ").append(guests.size()).append(", repeat guests: ").append(repeat).append("\n");
        return text.toString();
    }

}
