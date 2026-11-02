package hotel.storage;

import com.google.gson.JsonObject;
import hotel.housekeeping.HousekeepingTask;

public class HousekeepingTaskImporter extends JsonImporter<HousekeepingTask> {

    @Override
    protected HousekeepingTask parseItem(JsonObject item) {
        return null; // TODO
    }

}
