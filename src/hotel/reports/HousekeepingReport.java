package hotel.reports;

import hotel.housekeeping.HousekeepingTask;
import hotel.housekeeping.SupplyItem;
import hotel.storage.DataStore;
import hotel.structures.TaskHeap;
import java.time.LocalDate;

public class HousekeepingReport extends Report {

    public HousekeepingReport(DataStore store, LocalDate from, LocalDate to) {
        super("Housekeeping", store, from, to);
    }

    @Override
    public String render() {
        StringBuilder text = new StringBuilder(header());
        TaskHeap open = new TaskHeap();
        int done = 0;
        for (HousekeepingTask task : store.getTasks()) {
            if (task.isDone()) {
                done++;
            } else {
                open.push(task);
            }
        }
        text.append("Open tasks, most urgent first: ").append(open.size()).append("\n");
        for (HousekeepingTask task : open.toOrderedList()) {
            text.append(String.format("  %-7s %-5s %-32s %s%n", task.getPriority(), task.getRoom().getRoomNumber(), task.getJob(), task.getAssigneeName()));
        }
        text.append("Finished tasks: ").append(done).append("\n\n");
        text.append("Supplies to reorder\n");
        if (store.getInventory().lowStock().isEmpty()) {
            text.append("  (none)\n");
        }
        for (SupplyItem item : store.getInventory().lowStock()) {
            text.append(String.format("  %-28s %4d left (reorder at %d)%n", item.getName(), item.getQuantity(), item.getReorderLevel()));
        }
        return text.toString();
    }

}
