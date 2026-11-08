package hotel.extras;

public class TransportService extends ExtraService {
    private String route;
    private int maxPassengers;

    public TransportService(String id, String name, double unitPrice, String description, String route, int maxPassengers) {
        super(id, name, unitPrice, "trip", description);
        this.route = route;
        this.maxPassengers = maxPassengers;
    }

    public String getRoute() {
        return route;
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }

    @Override
    public double calculateCharge() {
        return getUnitPrice() * getQuantity();
    }

    @Override
    public String getCategory() {
        return "Transport";
    }

    @Override
    public String getDetails() {
        return route + ", up to " + maxPassengers + " people";
    }

}
