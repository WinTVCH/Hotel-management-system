package hotel.people;

import java.util.ArrayList;
import java.util.List;

public class Role {
    private String name ,roleId;
    private int rank;

    public Role(String name, String roleId, int rank) {
        this.name = name;
        this.roleId = roleId;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public String getRoleId() {
        return roleId;
    }

    public int getRank() {
        return rank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<String> getMenuItems() {
        List<String> items = new ArrayList<>();
        items.add("Today");
        return items;
    }

    @Override
    public String toString() {
        return name;
    }

}
