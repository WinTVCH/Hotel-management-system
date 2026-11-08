package hotel.rooms;

public class StandardRoom extends Room {

    public StandardRoom(String roomNumber, double price) {
        super(roomNumber, price);
    }

    @Override
    public double calculateRate() {
        return getPrice();
    }

    @Override
    public String getTypeName() {
        return "Standard";
    }

    @Override
    public int getCapacity() {
        return 2;
    }

}
