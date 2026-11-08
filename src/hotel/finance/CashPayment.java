package hotel.finance;

public class CashPayment extends Payment {
    private double received;

    public CashPayment(double amount, double received) {
        super(amount);
        this.received = received;
    }

    public double getReceived() {
        return received;
    }

    public double change() {
        return Math.max(0, received - getAmount());
    }

    @Override
    public boolean processPayment() {
        if (received < getAmount()) {
            return false;
        }
        return complete();
    }

    @Override
    public String getMethod() {
        return "Cash";
    }

    @Override
    public String getReference() {
        return String.valueOf(received);
    }

}
