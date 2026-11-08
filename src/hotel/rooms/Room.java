package hotel.rooms;

public abstract class Room {
    private String roomNumber;
    private double price;
    private RoomStatus status = RoomStatus.AVAILABLE;

    public Room(String roomNumber, double price) {
        this.roomNumber = roomNumber;
        this.price = price;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public boolean isBookable() {
        return status != RoomStatus.OUT_OF_SERVICE;
    }

    public abstract double calculateRate();

    public abstract String getTypeName();

    public abstract int getCapacity();

    @Override
    public String toString() {
        return roomNumber + " " + getTypeName();
    }

}
