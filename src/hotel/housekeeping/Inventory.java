package hotel.housekeeping;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<SupplyItem> items = new ArrayList<>();

    public List<SupplyItem> getItems() {
        return items;
    }

    public void addItem(SupplyItem item) {
        items.add(item);
    }

    public SupplyItem find(String name) {
        for (SupplyItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public boolean use(String name, int amount) {
        SupplyItem item = find(name);
        return item != null && item.use(amount);
    }

    public List<SupplyItem> lowStock() {
        List<SupplyItem> result = new ArrayList<>();
        for (SupplyItem item : items) {
            if (item.isLow()) {
                result.add(item);
            }
        }
        return result;
    }

}
