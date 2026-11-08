package hotel.people;

import java.util.List;

public class Housekeeper extends Role {

    public Housekeeper(String roleId, int rank) {
        super("Housekeeper", roleId, rank);
    }

    @Override
    public List<String> getMenuItems() {
        List<String> items = super.getMenuItems();
        items.add("Room Rack");
        return items;
    }

}
