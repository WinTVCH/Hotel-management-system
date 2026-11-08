package hotel.storage;

import com.google.gson.JsonObject;
import hotel.rooms.Bungalow;
import hotel.rooms.FamilyRoom;
import hotel.rooms.Room;
import hotel.rooms.RoomStatus;
import hotel.rooms.StandardRoom;

public class RoomImporter extends JsonImporter<Room> {

    public RoomImporter(DataStore store) {
        super("rooms.json", store);
    }

    @Override
    protected Room parseItem(JsonObject item) {
        String number = text(item, "number");
        double price = number(item, "price");
        String type = text(item, "type");
        Room room;
        if (type.equals("Standard")) {
            room = new StandardRoom(number, price);
        } else if (type.equals("Family")) {
            room = new FamilyRoom(number, price, integer(item, "extraBeds", 0));
        } else if (type.equals("Bungalow")) {
            room = new Bungalow(number, price, number(item, "cleaningFee", 0));
        } else {
            throw new IllegalArgumentException("Unknown room type " + type);
        }
        room.setStatus(RoomStatus.valueOf(text(item, "status", "AVAILABLE")));
        return room;
    }

    @Override
    protected void addToStore(Room item) {
        store.getProperty().addRoom(item);
    }

}
