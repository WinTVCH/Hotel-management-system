package hotel.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hotel.bookings.Booking;
import hotel.bookings.Stay;
import hotel.finance.Invoice;
import hotel.finance.InvoiceLine;

public class StayImporter extends JsonImporter<Stay> {

    public StayImporter(DataStore store) {
        super("stays.json", store);
    }

    @Override
    protected Stay parseItem(JsonObject item) {
        Booking booking = store.findBooking(text(item, "bookingCode"));
        if (booking == null) {
            throw new IllegalArgumentException("Unknown booking " + text(item, "bookingCode"));
        }
        Stay stay = new Stay(booking, dateTime(item, "checkedInAt"));
        stay.setCheckedOutAt(dateTime(item, "checkedOutAt"));
        if (item.has("invoice")) {
            JsonObject data = item.getAsJsonObject("invoice");
            Invoice invoice = new Invoice(text(data, "number"), stay);
            for (JsonElement element : data.getAsJsonArray("lines")) {
                JsonObject line = element.getAsJsonObject();
                invoice.addLine(new InvoiceLine(text(line, "item"), integer(line, "quantity", 1), number(line, "unitPrice", 0), number(line, "amount")));
            }
            if (data.has("payments")) {
                for (JsonElement element : data.getAsJsonArray("payments")) {
                    invoice.addPayment(payment(element.getAsJsonObject()));
                }
            }
            stay.setInvoice(invoice);
        }
        return stay;
    }

    @Override
    protected void addToStore(Stay item) {
        store.addStay(item);
    }

}
