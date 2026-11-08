package hotel.pricing;

public class PercentPromotion extends Promotion {
    private double percent;

    public PercentPromotion(String code, String description, double percent) {
        super(code, description);
        this.percent = percent;
    }

    @Override
    public double applyTo(double amount, int nights) {
        return amount - amount * percent / 100.0;
    }

    @Override
    public String getType() {
        return "PERCENT";
    }

    @Override
    public double getValue() {
        return percent;
    }

}
