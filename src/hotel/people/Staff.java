package hotel.people;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Staff extends Person {
    private double salary;
    private String shift;
    private int staffId;
    private List<Role> roles = new ArrayList<>();

    public Staff(String name, String phone, String email, double salary, String shift, int staffId) {
        super(name, phone, email);
        this.salary = salary;
        this.shift = shift;
        this.staffId = staffId;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public Role getMainRole() {
        Role best = null;
        for (Role role : roles) {
            if (best == null || role.getRank() > best.getRank()) {
                best = role;
            }
        }
        return best;
    }

    public boolean hasRole(String name) {
        for (Role role : roles) {
            if (role.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getMenuItems() {
        Set<String> items = new LinkedHashSet<>();
        for (Role role : roles) {
            items.addAll(role.getMenuItems());
        }
        return new ArrayList<>(items);
    }

    @Override
    public String getCode() {
        return String.format("S-%03d", staffId);
    }

}
