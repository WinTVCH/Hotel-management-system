package hotel.storage;

import com.google.gson.JsonObject;
import hotel.pricing.Promotion;

public class PromotionImporter extends JsonImporter<Promotion> {

    @Override
    protected Promotion parseItem(JsonObject item) {
        return null; // TODO
    }

}
