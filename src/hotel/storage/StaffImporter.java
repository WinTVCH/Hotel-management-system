package hotel.storage;

import com.google.gson.JsonObject;
import hotel.people.Staff;

public class StaffImporter extends JsonImporter<Staff> {

    @Override
    protected Staff parseItem(JsonObject item) {
        return null; // TODO
    }

}
