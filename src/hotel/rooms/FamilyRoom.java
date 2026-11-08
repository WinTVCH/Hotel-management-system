package hotel.rooms;

public class FamilyRoom extends Room {
    private int extraBeds;

    public FamilyRoom(String roomNumber, double price, int extraBeds) {
        super(roomNumber, price);
        this.extraBeds = extraBeds;
    }

    public int getExtraBeds() {
        return extraBeds;
    }

    public void setExtraBeds(int extraBeds) {
        this.extraBeds = extraBeds;
    }

    @Override
    public double calculateRate() {
        return getPrice() + extraBeds * 300;
    }

    @Override
    public String getTypeName() {
        return "Family";
    }

    @Override
    public int getCapacity() {
        return 4 + extraBeds;
    }

}
