package hotel.rooms;

public abstract class Room {
    private String RoomNumber;
    private double Price;

    public Room(String RoomNumber, double Price) {
        this.RoomNumber = RoomNumber
        this.Price = Price
    }

    public String getRoomNumber() {
        return this.RoomNumber;
    }

    protected double getPrice() {
        return this.Price;
    }

    public abstract double calculateRate();
}