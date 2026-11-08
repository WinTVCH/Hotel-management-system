package hotel.bookings;

import hotel.finance.Invoice;
import java.time.LocalDateTime;

public class Stay {
    private Booking booking;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
    private Invoice invoice;

    public Stay(Booking booking, LocalDateTime checkedInAt) {
        this.booking = booking;
        this.checkedInAt = checkedInAt;
    }

    public Booking getBooking() {
        return booking;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public LocalDateTime getCheckedOutAt() {
        return checkedOutAt;
    }

    public void setCheckedOutAt(LocalDateTime checkedOutAt) {
        this.checkedOutAt = checkedOutAt;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public boolean isOpen() {
        return checkedOutAt == null;
    }

    public void close(LocalDateTime time) {
        checkedOutAt = time;
    }

    @Override
    public String toString() {
        return "Room " + booking.getRoomNumbers() + " - " + booking.getDisplayName();
    }

}
