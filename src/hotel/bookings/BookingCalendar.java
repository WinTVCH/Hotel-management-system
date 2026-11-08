package hotel.bookings;

import hotel.rooms.Room;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingCalendar {
    private List<Booking> bookings = new ArrayList<>();

    public List<Booking> getBookings() {
        return bookings;
    }

    public void add(Booking booking) {
        bookings.add(booking);
    }

    public void remove(Booking booking) {
        bookings.remove(booking);
    }

    public boolean isFree(Room room, LocalDate from, LocalDate to) {
        for (Booking booking : bookings) {
            if (booking.isActive() && booking.getRooms().contains(room) && booking.overlaps(from, to)) {
                return false;
            }
        }
        return true;
    }

    public List<Booking> arrivalsOn(LocalDate day) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCheckIn().equals(day) && booking.getStatus() != BookingStatus.CANCELLED) {
                result.add(booking);
            }
        }
        return result;
    }

    public List<Booking> departuresOn(LocalDate day) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCheckOut().equals(day) && booking.isActive()) {
                result.add(booking);
            }
        }
        return result;
    }

    public int occupiedRoomsOn(LocalDate day) {
        int count = 0;
        for (Booking booking : bookings) {
            boolean stayed = booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.CHECKED_OUT;
            if (stayed && booking.overlaps(day, day.plusDays(1))) {
                count += booking.getRooms().size();
            }
        }
        return count;
    }

}
