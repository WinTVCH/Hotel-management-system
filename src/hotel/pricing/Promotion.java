package hotel.pricing;

public abstract class Promotion {
    private String code;
    private String description;

    public Promotion(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public abstract double applyTo(double amount, int nights);

    public abstract String getType();

    public abstract double getValue();

    @Override
    public String toString() {
        return description;
    }

}
