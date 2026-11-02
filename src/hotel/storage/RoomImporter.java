package hotel.storage;

import com.google.gson.JsonObject;
import hotel.rooms.Room;

public class RoomImporter extends JsonImporter<Room> {

    @Override
    protected Room parseItem(JsonObject item) {
        return null; // TODO
    }

}
