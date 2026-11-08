package hotel.finance;

public class TransferPayment extends Payment {
    private String bankRef;

    public TransferPayment(double amount, String bankRef) {
        super(amount);
        this.bankRef = bankRef == null ? "" : bankRef.trim();
    }

    @Override
    public boolean processPayment() {
        if (bankRef.length() < 4) {
            return false;
        }
        return complete();
    }

    @Override
    public String getMethod() {
        return "Transfer";
    }

    @Override
    public String getReference() {
        return bankRef;
    }

}
