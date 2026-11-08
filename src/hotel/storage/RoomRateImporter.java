package hotel.storage;

import com.google.gson.JsonObject;
import hotel.rooms.RoomRate;

public class RoomRateImporter extends JsonImporter<RoomRate> {

    public RoomRateImporter(DataStore store) {
        super("room_rates.json", store);
    }

    @Override
    protected RoomRate parseItem(JsonObject item) {
        return new RoomRate(text(item, "name"), text(item, "roomType", "All"), date(item, "from"), date(item, "to"), number(item, "percent"));
    }

    @Override
    protected void addToStore(RoomRate item) {
        store.getRates().add(item);
    }

}
