package hotel.structures;

import hotel.bookings.BookingCalendar;
import hotel.rooms.Property;
import hotel.rooms.Room;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityIndex {
    private Property property;
    private BookingCalendar calendar;

    public AvailabilityIndex(Property property, BookingCalendar calendar) {
        this.property = property;
        this.calendar = calendar;
    }

    public List<Room> freeRooms(LocalDate from, LocalDate to) {
        List<Room> result = new ArrayList<>();
        for (Room room : property.getRooms()) {
            if (room.isBookable() && calendar.isFree(room, from, to)) {
                result.add(room);
            }
        }
        return result;
    }

    public List<Room> freeRooms(LocalDate from, LocalDate to, int guests) {
        List<Room> result = new ArrayList<>();
        for (Room room : freeRooms(from, to)) {
            if (room.getCapacity() >= guests) {
                result.add(room);
            }
        }
        return result;
    }

    public boolean isFree(Room room, LocalDate from, LocalDate to) {
        return room.isBookable() && calendar.isFree(room, from, to);
    }

}
