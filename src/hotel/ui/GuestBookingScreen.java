package hotel.ui;

import hotel.bookings.Booking;
import hotel.bookings.BookingChannel;
import hotel.errors.BookingConflictException;
import hotel.errors.ValidationException;
import hotel.finance.Payment;
import hotel.pricing.Promotion;
import hotel.rooms.Room;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GuestBookingScreen extends Screen {
    private DatePicker checkIn = new DatePicker(LocalDate.now().plusDays(1));
    private DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(4));
    private Spinner<Integer> guests = new Spinner<>(1, 6, 2);
    private TableView<Room> roomTable = new TableView<>();
    private Label roomLabel = new Label("-");
    private Label lineLabel = new Label("-");
    private Label promoLabel = new Label("-");
    private Label totalLabel = new Label("-");
    private Label depositLabel = new Label("-");

    public GuestBookingScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "Book a Room";
    }

    @Override
    public Node build() {
        Button search = new Button("Search");
        search.setOnAction(e -> search());
        VBox searchCard = card("Search", row(new Label("Check-in:"), checkIn, new Label("Check-out:"), checkOut, new Label("Guests:"), guests, search));

        roomTable.getColumns().add(column("Room Type", Room::getTypeName, 120));
        roomTable.getColumns().add(column("Sleeps", Room::getCapacity, 70));
        roomTable.getColumns().add(column("Rate / night (THB)", r -> money(r.calculateRate()), 140));
        roomTable.setPrefHeight(260);
        roomTable.setPlaceholder(new Label("No free rooms. Try other dates."));
        roomTable.getSelectionModel().selectedItemProperty().addListener((obs, old, room) -> updateSummary());
        VBox rooms = card("Free Rooms", roomTable);

        totalLabel.setStyle("-fx-font-weight: bold;");
        GridPane summary = form();
        addRow(summary, 0, "Room:", roomLabel);
        addRow(summary, 1, "Nights:", lineLabel);
        addRow(summary, 2, "Promotion:", promoLabel);
        addRow(summary, 3, "Total (THB):", totalLabel);
        addRow(summary, 4, "Deposit now (50%):", depositLabel);
        TextField requests = new TextField();
        requests.setPromptText("Late arrival, extra pillow...");
        ComboBox<String> method = new ComboBox<>();
        method.getItems().addAll("Card", "Transfer");
        method.getSelectionModel().selectFirst();
        TextField reference = new TextField();
        reference.setPromptText(referenceHint("Card"));
        method.valueProperty().addListener((obs, old, value) -> reference.setPromptText(referenceHint(value)));
        addRow(summary, 5, "Requests:", requests);
        addRow(summary, 6, "Pay with:", method);
        addRow(summary, 7, "Reference:", reference);
        Button book = new Button("Pay Deposit and Book");
        book.setDefaultButton(true);
        book.setOnAction(e -> book(method.getValue(), reference.getText(), requests.getText()));
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> app.show("My Stays"));
        VBox summaryCard = card("Booking Summary", summary, row(spacer(), book, cancel), caption("Free cancellation until the day before check-in."));
        summaryCard.setPrefWidth(380);

        HBox middle = new HBox(12, rooms, summaryCard);
        HBox.setHgrow(rooms, Priority.ALWAYS);
        VBox root = new VBox(12, searchCard, middle);
        root.setPadding(new Insets(14));
        search();
        return root;
    }

    private void search() {
        try {
            roomTable.getItems().setAll(app.getBookings().cheapestFreeByType(checkIn.getValue(), checkOut.getValue(), guests.getValue()).values());
            roomTable.getSelectionModel().selectFirst();
            app.setStatus(roomTable.getItems().size() + " room types found");
        } catch (ValidationException e) {
            roomTable.getItems().clear();
            app.error(e.getMessage());
        }
        updateSummary();
    }

    private Booking quote() {
        Room room = roomTable.getSelectionModel().getSelectedItem();
        if (room == null) {
            return null;
        }
        List<Room> rooms = new ArrayList<>();
        rooms.add(room);
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn.getValue(), checkOut.getValue());
        Promotion promotion = app.getBookings().bestLongStayPromotion(nights);
        return app.getBookings().quote(rooms, checkIn.getValue(), checkOut.getValue(), promotion);
    }

    private void updateSummary() {
        Booking quote = quote();
        if (quote == null) {
            roomLabel.setText("-");
            lineLabel.setText("-");
            promoLabel.setText("-");
            totalLabel.setText("-");
            depositLabel.setText("-");
            return;
        }
        roomLabel.setText(quote.getRoomTypes());
        lineLabel.setText(quote.nights() + " nights, " + money(quote.subtotal()));
        promoLabel.setText(quote.getPromotion() == null ? "(none)" : quote.getPromotion().getDescription() + " (-" + money(quote.discount()) + ")");
        totalLabel.setText(money(quote.totalPrice()));
        depositLabel.setText(money(deposit(quote)));
    }

    private double deposit(Booking quote) {
        return Math.round(quote.totalPrice() / 2 * 100) / 100.0;
    }

    private void book(String method, String reference, String requests) {
        Booking quote = quote();
        if (quote == null) {
            app.error("Choose a room type first.");
            return;
        }
        try {
            double amount = deposit(quote);
            Payment payment = app.getBilling().createPayment(method, amount, reference);
            if (!payment.processPayment()) {
                app.error(app.getBilling().failureMessage(payment));
                return;
            }
            Booking booking = app.getBookings().book(app.getSession().getGuest(), quote.getRooms(), quote.getCheckIn(), quote.getCheckOut(), BookingChannel.ONLINE, quote.getPromotion(), requests, null);
            app.getBookings().payDeposit(booking, payment);
            app.info("Purchased successfully", "Booking code: " + booking.getCode() + "\n" + booking.getRoomTypes() + ", " + booking.getCheckIn() + " to " + booking.getCheckOut() + "\nDeposit of " + money(amount) + " THB paid; " + money(booking.totalPrice() - amount) + " THB is due at check-out.\n(Demo app: no real payment was taken.)");
            app.show("My Stays");
        } catch (ValidationException | BookingConflictException e) {
            app.error(e.getMessage());
        }
    }

}
