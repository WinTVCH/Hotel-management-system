package hotel.pricing;

public class FixedAmountPromotion extends Promotion {
    private double amount;

    public FixedAmountPromotion(String code, String description, double amount) {
        super(code, description);
        this.amount = amount;
    }

    @Override
    public double applyTo(double total, int nights) {
        return Math.max(0, total - amount);
    }

    @Override
    public String getType() {
        return "FIXED";
    }

    @Override
    public double getValue() {
        return amount;
    }

}
