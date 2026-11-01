package hotel.people;

public class Guest extends Person {
    
    private String roomNumber;
    private int guestId;

    public Guest(String name, String phone, String email, String roomNumber, int guestId){
        super(name, phone, email);
        this.roomNumber = roomNumber;
        this.guestId = guestId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }



}       
