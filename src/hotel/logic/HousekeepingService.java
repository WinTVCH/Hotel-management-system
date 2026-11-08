package hotel.logic;

import hotel.bookings.Stay;
import hotel.errors.ValidationException;
import hotel.housekeeping.HousekeepingTask;
import hotel.housekeeping.TaskPriority;
import hotel.people.Staff;
import hotel.rooms.Room;
import hotel.rooms.RoomStatus;
import hotel.storage.DataStore;
import hotel.structures.TaskHeap;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HousekeepingService {
    private DataStore store;

    public HousekeepingService(DataStore store) {
        this.store = store;
    }

    public TaskHeap queue() {
        TaskHeap heap = new TaskHeap();
        for (HousekeepingTask task : store.getTasks()) {
            if (!task.isDone()) {
                heap.push(task);
            }
        }
        return heap;
    }

    public List<HousekeepingTask> openTasks() {
        return queue().toOrderedList();
    }

    public HousekeepingTask nextTask() {
        return queue().peek();
    }

    public HousekeepingTask createTask(Room room, String job, TaskPriority priority) throws ValidationException {
        if (room == null) {
            throw new ValidationException("Choose a room.");
        }
        if (job == null || job.isBlank()) {
            throw new ValidationException("Describe the job.");
        }
        HousekeepingTask task = new HousekeepingTask(store.nextTaskId(), room, job.trim(), priority, LocalDateTime.now());
        store.addTask(task);
        store.save();
        return task;
    }

    public void assign(HousekeepingTask task, Staff staff) {
        task.setAssignee(staff);
        store.save();
    }

    public void markDone(HousekeepingTask task) {
        task.setDone(true);
        store.getInventory().use("Cleaning kit", 1);
        Room room = task.getRoom();
        if (room.getStatus() == RoomStatus.DIRTY && !hasOpenTask(room)) {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        store.save();
    }

    public boolean hasOpenTask(Room room) {
        for (HousekeepingTask task : store.getTasks()) {
            if (!task.isDone() && task.getRoom() == room) {
                return true;
            }
        }
        return false;
    }

    public List<Staff> housekeepers() {
        List<Staff> result = new ArrayList<>();
        for (Staff staff : store.getStaff()) {
            if (staff.hasRole("Housekeeper")) {
                result.add(staff);
            }
        }
        return result;
    }

    public void setRoomStatus(Room room, RoomStatus status) throws ValidationException {
        boolean guestInside = false;
        for (Stay stay : store.openStays()) {
            if (stay.getBooking().getRooms().contains(room)) {
                guestInside = true;
            }
        }
        if (guestInside && status != RoomStatus.OCCUPIED) {
            throw new ValidationException("A guest is checked in to room " + room.getRoomNumber() + ". Check them out first.");
        }
        if (!guestInside && status == RoomStatus.OCCUPIED) {
            throw new ValidationException("Rooms become occupied when a guest checks in.");
        }
        room.setStatus(status);
        store.save();
    }

}
