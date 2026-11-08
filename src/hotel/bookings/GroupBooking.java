package hotel.bookings;

import hotel.people.Guest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GroupBooking extends Booking {
    private String groupName;
    private List<Guest> members = new ArrayList<>();

    public GroupBooking(String code, Guest leader, LocalDate checkIn, LocalDate checkOut, BookingChannel channel, String groupName) {
        super(code, leader, checkIn, checkOut, channel);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Guest> getMembers() {
        return members;
    }

    public void addMember(Guest guest) {
        if (!members.contains(guest)) {
            members.add(guest);
        }
    }

    @Override
    public double totalPrice() {
        double total = super.totalPrice();
        if (getRooms().size() >= 3) {
            total = total * 0.95;
        }
        return total;
    }

    @Override
    public String getDisplayName() {
        return groupName + " (group)";
    }

}
