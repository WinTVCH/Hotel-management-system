package hotel.storage;

import com.google.gson.JsonObject;
import hotel.people.Guest;

public class GuestImporter extends JsonImporter<Guest> {

    @Override
    protected Guest parseItem(JsonObject item) {
        return null; // TODO
    }

}
