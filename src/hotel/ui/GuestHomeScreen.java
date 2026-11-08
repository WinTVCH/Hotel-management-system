package hotel.ui;

import hotel.bookings.Booking;
import hotel.bookings.BookingStatus;
import hotel.errors.ValidationException;
import hotel.finance.Invoice;
import hotel.finance.InvoiceLine;
import hotel.finance.Payment;
import hotel.people.Guest;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GuestHomeScreen extends Screen {

    public GuestHomeScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "My Stays";
    }

    @Override
    public Node build() {
        Guest guest = app.getSession().getGuest();
        LocalDate today = app.getSession().today();
        Booking current = app.getBookings().currentBookingOf(guest, today);
        VBox root = new VBox(12);
        root.setPadding(new Insets(14));
        Label welcome = new Label("Welcome, " + guest.getName());
        welcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.getChildren().add(welcome);
        if (current == null) {
            Button book = new Button("Book a Room");
            book.setDefaultButton(true);
            book.setOnAction(e -> app.show("Book a Room"));
            root.getChildren().add(card("Current Booking", caption("You have no upcoming booking."), book));
        } else {
            VBox details = detailsCard(current);
            VBox action = actionCard(current, today);
            HBox top = new HBox(12, details, action);
            HBox.setHgrow(details, Priority.ALWAYS);
            HBox.setHgrow(action, Priority.ALWAYS);
            root.getChildren().add(top);
        }
        root.getChildren().add(otherBookings(guest, current, today));
        return root;
    }

    private VBox detailsCard(Booking booking) {
        boolean inside = booking.getStatus() == BookingStatus.CHECKED_IN;
        GridPane grid = form();
        addRow(grid, 0, "Booking code:", new Label(booking.getCode()));
        addRow(grid, 1, "Room:", new Label(inside ? booking.getRoomTypes() + " " + booking.getRoomNumbers() : booking.getRoomTypes() + " (number given at check-in)"));
        addRow(grid, 2, "Check-in:", new Label(booking.getCheckIn() + ", from 14:00"));
        addRow(grid, 3, "Check-out:", new Label(booking.getCheckOut() + ", by 12:00"));
        addRow(grid, 4, "Status:", new Label(booking.getStatus().name().replace('_', ' ')));
        String message = inside ? "You are checked in. Services you order are added to this stay." : "Check in here on your arrival day to skip the front desk.";
        return card("Current Booking", grid, caption(message));
    }

    private VBox actionCard(Booking booking, LocalDate today) {
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            Invoice invoice = app.getBilling().invoiceOf(booking);
            TableView<InvoiceLine> lines = new TableView<>();
            lines.getColumns().add(column("Item", InvoiceLine::getItem, 220));
            lines.getColumns().add(column("Amount", l -> money(l.getAmount()), 100));
            lines.getItems().addAll(invoice.getLines());
            lines.setPrefHeight(160);
            Label balance = new Label("Paid: " + money(invoice.paid()) + "    Balance due: " + money(invoice.outstanding()) + " THB");
            balance.setStyle("-fx-font-weight: bold;");
            Button checkOut = new Button(invoice.isPaid() ? "Check Out" : "Pay and Check Out");
            checkOut.setDefaultButton(true);
            checkOut.setOnAction(e -> {
                Payment payment = null;
                double due = invoice.outstanding();
                if (!invoice.isPaid()) {
                    payment = askPayment("Pay and Check Out", due, false);
                    if (payment == null) {
                        return;
                    }
                }
                try {
                    app.getBookings().checkOut(booking, payment);
                    app.info("Payment successful", money(due) + " THB paid. You are checked out of room " + booking.getRoomNumbers() + ".\nThank you for staying at Putter's Hotel.\n(Demo app: no real payment was taken.)");
                    app.refresh();
                } catch (ValidationException ex) {
                    app.error(ex.getMessage());
                }
            });
            return card("Check Out", lines, balance, row(spacer(), checkOut));
        }
        if (booking.canCheckIn(today)) {
            CheckBox confirm = new CheckBox("My ID card number on file is correct.");
            Button checkIn = new Button("Check In Now");
            checkIn.setDefaultButton(true);
            checkIn.disableProperty().bind(confirm.selectedProperty().not());
            checkIn.setOnAction(e -> {
                try {
                    app.getBookings().checkIn(booking, today);
                    app.info("Checked in", "Welcome! Your room is " + booking.getRoomNumbers() + ".\nDoor code: " + doorCode(booking));
                    app.refresh();
                } catch (ValidationException ex) {
                    app.error(ex.getMessage());
                }
            });
            return card("Check In", confirm, checkIn);
        }
        return card("Check In", caption("Check-in opens on " + booking.getCheckIn() + " at 14:00."));
    }

    private String doorCode(Booking booking) {
        return String.format("%04d", Math.abs(booking.getCode().hashCode()) % 10000);
    }

    private VBox otherBookings(Guest guest, Booking current, LocalDate today) {
        TableView<Booking> table = new TableView<>();
        table.getColumns().add(column("Code", Booking::getCode, 90));
        table.getColumns().add(column("Room", b -> b.getRoomTypes() + " " + b.getRoomNumbers(), 160));
        table.getColumns().add(column("Check-in", Booking::getCheckIn, 100));
        table.getColumns().add(column("Check-out", Booking::getCheckOut, 100));
        table.getColumns().add(column("Total", b -> money(b.totalPrice()), 90));
        table.getColumns().add(column("Status", b -> b.getStatus().name().replace('_', ' '), 110));
        for (Booking booking : app.getBookings().bookingsOf(guest)) {
            if (booking != current) {
                table.getItems().add(booking);
            }
        }
        table.setPrefHeight(200);
        table.setPlaceholder(new Label("No other bookings"));
        Button cancel = new Button("Cancel Booking");
        cancel.setOnAction(e -> {
            Booking booking = table.getSelectionModel().getSelectedItem();
            if (booking == null || !app.confirm("Cancel booking " + booking.getCode() + "?")) {
                return;
            }
            try {
                app.getBookings().cancel(booking, today);
                app.refresh();
            } catch (ValidationException ex) {
                app.error(ex.getMessage());
            }
        });
        Button book = new Button("New Booking...");
        book.setOnAction(e -> app.show("Book a Room"));
        return card("Other Bookings", table, row(cancel, book));
    }

}
