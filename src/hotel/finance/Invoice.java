package hotel.finance;

import hotel.bookings.Stay;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private String number;
    private Stay stay;
    private List<InvoiceLine> lines = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();

    public Invoice(String number, Stay stay) {
        this.number = number;
        this.stay = stay;
    }

    public String getNumber() {
        return number;
    }

    public Stay getStay() {
        return stay;
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void addLine(InvoiceLine line) {
        lines.add(line);
    }

    public void addCharge(Chargeable item) {
        lines.add(item.toInvoiceLine());
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    public double total() {
        double sum = 0;
        for (InvoiceLine line : lines) {
            sum += line.getAmount();
        }
        return sum;
    }

    public double paid() {
        double sum = 0;
        for (Payment payment : payments) {
            sum += payment.getAmount();
        }
        return sum;
    }

    public double outstanding() {
        return Math.max(0, Math.round((total() - paid()) * 100) / 100.0);
    }

    public boolean isPaid() {
        return outstanding() <= 0.009;
    }

}
