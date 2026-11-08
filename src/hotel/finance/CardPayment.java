package hotel.finance;

public class CardPayment extends Payment {
    private String last4;

    public CardPayment(double amount, String last4) {
        super(amount);
        this.last4 = last4 == null ? "" : last4.trim();
    }

    @Override
    public boolean processPayment() {
        if (!last4.matches("\\d{4}")) {
            return false;
        }
        return complete();
    }

    @Override
    public String getMethod() {
        return "Card";
    }

    @Override
    public String getReference() {
        return last4;
    }

}
