package hotel.reports;

import hotel.bookings.Booking;
import hotel.finance.Invoice;
import hotel.finance.InvoiceLine;
import hotel.finance.Payment;
import hotel.storage.DataStore;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class RevenueReport extends Report {

    public RevenueReport(DataStore store, LocalDate from, LocalDate to) {
        super("Revenue", store, from, to);
    }

    private boolean inPeriod(Payment payment) {
        if (payment.getPaidAt() == null) {
            return false;
        }
        LocalDate day = payment.getPaidAt().toLocalDate();
        return !day.isBefore(getFrom()) && !day.isAfter(getTo());
    }

    @Override
    public String render() {
        StringBuilder text = new StringBuilder(header());
        Map<String, Double> byMethod = new LinkedHashMap<>();
        byMethod.put("Cash", 0.0);
        byMethod.put("Transfer", 0.0);
        byMethod.put("Card", 0.0);
        double rooms = 0;
        double services = 0;
        double discounts = 0;
        for (Invoice invoice : store.getInvoices()) {
            for (InvoiceLine line : invoice.getLines()) {
                if (line.getAmount() < 0) {
                    discounts += line.getAmount();
                } else if (line.getItem().startsWith("Room ")) {
                    rooms += line.getAmount();
                } else {
                    services += line.getAmount();
                }
            }
            for (Payment payment : invoice.getPayments()) {
                if (inPeriod(payment)) {
                    byMethod.merge(payment.getMethod(), payment.getAmount(), Double::sum);
                }
            }
        }
        for (Booking booking : store.getBookings()) {
            Payment deposit = booking.getDeposit();
            if (deposit != null && inPeriod(deposit) && store.findStay(booking) == null) {
                byMethod.merge(deposit.getMethod(), deposit.getAmount(), Double::sum);
            }
        }
        text.append("Charged on invoices\n");
        text.append(String.format("  %-24s %14s%n", "Rooms", money(rooms)));
        text.append(String.format("  %-24s %14s%n", "Extra services", money(services)));
        text.append(String.format("  %-24s %14s%n", "Discounts", money(discounts)));
        text.append("\nPayments received\n");
        double total = 0;
        for (Map.Entry<String, Double> entry : byMethod.entrySet()) {
            text.append(String.format("  %-24s %14s%n", entry.getKey(), money(entry.getValue())));
            total += entry.getValue();
        }
        text.append("-".repeat(56)).append("\n");
        text.append(String.format("  %-24s %14s%n", "Total received (THB)", money(total)));
        return text.toString();
    }

}
