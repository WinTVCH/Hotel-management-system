package hotel.storage;

import com.google.gson.JsonObject;
import hotel.housekeeping.SupplyItem;

public class SupplyImporter extends JsonImporter<SupplyItem> {

    public SupplyImporter(DataStore store) {
        super("supplies.json", store);
    }

    @Override
    protected SupplyItem parseItem(JsonObject item) {
        return new SupplyItem(text(item, "name"), integer(item, "quantity"), integer(item, "reorderLevel", 0));
    }

    @Override
    protected void addToStore(SupplyItem item) {
        store.getInventory().addItem(item);
    }

}
