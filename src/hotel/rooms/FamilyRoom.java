package hotel.rooms;

public class FamilyRoom extends Room {
    public StandardRoom(String RoomNumber, double Price) {
        super(RoomNumber, Price);
    }

    @Override
    public double calculateRate() {
        return getPrice();
    }
}