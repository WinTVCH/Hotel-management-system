package hotel.people;

import java.util.List;

public class TourGuide extends Role {

    public TourGuide(String roleId, int rank) {
        super("Tour Guide", roleId, rank);
    }

    @Override
    public List<String> getMenuItems() {
        List<String> items = super.getMenuItems();
        items.add("Services");
        return items;
    }

}
