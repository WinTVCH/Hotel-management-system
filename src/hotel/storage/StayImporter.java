package hotel.storage;

import com.google.gson.JsonObject;
import hotel.bookings.Stay;

public class StayImporter extends JsonImporter<Stay> {

    @Override
    protected Stay parseItem(JsonObject item) {
        return null; // TODO
    }

}
