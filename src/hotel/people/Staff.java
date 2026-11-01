package hotel.people;

public class Staff extends Person {
    
    private double salary;
    private String shift;
    private int staffId;
    private Role role;

    public Staff(String name, String phone, String email, double salary, String shift, int staffId) {
        super(name,phone,email);
        this.salary = salary;
        this.shift = shift;
        this.staffId = staffId;
    }

    public double getSalary() {
        return salary;
    }

    public String getShift() {
        return shift;
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
    
    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }




}
