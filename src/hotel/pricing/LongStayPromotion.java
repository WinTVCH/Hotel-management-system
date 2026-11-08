package hotel.pricing;

public class LongStayPromotion extends Promotion {
    private int minNights;
    private double percent;

    public LongStayPromotion(String code, String description, int minNights, double percent) {
        super(code, description);
        this.minNights = minNights;
        this.percent = percent;
    }

    public int getMinNights() {
        return minNights;
    }

    @Override
    public double applyTo(double amount, int nights) {
        if (nights < minNights) {
            return amount;
        }
        return amount - amount * percent / 100.0;
    }

    @Override
    public String getType() {
        return "LONG_STAY";
    }

    @Override
    public double getValue() {
        return percent;
    }

}
