package hotel.storage;

import com.google.gson.JsonObject;
import hotel.housekeeping.HousekeepingTask;
import hotel.housekeeping.TaskPriority;
import hotel.rooms.Room;

public class HousekeepingTaskImporter extends JsonImporter<HousekeepingTask> {

    public HousekeepingTaskImporter(DataStore store) {
        super("housekeeping_tasks.json", store);
    }

    @Override
    protected HousekeepingTask parseItem(JsonObject item) {
        Room room = store.findRoom(text(item, "room"));
        if (room == null) {
            throw new IllegalArgumentException("Unknown room " + text(item, "room"));
        }
        HousekeepingTask task = new HousekeepingTask(integer(item, "id"), room, text(item, "job"), TaskPriority.valueOf(text(item, "priority", "NORMAL")), dateTime(item, "createdAt"));
        task.setAssignee(store.findStaff(integer(item, "assignee", 0)));
        task.setDone(flag(item, "done", false));
        return task;
    }

    @Override
    protected void addToStore(HousekeepingTask item) {
        store.addTask(item);
    }

}
