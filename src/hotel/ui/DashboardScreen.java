package hotel.ui;

import hotel.bookings.Booking;
import hotel.bookings.BookingStatus;
import hotel.errors.ValidationException;
import hotel.housekeeping.HousekeepingTask;
import hotel.people.Staff;
import hotel.rooms.RoomStatus;
import hotel.storage.DataStore;
import java.time.LocalDate;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DashboardScreen extends Screen {

    public DashboardScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "Today";
    }

    @Override
    public Node build() {
        DataStore store = app.getStore();
        LocalDate today = app.getSession().today();
        List<Booking> arrivals = app.getBookings().arrivals(today);
        int checkedIn = 0;
        for (Booking booking : arrivals) {
            if (booking.getStatus() == BookingStatus.CHECKED_IN) {
                checkedIn++;
            }
        }
        int total = store.getProperty().getRooms().size();
        int occupied = store.getProperty().countByStatus(RoomStatus.OCCUPIED);
        GridPane summary = form();
        summary.setHgap(24);
        summary.add(new Label("Rooms occupied:"), 0, 0);
        summary.add(bold(occupied + " of " + total + " (" + (total == 0 ? 0 : occupied * 100 / total) + "%)"), 1, 0);
        summary.add(new Label("Arrivals today:"), 2, 0);
        summary.add(bold(arrivals.size() + " (" + checkedIn + " checked in)"), 3, 0);
        summary.add(new Label("Departures today:"), 0, 1);
        summary.add(bold(String.valueOf(app.getBookings().departures(today).size())), 1, 1);
        summary.add(new Label("Unpaid balances:"), 2, 1);
        summary.add(bold(money(app.getBilling().totalOutstanding()) + " THB"), 3, 1);

        TableView<Booking> arrivalTable = new TableView<>();
        arrivalTable.getColumns().add(column("Code", Booking::getCode, 90));
        arrivalTable.getColumns().add(column("Guest Name", Booking::getDisplayName, 180));
        arrivalTable.getColumns().add(column("Room", b -> b.getRoomNumbers() + " " + b.getRoomTypes(), 150));
        arrivalTable.getColumns().add(column("Nights", Booking::nights, 60));
        arrivalTable.getColumns().add(column("Status", b -> b.getStatus().name().replace('_', ' '), 100));
        arrivalTable.getItems().addAll(arrivals);
        arrivalTable.setPrefHeight(220);
        arrivalTable.setPlaceholder(new Label("No arrivals today"));
        Button checkIn = new Button("Check In");
        checkIn.setDefaultButton(true);
        checkIn.setOnAction(e -> {
            Booking booking = arrivalTable.getSelectionModel().getSelectedItem();
            try {
                app.getBookings().checkIn(booking, today);
                app.info("Checked in", booking.getDisplayName() + " is checked in to room " + booking.getRoomNumbers() + ".");
                app.refresh();
            } catch (ValidationException ex) {
                app.error(ex.getMessage());
            }
        });
        Button openBookings = new Button("Open Bookings");
        openBookings.setOnAction(e -> app.show("Bookings"));
        boolean frontDesk = app.getSession().getMenuItems().contains("Bookings");
        checkIn.setVisible(frontDesk);
        openBookings.setVisible(frontDesk);
        VBox arrivalCard = card("Arrivals Today", arrivalTable, row(checkIn, openBookings));

        TableView<HousekeepingTask> taskTable = new TableView<>();
        taskTable.getColumns().add(column("Priority", HousekeepingTask::getPriority, 80));
        taskTable.getColumns().add(column("Room", t -> t.getRoom().getRoomNumber(), 60));
        taskTable.getColumns().add(column("Job", HousekeepingTask::getJob, 190));
        taskTable.getColumns().add(column("Assigned", HousekeepingTask::getAssigneeName, 100));
        taskTable.getItems().addAll(app.getHousekeeping().openTasks());
        taskTable.setPrefHeight(220);
        taskTable.setPlaceholder(new Label("Nothing to clean"));
        Button assign = new Button("Assign...");
        assign.setOnAction(e -> {
            HousekeepingTask task = taskTable.getSelectionModel().getSelectedItem();
            if (task == null) {
                app.error("Choose a task first.");
                return;
            }
            List<Staff> housekeepers = app.getHousekeeping().housekeepers();
            if (housekeepers.isEmpty()) {
                app.error("There are no housekeepers on the staff list.");
                return;
            }
            ChoiceDialog<Staff> dialog = new ChoiceDialog<>(housekeepers.get(0), housekeepers);
            dialog.setTitle("Assign Task");
            dialog.setHeaderText("Room " + task.getRoom().getRoomNumber() + ": " + task.getJob());
            dialog.setContentText("Housekeeper:");
            dialog.showAndWait().ifPresent(staff -> {
                app.getHousekeeping().assign(task, staff);
                app.refresh();
            });
        });
        Button done = new Button("Mark Done");
        done.setOnAction(e -> {
            HousekeepingTask task = taskTable.getSelectionModel().getSelectedItem();
            if (task == null) {
                app.error("Choose a task first.");
                return;
            }
            app.getHousekeeping().markDone(task);
            app.refresh();
        });
        HousekeepingTask next = app.getHousekeeping().nextTask();
        Label nextLabel = caption(next == null ? "No open tasks." : "Next: room " + next.getRoom().getRoomNumber() + ", " + next.getJob());
        VBox taskCard = card("Housekeeping Queue (by priority)", taskTable, nextLabel, row(assign, done));

        HBox middle = new HBox(12, arrivalCard, taskCard);
        HBox.setHgrow(arrivalCard, Priority.ALWAYS);
        HBox.setHgrow(taskCard, Priority.ALWAYS);

        HBox reportButtons = row();
        for (String name : new String[] {"Occupancy", "Revenue", "Housekeeping", "Guests"}) {
            Button button = new Button(name + "...");
            button.setOnAction(e -> app.showReport(name));
            reportButtons.getChildren().add(button);
        }
        VBox root = new VBox(12, card("Summary for " + today, summary), middle);
        if (app.getSession().getMenuItems().contains("Bookings")) {
            root.getChildren().add(card("Reports", reportButtons));
        }
        root.setPadding(new Insets(14));
        return root;
    }

    private Label bold(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

}
