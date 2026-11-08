package hotel.ui;

import hotel.bookings.Booking;
import hotel.bookings.Stay;
import hotel.errors.ValidationException;
import hotel.housekeeping.TaskPriority;
import hotel.rooms.Room;
import hotel.rooms.RoomStatus;
import hotel.structures.MergeSorter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class RoomScreen extends Screen {
    private TilePane rack = new TilePane();
    private ToggleGroup typeGroup = new ToggleGroup();
    private ComboBox<String> sort = new ComboBox<>();
    private Label selectedLabel = caption("Click a room to select it.");
    private Room selected;

    public RoomScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "Room Rack";
    }

    @Override
    public Node build() {
        HBox types = row();
        for (String type : new String[] {"All", "Standard", "Family", "Bungalow"}) {
            RadioButton button = new RadioButton(type);
            button.setUserData(type);
            button.setToggleGroup(typeGroup);
            button.setSelected(type.equals("All"));
            types.getChildren().add(button);
        }
        typeGroup.selectedToggleProperty().addListener((obs, old, value) -> loadRack());
        HBox legend = row();
        for (RoomStatus status : RoomStatus.values()) {
            Region swatch = new Region();
            swatch.setPrefSize(12, 12);
            swatch.setStyle("-fx-background-color: " + color(status) + "; -fx-border-color: #8a8a8a;");
            legend.getChildren().addAll(swatch, new Label(label(status) + "   "));
        }
        sort.getItems().addAll("Rate, low to high", "Room number");
        sort.getSelectionModel().selectFirst();
        sort.setOnAction(e -> loadRack());
        HBox filters = new HBox(12, card("Room type", types), card("Legend", legend), card("Sort", sort));
        rack.setHgap(8);
        rack.setVgap(8);
        rack.setPrefColumns(6);
        loadRack();
        Button setStatus = new Button("Set Status...");
        setStatus.setOnAction(e -> changeStatus());
        Button newTask = new Button("New Task...");
        newTask.setOnAction(e -> addTask());
        VBox root = new VBox(12, filters, card("Rooms - " + app.getStore().getProperty().getName(), rack, selectedLabel), row(setStatus, newTask));
        root.setPadding(new Insets(14));
        return root;
    }

    private void loadRack() {
        String type = typeGroup.getSelectedToggle() == null ? "All" : (String) typeGroup.getSelectedToggle().getUserData();
        List<Room> rooms = app.getStore().getProperty().roomsOfType(type.equals("All") ? null : type);
        MergeSorter<Room> sorter;
        if ("Room number".equals(sort.getValue())) {
            sorter = new MergeSorter<>((a, b) -> a.getRoomNumber().compareTo(b.getRoomNumber()));
        } else {
            sorter = new MergeSorter<>((a, b) -> Double.compare(a.calculateRate(), b.calculateRate()));
        }
        rack.getChildren().clear();
        for (Room room : sorter.sort(rooms)) {
            rack.getChildren().add(cell(room));
        }
        app.setStatus(rooms.size() + " rooms shown");
    }

    private Button cell(Room room) {
        Label number = new Label(room.getRoomNumber());
        number.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        VBox content = new VBox(2, number, new Label(room.getTypeName() + " - " + money(room.calculateRate())), new Label(label(room.getStatus())), caption(note(room)));
        content.setAlignment(Pos.TOP_LEFT);
        Button button = new Button();
        button.setGraphic(content);
        button.setPrefSize(160, 92);
        button.setAlignment(Pos.TOP_LEFT);
        String border = room == selected ? "#0096c9" : "#c8c8c8";
        button.setStyle("-fx-background-color: " + color(room.getStatus()) + "; -fx-border-color: " + border + "; -fx-border-width: " + (room == selected ? 2 : 1) + "; -fx-border-radius: 4; -fx-background-radius: 4;");
        button.setOnAction(e -> {
            selected = room;
            selectedLabel.setText("Selected: room " + room.getRoomNumber() + " (" + label(room.getStatus()) + ")");
            loadRack();
        });
        return button;
    }

    private String note(Room room) {
        for (Stay stay : app.getStore().openStays()) {
            if (stay.getBooking().getRooms().contains(room)) {
                return stay.getBooking().getDisplayName() + ", dep. " + stay.getBooking().getCheckOut();
            }
        }
        for (Booking booking : app.getBookings().arrivals(app.getSession().today())) {
            if (booking.isActive() && booking.getRooms().contains(room)) {
                return "Arrival: " + booking.getDisplayName();
            }
        }
        if (app.getHousekeeping().hasOpenTask(room)) {
            return "Cleaning task open";
        }
        return "-";
    }

    private String color(RoomStatus status) {
        switch (status) {
            case OCCUPIED:
                return "#e1ecfa";
            case DIRTY:
                return "#fcf1d2";
            case OUT_OF_SERVICE:
                return "#e4e4e4";
            default:
                return "#e3f5e1";
        }
    }

    private String label(RoomStatus status) {
        switch (status) {
            case OCCUPIED:
                return "Occupied";
            case DIRTY:
                return "Vacant dirty";
            case OUT_OF_SERVICE:
                return "Out of order";
            default:
                return "Vacant clean";
        }
    }

    private void changeStatus() {
        if (selected == null) {
            app.error("Click a room first.");
            return;
        }
        ChoiceDialog<RoomStatus> dialog = new ChoiceDialog<>(selected.getStatus(), RoomStatus.values());
        dialog.setTitle("Set Status");
        dialog.setHeaderText("Room " + selected.getRoomNumber());
        dialog.setContentText("Status:");
        dialog.showAndWait().ifPresent(status -> {
            try {
                app.getHousekeeping().setRoomStatus(selected, status);
                loadRack();
            } catch (ValidationException ex) {
                app.error(ex.getMessage());
            }
        });
    }

    private void addTask() {
        if (selected == null) {
            app.error("Click a room first.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Task");
        dialog.setHeaderText("Housekeeping task for room " + selected.getRoomNumber());
        TextField job = new TextField();
        ComboBox<TaskPriority> priority = new ComboBox<>();
        priority.getItems().addAll(TaskPriority.values());
        priority.setValue(TaskPriority.NORMAL);
        GridPane grid = form();
        addRow(grid, 0, "Job:", job);
        addRow(grid, 1, "Priority:", priority);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            app.getHousekeeping().createTask(selected, job.getText(), priority.getValue());
            app.info("Task added", "Room " + selected.getRoomNumber() + ": " + job.getText().trim());
            loadRack();
        } catch (ValidationException ex) {
            app.error(ex.getMessage());
        }
    }

}
