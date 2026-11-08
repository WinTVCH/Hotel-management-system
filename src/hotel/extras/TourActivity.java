package hotel.extras;

public class TourActivity {
    private String id;
    private String name;
    private int minutes;
    private double extraCost;
    private LocalPartner partner;

    public TourActivity(String id, String name, int minutes, double extraCost, LocalPartner partner) {
        this.id = id;
        this.name = name;
        this.minutes = minutes;
        this.extraCost = extraCost;
        this.partner = partner;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMinutes() {
        return minutes;
    }

    public double getExtraCost() {
        return extraCost;
    }

    public LocalPartner getPartner() {
        return partner;
    }

}
