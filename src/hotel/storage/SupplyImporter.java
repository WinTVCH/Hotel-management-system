package hotel.storage;

import com.google.gson.JsonObject;
import hotel.housekeeping.SupplyItem;

public class SupplyImporter extends JsonImporter<SupplyItem> {

    @Override
    protected SupplyItem parseItem(JsonObject item) {
        return null; // TODO
    }

}
