package hotel.housekeeping;

import hotel.people.Staff;
import hotel.rooms.Room;
import java.time.LocalDateTime;

public class HousekeepingTask implements Comparable<HousekeepingTask> {
    private int id;
    private Room room;
    private String job;
    private TaskPriority priority;
    private Staff assignee;
    private boolean done;
    private LocalDateTime createdAt;

    public HousekeepingTask(int id, Room room, String job, TaskPriority priority, LocalDateTime createdAt) {
        this.id = id;
        this.room = room;
        this.job = job;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public String getJob() {
        return job;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Staff getAssignee() {
        return assignee;
    }

    public void setAssignee(Staff assignee) {
        this.assignee = assignee;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAssigneeName() {
        return assignee == null ? "(none)" : assignee.getName();
    }

    @Override
    public int compareTo(HousekeepingTask other) {
        int byPriority = Integer.compare(priority.ordinal(), other.priority.ordinal());
        if (byPriority != 0) {
            return byPriority;
        }
        return other.createdAt.compareTo(createdAt);
    }

}
