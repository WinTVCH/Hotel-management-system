package hotel.extras;

public class LocalPartner {
    private String id;
    private String name;
    private String phone;
    private double commission;

    public LocalPartner(String id, String name, String phone, double commission) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.commission = commission;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public double getCommission() {
        return commission;
    }

    public double commissionOn(double amount) {
        return amount * commission / 100.0;
    }

}
