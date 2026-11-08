package hotel.ui;

import hotel.errors.ValidationException;
import hotel.finance.Payment;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class Screen {
    protected static final String CARD = "-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-radius: 6; -fx-background-radius: 6;";
    protected ScreenManager app;

    public Screen(ScreenManager app) {
        this.app = app;
    }

    public abstract String getTitle();

    public abstract Node build();

    protected VBox card(String title, Node... content) {
        Label heading = new Label(title);
        heading.setStyle("-fx-font-weight: bold; -fx-text-fill: #2b3035;");
        VBox box = new VBox(8, heading);
        box.getChildren().addAll(content);
        box.setPadding(new Insets(10, 12, 12, 12));
        box.setStyle(CARD);
        return box;
    }

    protected HBox row(Node... nodes) {
        HBox box = new HBox(8, nodes);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    protected Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    protected Label caption(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #5a5f66;");
        label.setWrapText(true);
        return label;
    }

    protected GridPane form() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        return grid;
    }

    protected void addRow(GridPane grid, int row, String label, Node field) {
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    protected <T> TableColumn<T, String> column(String title, Function<T, Object> value, double width) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(value.apply(cell.getValue()))));
        column.setPrefWidth(width);
        return column;
    }

    protected String money(double amount) {
        return app.money(amount);
    }

    protected String referenceHint(String method) {
        if (method.equals("Cash")) {
            return "Cash received";
        }
        if (method.equals("Transfer")) {
            return "Bank reference";
        }
        return "Last 4 digits of card";
    }

    protected Payment askPayment(String title, double amount, boolean allowCash) {
        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Amount due: " + money(amount) + " THB");
        ComboBox<String> method = new ComboBox<>();
        method.getItems().addAll("Card", "Transfer");
        if (allowCash) {
            method.getItems().add("Cash");
        }
        method.getSelectionModel().selectFirst();
        TextField reference = new TextField();
        reference.setPromptText(referenceHint(method.getValue()));
        method.valueProperty().addListener((obs, old, value) -> reference.setPromptText(referenceHint(value)));
        GridPane grid = form();
        addRow(grid, 0, "Pay with:", method);
        addRow(grid, 1, "Reference:", reference);
        dialog.getDialogPane().setContent(grid);
        ButtonType pay = new ButtonType("Pay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(pay, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button != pay) {
                return null;
            }
            try {
                return app.getBilling().createPayment(method.getValue(), amount, reference.getText());
            } catch (ValidationException e) {
                app.error(e.getMessage());
                return null;
            }
        });
        return dialog.showAndWait().orElse(null);
    }

}
