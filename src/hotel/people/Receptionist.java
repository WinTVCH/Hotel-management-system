package hotel.people;

public class Receptionist extends Staff{
    
    private String deskNumber;

    public Receptionist(String name, String phone, String email, double salary, String shift, String position, int staffId, String deskNumber) {
        super(name, phone, email, salary, shift, position, staffId);
        this.deskNumber = deskNumber;
    }

    public String getDeskNumber() {
        return deskNumber;
    }

    public void setDeskNumber(String deskNumber) {
        this.deskNumber = deskNumber;
    }






}




