package hotel.people;

import java.util.List;

public class Manager extends Role {

    public Manager(String roleId, int rank) {
        super("Manager", roleId, rank);
    }

    @Override
    public List<String> getMenuItems() {
        List<String> items = super.getMenuItems();
        items.add("Bookings");
        items.add("Room Rack");
        items.add("Billing");
        items.add("Services");
        items.add("Reports");
        return items;
    }

}
