package hotel.rooms;

import java.time.LocalDate;

public class RoomRate {
    private String name;
    private String roomType;
    private LocalDate from;
    private LocalDate to;
    private double percent;

    public RoomRate(String name, String roomType, LocalDate from, LocalDate to, double percent) {
        this.name = name;
        this.roomType = roomType;
        this.from = from;
        this.to = to;
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public String getRoomType() {
        return roomType;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public double getPercent() {
        return percent;
    }

    public boolean appliesTo(Room room, LocalDate night) {
        boolean typeMatches = roomType.equals("All") || roomType.equals(room.getTypeName());
        return typeMatches && !night.isBefore(from) && !night.isAfter(to);
    }

    public double apply(double nightly) {
        return nightly * (1 + percent / 100.0);
    }

}
