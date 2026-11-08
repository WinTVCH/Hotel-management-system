package hotel.finance;

import java.time.LocalDateTime;

public abstract class Payment {
    private double amount;
    private LocalDateTime paidAt;

    public Payment(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public boolean isProcessed() {
        return paidAt != null;
    }

    protected boolean complete() {
        if (amount <= 0) {
            return false;
        }
        paidAt = LocalDateTime.now();
        return true;
    }

    public abstract boolean processPayment();

    public abstract String getMethod();

    public abstract String getReference();

}
