package hotel.extras;

public class LaundryService extends ExtraService {
    private boolean express;

    public LaundryService(String id, String name, double unitPrice, String description, boolean express) {
        super(id, name, unitPrice, "kg", description);
        this.express = express;
    }

    public boolean isExpress() {
        return express;
    }

    @Override
    public double calculateCharge() {
        double charge = getUnitPrice() * getQuantity();
        if (express) {
            charge = charge * 1.5;
        }
        return charge;
    }

    @Override
    public String getCategory() {
        return "Laundry";
    }

    @Override
    public String getDetails() {
        return express ? "Express, +50%, per kg" : "Per kg";
    }

}
