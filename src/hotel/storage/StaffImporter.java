package hotel.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hotel.people.Housekeeper;
import hotel.people.Manager;
import hotel.people.Receptionist;
import hotel.people.Role;
import hotel.people.Staff;
import hotel.people.TourGuide;

public class StaffImporter extends JsonImporter<Staff> {

    public StaffImporter(DataStore store) {
        super("staff.json", store);
    }

    @Override
    protected Staff parseItem(JsonObject item) {
        Staff staff = new Staff(text(item, "name"), text(item, "phone", ""), text(item, "email", ""), number(item, "salary", 0), text(item, "shift", ""), integer(item, "staffId"));
        if (item.has("roles")) {
            for (JsonElement element : item.getAsJsonArray("roles")) {
                JsonObject role = element.getAsJsonObject();
                staff.addRole(makeRole(text(role, "type"), text(role, "roleId", ""), integer(role, "rank", 1)));
            }
        }
        return staff;
    }

    private Role makeRole(String type, String roleId, int rank) {
        switch (type) {
            case "Receptionist":
                return new Receptionist(roleId, rank);
            case "Housekeeper":
                return new Housekeeper(roleId, rank);
            case "Manager":
                return new Manager(roleId, rank);
            case "TourGuide":
                return new TourGuide(roleId, rank);
            default:
                return new Role(type, roleId, rank);
        }
    }

    @Override
    protected void addToStore(Staff item) {
        store.addStaff(item);
    }

}
