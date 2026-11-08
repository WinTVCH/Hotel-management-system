package hotel.rooms;

public class Bungalow extends Room {
    private double cleaningFee;

    public Bungalow(String roomNumber, double price, double cleaningFee) {
        super(roomNumber, price);
        this.cleaningFee = cleaningFee;
    }

    public double getCleaningFee() {
        return cleaningFee;
    }

    public void setCleaningFee(double cleaningFee) {
        this.cleaningFee = cleaningFee;
    }

    @Override
    public double calculateRate() {
        return getPrice() + cleaningFee;
    }

    @Override
    public String getTypeName() {
        return "Bungalow";
    }

    @Override
    public int getCapacity() {
        return 2;
    }

}
