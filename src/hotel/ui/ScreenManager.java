package hotel.ui;

import hotel.logic.AuthService;
import hotel.logic.BillingService;
import hotel.logic.BookingService;
import hotel.logic.HousekeepingService;
import hotel.logic.Session;
import hotel.storage.DataStore;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScreenManager {
    private Stage stage;
    private DataStore store;
    private AuthService auth;
    private BookingService bookings;
    private BillingService billing;
    private HousekeepingService housekeeping;
    private Session session;
    private String current;
    private Label statusMessage = new Label("Ready");

    public ScreenManager(Stage stage, DataStore store) {
        this.stage = stage;
        this.store = store;
        this.billing = new BillingService(store);
        this.housekeeping = new HousekeepingService(store);
        this.bookings = new BookingService(store, billing, housekeeping);
        this.auth = new AuthService(store);
        stage.setTitle("Putter's Hotel");
        stage.setMinWidth(980);
        stage.setMinHeight(640);
    }

    public DataStore getStore() {
        return store;
    }

    public AuthService getAuth() {
        return auth;
    }

    public BookingService getBookings() {
        return bookings;
    }

    public BillingService getBilling() {
        return billing;
    }

    public HousekeepingService getHousekeeping() {
        return housekeeping;
    }

    public Session getSession() {
        return session;
    }

    public void showLogin() {
        session = null;
        current = null;
        setRoot((Parent) new LoginScreen(this).build());
        stage.setTitle("Putter's Hotel - Sign In");
    }

    public void showRegister() {
        setRoot((Parent) new GuestRegisterScreen(this).build());
        stage.setTitle("Putter's Hotel - New Guest Account");
    }

    public void startSession(Session session) {
        this.session = session;
        show(session.getMenuItems().get(0));
    }

    public void show(String item) {
        open(createScreen(item), item);
    }

    public void showReport(String reportName) {
        open(new ReportScreen(this, reportName), "Reports");
    }

    public void refresh() {
        if (current != null) {
            show(current);
        }
    }

    private Screen createScreen(String item) {
        switch (item) {
            case "Bookings":
                return new BookingScreen(this);
            case "Room Rack":
                return new RoomScreen(this);
            case "Billing":
                return new BillingScreen(this);
            case "Services":
                return new ServiceScreen(this);
            case "Reports":
                return new ReportScreen(this, "Occupancy");
            case "My Stays":
                return new GuestHomeScreen(this);
            case "Book a Room":
                return new GuestBookingScreen(this);
            default:
                return new DashboardScreen(this);
        }
    }

    private void open(Screen screen, String item) {
        current = item;
        statusMessage.setText("Ready");
        BorderPane root = new BorderPane();
        root.setTop(new VBox(menuBar(), toolBar()));
        ScrollPane scroll = new ScrollPane(screen.build());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f4f4f4; -fx-background: #f4f4f4;");
        root.setCenter(scroll);
        root.setBottom(statusBar());
        setRoot(root);
        String area = session.isGuest() ? "Guest Self-Service" : "Front Desk";
        stage.setTitle("Putter's Hotel - " + area + " - " + screen.getTitle());
    }

    private void setRoot(Parent root) {
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root, 1180, 760));
        } else {
            stage.getScene().setRoot(root);
        }
    }

    private MenuBar menuBar() {
        MenuItem logOff = new MenuItem("Log Off");
        logOff.setOnAction(e -> logout());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> Platform.exit());
        Menu file = new Menu("File", null, logOff, exit);
        Menu go = new Menu("Go");
        for (String item : session.getMenuItems()) {
            MenuItem entry = new MenuItem(item);
            entry.setOnAction(e -> show(item));
            go.getItems().add(entry);
        }
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> info("Putter's Hotel", "Hotel management system\nOOP and Data Structures project, SUT"));
        Menu help = new Menu("Help", null, about);
        return new MenuBar(file, go, help);
    }

    private ToolBar toolBar() {
        ToolBar bar = new ToolBar();
        ToggleGroup group = new ToggleGroup();
        for (String item : session.getMenuItems()) {
            ToggleButton button = new ToggleButton(item);
            button.setToggleGroup(group);
            button.setSelected(item.equals(current));
            button.setOnAction(e -> show(item));
            bar.getItems().add(button);
        }
        Button logOff = new Button("Log Off");
        logOff.setOnAction(e -> logout());
        bar.getItems().addAll(new Separator(), logOff);
        return bar;
    }

    private Node statusBar() {
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label user = new Label("User: " + session.getAccount().getUsername());
        Label role = new Label(session.getRoleLabel());
        Label date = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        HBox bar = new HBox(18, statusMessage, gap, user, role, date);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(4, 10, 4, 10));
        bar.setStyle("-fx-background-color: #ececec; -fx-border-color: #d0d0d0 transparent transparent transparent; -fx-font-size: 12px;");
        return bar;
    }

    public void setStatus(String message) {
        statusMessage.setText(message);
    }

    public void logout() {
        showLogin();
    }

    public void info(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Putter's Hotel");
        alert.setHeaderText(header);
        alert.initOwner(stage);
        alert.showAndWait();
    }

    public void error(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle("Putter's Hotel");
        alert.setHeaderText(null);
        alert.initOwner(stage);
        alert.showAndWait();
    }

    public boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Putter's Hotel");
        alert.setHeaderText(null);
        alert.initOwner(stage);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    public String money(double amount) {
        return String.format("%,.2f", amount);
    }

}
