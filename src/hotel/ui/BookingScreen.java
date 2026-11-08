package hotel.ui;

import hotel.bookings.Booking;
import hotel.bookings.BookingChannel;
import hotel.errors.BookingConflictException;
import hotel.errors.ValidationException;
import hotel.finance.Invoice;
import hotel.finance.Payment;
import hotel.people.Guest;
import hotel.pricing.Promotion;
import hotel.rooms.Room;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BookingScreen extends Screen {
    private TableView<Booking> bookingTable = new TableView<>();
    private TableView<Room> roomTable = new TableView<>();
    private ComboBox<String> sort = new ComboBox<>();
    private DatePicker checkIn = new DatePicker();
    private DatePicker checkOut = new DatePicker();
    private ComboBox<String> promotion = new ComboBox<>();
    private Label total = new Label();
    private Guest selectedGuest;

    public BookingScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "Bookings";
    }

    @Override
    public Node build() {
        HBox root = new HBox(12, findCard(), newBookingCard());
        root.setPadding(new Insets(14));
        HBox.setHgrow(root.getChildren().get(0), Priority.ALWAYS);
        return root;
    }

    private VBox findCard() {
        TextField code = new TextField();
        code.setPromptText("e.g. BK-24055");
        code.setPrefWidth(130);
        Button find = new Button("Find");
        find.setOnAction(e -> {
            Booking booking = app.getBookings().findByCode(code.getText());
            if (booking == null) {
                app.error("No booking with code " + code.getText().trim() + ".");
                return;
            }
            bookingTable.getSelectionModel().select(booking);
            bookingTable.scrollTo(booking);
            app.setStatus("Found " + booking.getCode());
        });
        sort.getItems().addAll("Check-in date", "Guest name", "Total price");
        sort.getSelectionModel().selectFirst();
        sort.setOnAction(e -> loadBookings());
        bookingTable.getColumns().add(column("Code", Booking::getCode, 80));
        bookingTable.getColumns().add(column("Guest Name", Booking::getDisplayName, 150));
        bookingTable.getColumns().add(column("Check-in", Booking::getCheckIn, 90));
        bookingTable.getColumns().add(column("Check-out", Booking::getCheckOut, 90));
        bookingTable.getColumns().add(column("Room", Booking::getRoomNumbers, 70));
        bookingTable.getColumns().add(column("Channel", b -> b.getChannel().name().replace('_', ' '), 70));
        bookingTable.getColumns().add(column("Status", b -> b.getStatus().name().replace('_', ' '), 90));
        bookingTable.getColumns().add(column("Total", b -> money(b.totalPrice()), 80));
        bookingTable.setPrefHeight(470);
        loadBookings();
        Button checkInButton = new Button("Check In");
        checkInButton.setOnAction(e -> {
            Booking booking = bookingTable.getSelectionModel().getSelectedItem();
            try {
                app.getBookings().checkIn(booking, app.getSession().today());
                app.info("Checked in", booking.getDisplayName() + " is checked in to room " + booking.getRoomNumbers() + ".");
                loadBookings();
            } catch (ValidationException ex) {
                app.error(ex.getMessage());
            }
        });
        Button checkOutButton = new Button("Check Out...");
        checkOutButton.setOnAction(e -> checkOutSelected());
        Button cancel = new Button("Cancel Booking");
        cancel.setOnAction(e -> {
            Booking booking = bookingTable.getSelectionModel().getSelectedItem();
            if (booking == null || !app.confirm("Cancel booking " + booking.getCode() + "?")) {
                return;
            }
            try {
                app.getBookings().cancel(booking, app.getSession().today());
                loadBookings();
            } catch (ValidationException ex) {
                app.error(ex.getMessage());
            }
        });
        return card("Find Booking", row(new Label("Booking code:"), code, find, spacer(), new Label("Sort by:"), sort), bookingTable, row(checkInButton, checkOutButton, cancel));
    }

    private void checkOutSelected() {
        Booking booking = bookingTable.getSelectionModel().getSelectedItem();
        if (booking == null) {
            app.error("Choose a booking.");
            return;
        }
        Invoice invoice = app.getBilling().invoiceOf(booking);
        Payment payment = null;
        if (invoice != null && !invoice.isPaid()) {
            payment = askPayment("Check Out " + booking.getCode(), invoice.outstanding(), true);
            if (payment == null) {
                return;
            }
        }
        try {
            Invoice paid = app.getBookings().checkOut(booking, payment);
            app.info("Payment recorded successfully", booking.getDisplayName() + " is checked out. Invoice " + paid.getNumber() + " is paid in full.");
            loadBookings();
        } catch (ValidationException ex) {
            app.error(ex.getMessage());
        }
    }

    private void loadBookings() {
        bookingTable.getItems().setAll(app.getBookings().sorted(sort.getValue() == null ? "Check-in date" : sort.getValue()));
    }

    private VBox newBookingCard() {
        TextField guestField = new TextField();
        guestField.setPromptText("Type a guest name");
        ListView<Guest> matches = new ListView<>();
        matches.setPrefHeight(90);
        Label chosen = caption("No guest chosen");
        guestField.textProperty().addListener((obs, old, text) -> matches.getItems().setAll(app.getBookings().searchGuests(text)));
        matches.getSelectionModel().selectedItemProperty().addListener((obs, old, guest) -> {
            if (guest != null) {
                selectedGuest = guest;
                chosen.setText("Guest: " + guest.getName() + " (" + guest.getCode() + ")");
            }
        });
        Button newGuest = new Button("New Guest...");
        newGuest.setOnAction(e -> {
            Guest guest = askNewGuest();
            if (guest != null) {
                selectedGuest = guest;
                chosen.setText("Guest: " + guest.getName() + " (" + guest.getCode() + ")");
            }
        });
        checkIn.setValue(LocalDate.now());
        checkOut.setValue(LocalDate.now().plusDays(1));
        checkIn.valueProperty().addListener((obs, old, value) -> loadRooms());
        checkOut.valueProperty().addListener((obs, old, value) -> loadRooms());
        ComboBox<BookingChannel> channel = new ComboBox<>();
        channel.getItems().addAll(BookingChannel.values());
        channel.getSelectionModel().selectFirst();
        promotion.getItems().add("(none)");
        for (Promotion promo : app.getStore().getPromotions()) {
            promotion.getItems().add(promo.getCode());
        }
        promotion.getSelectionModel().selectFirst();
        promotion.setOnAction(e -> updateTotal());
        TextField groupName = new TextField();
        groupName.setPromptText("Only for group bookings");
        TextField requests = new TextField();
        GridPane grid = form();
        addRow(grid, 0, "Check-in:", checkIn);
        addRow(grid, 1, "Check-out:", checkOut);
        addRow(grid, 2, "Channel:", channel);
        addRow(grid, 3, "Promotion:", promotion);
        addRow(grid, 4, "Group name:", groupName);
        addRow(grid, 5, "Requests:", requests);
        roomTable.getColumns().add(column("Room", Room::getRoomNumber, 60));
        roomTable.getColumns().add(column("Type", Room::getTypeName, 90));
        roomTable.getColumns().add(column("Guests", Room::getCapacity, 60));
        roomTable.getColumns().add(column("Rate / night", r -> money(r.calculateRate()), 100));
        roomTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        roomTable.getSelectionModel().getSelectedItems().addListener((javafx.collections.ListChangeListener<Room>) change -> updateTotal());
        roomTable.setPrefHeight(150);
        roomTable.setPlaceholder(new Label("No free rooms for these dates"));
        loadRooms();
        total.setStyle("-fx-font-weight: bold;");
        Button save = new Button("Save Booking");
        save.setDefaultButton(true);
        save.setOnAction(e -> {
            List<Room> rooms = new ArrayList<>(roomTable.getSelectionModel().getSelectedItems());
            try {
                Booking booking = app.getBookings().book(selectedGuest, rooms, checkIn.getValue(), checkOut.getValue(), channel.getValue(), selectedPromotion(), requests.getText(), groupName.getText());
                app.info("Booking saved", "Booking code: " + booking.getCode() + "\nTotal: " + money(booking.totalPrice()) + " THB");
                app.refresh();
            } catch (ValidationException | BookingConflictException ex) {
                app.error(ex.getMessage());
            }
        });
        Button clear = new Button("Clear");
        clear.setOnAction(e -> app.refresh());
        VBox box = card("New Booking", row(new Label("Guest:"), guestField, newGuest), matches, chosen, grid, new Label("Free rooms (hold Ctrl to pick several):"), roomTable, total, row(spacer(), save, clear));
        box.setPrefWidth(400);
        box.setMinWidth(360);
        return box;
    }

    private Promotion selectedPromotion() {
        String code = promotion.getValue();
        return code == null || code.equals("(none)") ? null : app.getStore().findPromotion(code);
    }

    private void loadRooms() {
        try {
            roomTable.getItems().setAll(app.getBookings().findFreeRooms(checkIn.getValue(), checkOut.getValue(), 1));
        } catch (ValidationException e) {
            roomTable.getItems().clear();
        }
        updateTotal();
    }

    private void updateTotal() {
        List<Room> rooms = new ArrayList<>(roomTable.getSelectionModel().getSelectedItems());
        if (rooms.isEmpty() || checkIn.getValue() == null || checkOut.getValue() == null || !checkOut.getValue().isAfter(checkIn.getValue())) {
            total.setText("Total (THB): 0.00");
            return;
        }
        Booking quote = app.getBookings().quote(rooms, checkIn.getValue(), checkOut.getValue(), selectedPromotion());
        total.setText(quote.nights() + " nights, subtotal " + money(quote.subtotal()) + ", total (THB): " + money(quote.totalPrice()));
    }

    private Guest askNewGuest() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Guest");
        dialog.setHeaderText("Add a walk-in guest");
        TextField name = new TextField();
        TextField phone = new TextField();
        TextField email = new TextField();
        TextField idNumber = new TextField();
        GridPane grid = form();
        addRow(grid, 0, "Full name:", name);
        addRow(grid, 1, "Phone:", phone);
        addRow(grid, 2, "E-mail:", email);
        addRow(grid, 3, "ID / passport:", idNumber);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return null;
        }
        try {
            return app.getBookings().addGuest(name.getText(), phone.getText(), email.getText(), idNumber.getText());
        } catch (ValidationException e) {
            app.error(e.getMessage());
            return null;
        }
    }

}
