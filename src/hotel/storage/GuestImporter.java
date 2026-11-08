package hotel.storage;

import com.google.gson.JsonObject;
import hotel.people.Guest;

public class GuestImporter extends JsonImporter<Guest> {

    public GuestImporter(DataStore store) {
        super("guests.json", store);
    }

    @Override
    protected Guest parseItem(JsonObject item) {
        return new Guest(text(item, "name"), text(item, "phone", ""), text(item, "email", ""), integer(item, "guestId"), text(item, "idNumber", ""));
    }

    @Override
    protected void addToStore(Guest item) {
        store.addGuest(item);
    }

}
