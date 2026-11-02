package hotel.storage;

import com.google.gson.JsonObject;

public abstract class JsonImporter<T> {

    protected abstract T parseItem(JsonObject item);

}
