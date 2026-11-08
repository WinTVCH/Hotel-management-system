package hotel.logic;

import hotel.bookings.Booking;
import hotel.bookings.Stay;
import hotel.errors.ValidationException;
import hotel.extras.ExtraService;
import hotel.finance.CardPayment;
import hotel.finance.CashPayment;
import hotel.finance.Invoice;
import hotel.finance.InvoiceLine;
import hotel.finance.Payment;
import hotel.finance.TransferPayment;
import hotel.rooms.Room;
import hotel.storage.DataStore;
import hotel.structures.MergeSorter;
import java.util.ArrayList;
import java.util.List;

public class BillingService {
    private DataStore store;

    public BillingService(DataStore store) {
        this.store = store;
    }

    public Invoice openInvoice(Stay stay) {
        Booking booking = stay.getBooking();
        Invoice invoice = new Invoice(store.nextInvoiceNumber(), stay);
        int nights = Math.max(1, booking.nights());
        for (Room room : booking.getRooms()) {
            double amount = booking.roomSubtotal(room);
            invoice.addLine(new InvoiceLine("Room " + room.getRoomNumber() + " " + room.getTypeName(), nights, amount / nights, amount));
        }
        double discount = booking.discount();
        if (discount > 0.009) {
            String label = booking.getPromotion() == null ? "Group discount" : "Promotion: " + booking.getPromotion().getDescription();
            invoice.addLine(new InvoiceLine(label, 1, -discount, -discount));
        }
        if (booking.getDeposit() != null) {
            invoice.addPayment(booking.getDeposit());
        }
        stay.setInvoice(invoice);
        return invoice;
    }

    public InvoiceLine addCharge(Stay stay, ExtraService service, int quantity) throws ValidationException {
        if (stay == null || !stay.isOpen()) {
            throw new ValidationException("Choose a guest who is checked in.");
        }
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be at least 1.");
        }
        service.setQuantity(quantity);
        InvoiceLine line = service.toInvoiceLine();
        stay.getInvoice().addLine(line);
        service.setQuantity(1);
        store.save();
        return line;
    }

    public double previewCharge(ExtraService service, int quantity) {
        service.setQuantity(quantity);
        double charge = service.calculateCharge();
        service.setQuantity(1);
        return charge;
    }

    public Payment createPayment(String method, double amount, String reference) throws ValidationException {
        if (method.equals("Cash")) {
            double received;
            try {
                received = reference == null || reference.isBlank() ? amount : Double.parseDouble(reference.replace(",", ""));
            } catch (NumberFormatException e) {
                throw new ValidationException("Enter the cash received as a number.");
            }
            return new CashPayment(amount, received);
        }
        if (method.equals("Transfer")) {
            return new TransferPayment(amount, reference);
        }
        if (method.equals("Card")) {
            return new CardPayment(amount, reference);
        }
        throw new ValidationException("Choose a payment method.");
    }

    public void pay(Invoice invoice, Payment payment) throws ValidationException {
        if (payment.getAmount() <= 0) {
            throw new ValidationException("The amount must be more than zero.");
        }
        if (payment.getAmount() > invoice.outstanding() + 0.009) {
            throw new ValidationException("The amount is more than the outstanding balance of " + String.format("%,.2f", invoice.outstanding()) + ".");
        }
        if (!payment.processPayment()) {
            throw new ValidationException(failureMessage(payment));
        }
        invoice.addPayment(payment);
        store.save();
    }

    public String failureMessage(Payment payment) {
        if (payment instanceof CashPayment) {
            return "The cash received is less than the amount.";
        }
        if (payment instanceof TransferPayment) {
            return "Enter the bank reference from the transfer slip.";
        }
        return "Enter the last 4 digits of the card.";
    }

    public List<Invoice> invoicesByOutstanding() {
        return new MergeSorter<Invoice>((a, b) -> Double.compare(b.outstanding(), a.outstanding())).sort(store.getInvoices());
    }

    public List<Invoice> openInvoices() {
        List<Invoice> result = new ArrayList<>();
        for (Invoice invoice : invoicesByOutstanding()) {
            if (!invoice.isPaid()) {
                result.add(invoice);
            }
        }
        return result;
    }

    public double totalOutstanding() {
        double sum = 0;
        for (Invoice invoice : store.getInvoices()) {
            sum += invoice.outstanding();
        }
        return sum;
    }

    public Invoice invoiceOf(Booking booking) {
        Stay stay = store.findStay(booking);
        return stay == null ? null : stay.getInvoice();
    }

}
