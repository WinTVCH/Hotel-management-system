package hotel.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hotel.extras.ExtraService;
import hotel.extras.LaundryService;
import hotel.extras.MealService;
import hotel.extras.TourActivity;
import hotel.extras.TourPackage;
import hotel.extras.TransportService;

public class ServiceImporter extends JsonImporter<ExtraService> {

    public ServiceImporter(DataStore store) {
        super("services.json", store);
    }

    @Override
    protected ExtraService parseItem(JsonObject item) {
        String id = text(item, "id");
        String name = text(item, "name");
        double price = number(item, "unitPrice");
        String description = text(item, "description", "");
        String type = text(item, "type");
        if (type.equals("MEAL")) {
            return new MealService(id, name, price, description, text(item, "mealType", "Meal"));
        }
        if (type.equals("LAUNDRY")) {
            return new LaundryService(id, name, price, description, flag(item, "express", false));
        }
        if (type.equals("TRANSPORT")) {
            return new TransportService(id, name, price, description, text(item, "route", ""), integer(item, "maxPassengers", 4));
        }
        if (type.equals("TOUR")) {
            TourPackage tour = new TourPackage(id, name, price, description);
            if (item.has("activities")) {
                for (JsonElement element : item.getAsJsonArray("activities")) {
                    TourActivity activity = store.findActivity(element.getAsString());
                    if (activity != null) {
                        tour.addActivity(activity);
                    }
                }
            }
            return tour;
        }
        throw new IllegalArgumentException("Unknown service type " + type);
    }

    @Override
    protected void addToStore(ExtraService item) {
        store.getServices().add(item);
    }

}
