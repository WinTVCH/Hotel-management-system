package hotel.people;

import hotel.bookings.Stay;
import java.util.ArrayList;
import java.util.List;

public class Guest extends Person {
    private int guestId;
    private String idNumber;
    private List<Stay> stays = new ArrayList<>();

    public Guest(String name, String phone, String email, int guestId, String idNumber) {
        super(name, phone, email);
        this.guestId = guestId;
        this.idNumber = idNumber;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public List<Stay> getStays() {
        return stays;
    }

    public void addStay(Stay stay) {
        if (!stays.contains(stay)) {
            stays.add(stay);
        }
    }

    @Override
    public String getCode() {
        return String.format("G-%04d", guestId);
    }

}
