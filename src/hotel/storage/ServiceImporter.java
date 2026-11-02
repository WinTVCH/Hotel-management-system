package hotel.storage;

import com.google.gson.JsonObject;
import hotel.extras.ExtraService;

public class ServiceImporter extends JsonImporter<ExtraService> {

    @Override
    protected ExtraService parseItem(JsonObject item) {
        return null; // TODO
    }

}
