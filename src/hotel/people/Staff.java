package hotel.people;

public class Staff extends Person {
    
    private double salary;
    private String shift,position;
    private int staffId;

    public Staff(String name, String phone, String email, double salary, String shift, String position, int staffId) {
        super(name,phone,email);
        this.salary = salary;
        this.shift = shift;
        this.position = position;
        this.staffId = staffId;
    }

    public double getSalary() {
        return salary;
    }

    public String getShift() {
        return shift;
    }

    public String getPosition() {
        return position;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }



}
