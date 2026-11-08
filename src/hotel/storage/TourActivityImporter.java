package hotel.storage;

import com.google.gson.JsonObject;
import hotel.extras.LocalPartner;
import hotel.extras.TourActivity;

public class TourActivityImporter extends JsonImporter<TourActivity> {

    public TourActivityImporter(DataStore store) {
        super("tour_activities.json", store);
    }

    @Override
    protected TourActivity parseItem(JsonObject item) {
        LocalPartner partner = store.findPartner(text(item, "partnerId", ""));
        return new TourActivity(text(item, "id"), text(item, "name"), integer(item, "minutes", 60), number(item, "extraCost", 0), partner);
    }

    @Override
    protected void addToStore(TourActivity item) {
        store.getActivities().add(item);
    }

}
