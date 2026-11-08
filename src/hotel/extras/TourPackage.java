package hotel.extras;

import java.util.ArrayList;
import java.util.List;

public class TourPackage extends ExtraService {
    private List<TourActivity> activities = new ArrayList<>();

    public TourPackage(String id, String name, double unitPrice, String description) {
        super(id, name, unitPrice, "person", description);
    }

    public List<TourActivity> getActivities() {
        return activities;
    }

    public void addActivity(TourActivity activity) {
        activities.add(activity);
    }

    public int totalMinutes() {
        int minutes = 0;
        for (TourActivity activity : activities) {
            minutes += activity.getMinutes();
        }
        return minutes;
    }

    @Override
    public double calculateCharge() {
        double perPerson = getUnitPrice();
        for (TourActivity activity : activities) {
            perPerson += activity.getExtraCost();
        }
        return perPerson * getQuantity();
    }

    @Override
    public String getCategory() {
        return "Tours";
    }

    @Override
    public String getDetails() {
        List<String> partners = new ArrayList<>();
        for (TourActivity activity : activities) {
            String partner = activity.getPartner() == null ? "Hotel" : activity.getPartner().getName();
            if (!partners.contains(partner)) {
                partners.add(partner);
            }
        }
        return activities.size() + " activities, " + (totalMinutes() / 60.0) + " h, " + String.join(", ", partners);
    }

}
