package hotel.ui;

import hotel.errors.ValidationException;
import hotel.logic.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GuestRegisterScreen extends Screen {

    public GuestRegisterScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "New Guest Account";
    }

    @Override
    public Node build() {
        Label title = new Label("Create a guest account");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        TextField name = new TextField();
        TextField phone = new TextField();
        TextField idNumber = new TextField();
        TextField email = new TextField();
        PasswordField password = new PasswordField();
        PasswordField confirm = new PasswordField();
        GridPane personal = form();
        addRow(personal, 0, "Full name:", name);
        addRow(personal, 1, "Phone:", phone);
        addRow(personal, 2, "ID card / passport no.:", idNumber);
        GridPane account = form();
        addRow(account, 0, "E-mail (user name):", email);
        addRow(account, 1, "Password:", password);
        addRow(account, 2, "Confirm password:", confirm);
        CheckBox terms = new CheckBox("I agree to the hotel's terms of stay.");
        Label error = new Label();
        error.setStyle("-fx-text-fill: #8a1f14;");
        error.setWrapText(true);
        Button create = new Button("Create Account");
        create.setDefaultButton(true);
        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> app.showLogin());
        create.setOnAction(e -> {
            if (!terms.isSelected()) {
                error.setText("Tick the box to agree to the terms of stay.");
                return;
            }
            try {
                Session session = app.getAuth().registerGuest(name.getText(), phone.getText(), email.getText(), idNumber.getText(), password.getText(), confirm.getText());
                app.info("Account created", "Welcome, " + session.getPerson().getName() + ". You can now book a room.");
                app.startSession(session);
            } catch (ValidationException ex) {
                error.setText(ex.getMessage());
            }
        });
        VBox box = new VBox(12, title, caption("With an account you can book rooms, check in and check out yourself."), card("Personal details", personal), card("Account", account), terms, error, row(spacer(), create, cancel));
        box.setPadding(new Insets(20));
        box.setMaxWidth(520);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #c8c8c8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 18, 0, 0, 6);");
        StackPane root = new StackPane(box);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #e6e9ec;");
        return root;
    }

}
