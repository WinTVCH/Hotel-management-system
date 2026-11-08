package hotel.ui;

import hotel.errors.ValidationException;
import hotel.people.AccountType;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginScreen extends Screen {

    public LoginScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "Sign In";
    }

    @Override
    public Node build() {
        Label title = new Label("Putter's Hotel");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitle = caption("Sign in with your user name and password.");
        ToggleGroup kind = new ToggleGroup();
        RadioButton staff = new RadioButton("Staff");
        RadioButton guest = new RadioButton("Guest");
        staff.setToggleGroup(kind);
        guest.setToggleGroup(kind);
        staff.setSelected(true);
        TextField username = new TextField();
        username.setPromptText("User name or e-mail");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        GridPane grid = form();
        addRow(grid, 0, "User name:", username);
        addRow(grid, 1, "Password:", password);
        Label error = new Label();
        error.setWrapText(true);
        error.setStyle("-fx-text-fill: #8a1f14; -fx-background-color: #fdecea; -fx-border-color: #f0b7b0; -fx-padding: 6 10; -fx-background-radius: 4; -fx-border-radius: 4;");
        error.setVisible(false);
        error.managedProperty().bind(error.visibleProperty());
        Hyperlink register = new Hyperlink("Create a guest account...");
        register.setOnAction(e -> app.showRegister());
        register.visibleProperty().bind(guest.selectedProperty());
        Button ok = new Button("Sign In");
        ok.setDefaultButton(true);
        Button exit = new Button("Exit");
        exit.setOnAction(e -> Platform.exit());
        ok.setOnAction(e -> {
            AccountType type = guest.isSelected() ? AccountType.GUEST : AccountType.STAFF;
            try {
                app.startSession(app.getAuth().login(username.getText(), password.getText(), type));
            } catch (ValidationException ex) {
                error.setText(ex.getMessage());
                error.setVisible(true);
                password.clear();
            }
        });
        VBox box = new VBox(12, title, subtitle, row(new Label("Sign in as:"), staff, guest), grid, error, row(register, spacer(), ok, exit));
        box.setPadding(new Insets(20));
        box.setMaxWidth(420);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.setStyle(CARD + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 18, 0, 0, 6);");
        StackPane root = new StackPane(box);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #e6e9ec;");
        Platform.runLater(username::requestFocus);
        return root;
    }

}
