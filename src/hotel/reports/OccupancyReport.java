package hotel.reports;

import hotel.storage.DataStore;
import java.time.LocalDate;

public class OccupancyReport extends Report {

    public OccupancyReport(DataStore store, LocalDate from, LocalDate to) {
        super("Occupancy", store, from, to);
    }

    @Override
    public String render() {
        StringBuilder text = new StringBuilder(header());
        int totalRooms = store.getProperty().getRooms().size();
        int sum = 0;
        int days = 0;
        for (LocalDate day = getFrom(); !day.isAfter(getTo()); day = day.plusDays(1)) {
            int occupied = store.getCalendar().occupiedRoomsOn(day);
            double percent = totalRooms == 0 ? 0 : occupied * 100.0 / totalRooms;
            text.append(String.format("%-12s %3d / %-3d %6.1f%%%n", day, occupied, totalRooms, percent));
            sum += occupied;
            days++;
        }
        double average = days == 0 || totalRooms == 0 ? 0 : sum * 100.0 / (days * totalRooms);
        text.append("-".repeat(56)).append("\n");
        text.append(String.format("Average occupancy: %.1f%%%n", average));
        return text.toString();
    }

}
