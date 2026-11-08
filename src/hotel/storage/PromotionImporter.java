package hotel.storage;

import com.google.gson.JsonObject;
import hotel.pricing.FixedAmountPromotion;
import hotel.pricing.LongStayPromotion;
import hotel.pricing.PercentPromotion;
import hotel.pricing.Promotion;

public class PromotionImporter extends JsonImporter<Promotion> {

    public PromotionImporter(DataStore store) {
        super("promotions.json", store);
    }

    @Override
    protected Promotion parseItem(JsonObject item) {
        String code = text(item, "code");
        String description = text(item, "description");
        String type = text(item, "type");
        double value = number(item, "value");
        if (type.equals("PERCENT")) {
            return new PercentPromotion(code, description, value);
        }
        if (type.equals("FIXED")) {
            return new FixedAmountPromotion(code, description, value);
        }
        if (type.equals("LONG_STAY")) {
            return new LongStayPromotion(code, description, integer(item, "minNights", 3), value);
        }
        throw new IllegalArgumentException("Unknown promotion type " + type);
    }

    @Override
    protected void addToStore(Promotion item) {
        store.getPromotions().add(item);
    }

}
