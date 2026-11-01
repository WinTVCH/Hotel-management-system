package hotel.rooms;

public class StandardRoom extends Room {
    public StandardRoom(String RoomNumber,double Price) {
        super(RoomNumber, Price);
    }

    @Override
    public double calculateRate() {
        return getPrice();
    }
}