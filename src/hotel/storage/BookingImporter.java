package hotel.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hotel.bookings.Booking;
import hotel.bookings.BookingChannel;
import hotel.bookings.BookingStatus;
import hotel.bookings.GroupBooking;
import hotel.people.Guest;
import hotel.rooms.Room;
import java.time.LocalDate;

public class BookingImporter extends JsonImporter<Booking> {

    public BookingImporter(DataStore store) {
        super("bookings.json", store);
    }

    @Override
    protected Booking parseItem(JsonObject item) {
        String code = text(item, "code");
        Guest guest = store.findGuestById(integer(item, "guestId"));
        if (guest == null) {
            throw new IllegalArgumentException("Unknown guest for " + code);
        }
        LocalDate checkIn = date(item, "checkIn");
        LocalDate checkOut = date(item, "checkOut");
        BookingChannel channel = BookingChannel.valueOf(text(item, "channel", "WALK_IN"));
        Booking booking;
        String groupName = text(item, "groupName", null);
        if (groupName != null) {
            GroupBooking group = new GroupBooking(code, guest, checkIn, checkOut, channel, groupName);
            if (item.has("members")) {
                for (JsonElement element : item.getAsJsonArray("members")) {
                    Guest member = store.findGuestById(element.getAsInt());
                    if (member != null) {
                        group.addMember(member);
                    }
                }
            }
            booking = group;
        } else {
            booking = new Booking(code, guest, checkIn, checkOut, channel);
        }
        for (JsonElement element : item.getAsJsonArray("rooms")) {
            Room room = store.findRoom(element.getAsString());
            if (room == null) {
                throw new IllegalArgumentException("Unknown room " + element.getAsString());
            }
            booking.addRoom(room);
        }
        booking.setStatus(BookingStatus.valueOf(text(item, "status", "CONFIRMED")));
        String promo = text(item, "promotion", null);
        if (promo != null) {
            booking.setPromotion(store.findPromotion(promo));
        }
        booking.setRequests(text(item, "requests", ""));
        if (item.has("deposit") && !item.get("deposit").isJsonNull()) {
            booking.setDeposit(payment(item.getAsJsonObject("deposit")));
        }
        return booking;
    }

    @Override
    protected void addToStore(Booking item) {
        store.addBooking(item);
    }

}
