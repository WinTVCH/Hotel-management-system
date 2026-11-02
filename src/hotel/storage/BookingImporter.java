package hotel.storage;

import com.google.gson.JsonObject;
import hotel.bookings.Booking;

public class BookingImporter extends JsonImporter<Booking> {

    @Override
    protected Booking parseItem(JsonObject item) {
        return null; // TODO
    }

}
