package hotel.people;

import java.util.List;

public class Receptionist extends Role {

    public Receptionist(String roleId, int rank) {
        super("Receptionist", roleId, rank);
    }

    @Override
    public List<String> getMenuItems() {
        List<String> items = super.getMenuItems();
        items.add("Bookings");
        items.add("Room Rack");
        items.add("Billing");
        items.add("Services");
        return items;
    }

}
