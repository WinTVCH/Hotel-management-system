package hotel.storage;

import com.google.gson.JsonObject;
import hotel.rooms.RoomRate;

public class RoomRateImporter extends JsonImporter<RoomRate> {

    @Override
    protected RoomRate parseItem(JsonObject item) {
        return null; // TODO
    }

}
