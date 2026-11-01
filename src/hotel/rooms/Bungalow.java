package hotel.rooms;

public class Bungalow extends Room {
    private double CleaningFee;

    public StandardRoom(String RoomNumber, double Price,double CleaningFee) {
        super(RoomNumber, Price);
        this.CleaningFee = CleaningFee;
    }

    public void setCleaningFee(double CleaningFee) {
        this.CleaningFee = CleaningFee;
    }

    @Override
    public double calculateRate() {
        return getPrice() + CleaningFee;
    }
}