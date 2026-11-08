package hotel.ui;

import hotel.bookings.Stay;
import hotel.errors.ValidationException;
import hotel.extras.ExtraService;
import hotel.finance.InvoiceLine;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ServiceScreen extends Screen {
    private ExtraService selected;
    private Label serviceLabel = new Label("(choose a service)");
    private Label chargeLabel = new Label("0.00");
    private Spinner<Integer> quantity = new Spinner<>(1, 50, 1);

    public ServiceScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "Extra Services";
    }

    @Override
    public Node build() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        for (String category : new String[] {"Meals", "Laundry", "Transport", "Tours"}) {
            TableView<ExtraService> table = new TableView<>();
            table.getColumns().add(column("Service", ExtraService::getName, 180));
            table.getColumns().add(column("Description", ExtraService::getDescription, 220));
            table.getColumns().add(column("Details", ExtraService::getDetails, 230));
            table.getColumns().add(column("Price (THB)", s -> money(s.getUnitPrice()) + " / " + s.getUnit(), 120));
            for (ExtraService service : app.getStore().getServices()) {
                if (service.getCategory().equals(category)) {
                    table.getItems().add(service);
                }
            }
            table.getSelectionModel().selectedItemProperty().addListener((obs, old, service) -> choose(service));
            table.setPrefHeight(420);
            tabs.getTabs().add(new Tab(category.equals("Tours") ? "Tour Packages" : category, table));
        }
        ComboBox<Stay> stay = new ComboBox<>();
        stay.getItems().addAll(app.getBookings().openStays());
        stay.getSelectionModel().selectFirst();
        stay.setMaxWidth(Double.MAX_VALUE);
        quantity.valueProperty().addListener((obs, old, value) -> updateCharge());
        chargeLabel.setStyle("-fx-font-weight: bold;");
        GridPane grid = form();
        addRow(grid, 0, "Stay:", stay);
        addRow(grid, 1, "Service:", serviceLabel);
        addRow(grid, 2, "Quantity:", quantity);
        addRow(grid, 3, "Charge (THB):", chargeLabel);
        Button add = new Button("Add Charge");
        add.setDefaultButton(true);
        add.setOnAction(e -> {
            if (selected == null) {
                app.error("Choose a service from the list.");
                return;
            }
            try {
                InvoiceLine line = app.getBilling().addCharge(stay.getValue(), selected, quantity.getValue());
                app.info("Charge added", line.getItem() + " x " + line.getQuantity() + " = " + money(line.getAmount()) + " THB\nAdded to " + stay.getValue() + ".");
                app.setStatus("Charge added to " + stay.getValue().getBooking().getCode());
            } catch (ValidationException ex) {
                app.error(ex.getMessage());
            }
        });
        VBox charge = card("Charge to Stay", grid, row(spacer(), add));
        if (stay.getItems().isEmpty()) {
            charge.getChildren().add(caption("No guests are checked in right now."));
        }
        charge.setPrefWidth(340);
        charge.setMinWidth(300);
        HBox root = new HBox(12, tabs, charge);
        HBox.setHgrow(tabs, Priority.ALWAYS);
        root.setPadding(new Insets(14));
        return root;
    }

    private void choose(ExtraService service) {
        selected = service;
        serviceLabel.setText(service == null ? "(choose a service)" : service.getName());
        updateCharge();
    }

    private void updateCharge() {
        if (selected == null) {
            chargeLabel.setText("0.00");
            return;
        }
        chargeLabel.setText(money(app.getBilling().previewCharge(selected, quantity.getValue())));
    }

}
