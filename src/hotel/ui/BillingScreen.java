package hotel.ui;

import hotel.errors.ValidationException;
import hotel.finance.CashPayment;
import hotel.finance.Invoice;
import hotel.finance.InvoiceLine;
import hotel.finance.Payment;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BillingScreen extends Screen {
    private TableView<Invoice> invoiceTable = new TableView<>();
    private TableView<InvoiceLine> lineTable = new TableView<>();
    private CheckBox showPaid = new CheckBox("Show paid invoices");
    private Label detailTitle = new Label("Choose an invoice");
    private Label totalLabel = new Label();
    private Label paidLabel = new Label();
    private Label outstandingLabel = new Label();
    private TextField amount = new TextField();
    private TextField reference = new TextField();
    private ToggleGroup method = new ToggleGroup();
    private Label hint = caption("");

    public BillingScreen(ScreenManager app) {
        super(app);
    }

    @Override
    public String getTitle() {
        return "Billing";
    }

    @Override
    public Node build() {
        invoiceTable.getColumns().add(column("Invoice", Invoice::getNumber, 80));
        invoiceTable.getColumns().add(column("Guest Name", i -> i.getStay().getBooking().getDisplayName(), 150));
        invoiceTable.getColumns().add(column("Room", i -> i.getStay().getBooking().getRoomNumbers(), 70));
        invoiceTable.getColumns().add(column("Outstanding", i -> money(i.outstanding()), 90));
        invoiceTable.setPrefHeight(440);
        invoiceTable.setPlaceholder(new Label("No open invoices"));
        invoiceTable.getSelectionModel().selectedItemProperty().addListener((obs, old, invoice) -> showInvoice(invoice));
        showPaid.setOnAction(e -> loadInvoices());
        VBox list = card("Invoices (largest balance first)", invoiceTable, showPaid);

        lineTable.getColumns().add(column("Item", InvoiceLine::getItem, 230));
        lineTable.getColumns().add(column("Qty", InvoiceLine::getQuantity, 50));
        lineTable.getColumns().add(column("Price", l -> money(l.getUnitPrice()), 90));
        lineTable.getColumns().add(column("Amount", l -> money(l.getAmount()), 90));
        lineTable.setPrefHeight(200);
        detailTitle.setStyle("-fx-font-weight: bold;");
        outstandingLabel.setStyle("-fx-font-weight: bold;");
        GridPane totals = form();
        addRow(totals, 0, "Total:", totalLabel);
        addRow(totals, 1, "Paid:", paidLabel);
        addRow(totals, 2, "Outstanding (THB):", outstandingLabel);
        VBox detail = card("Invoice Detail", detailTitle, lineTable, totals);

        HBox methods = row();
        for (String name : new String[] {"Cash", "Transfer", "Card"}) {
            RadioButton button = new RadioButton(name);
            button.setUserData(name);
            button.setToggleGroup(method);
            button.setSelected(name.equals("Cash"));
            methods.getChildren().add(button);
        }
        method.selectedToggleProperty().addListener((obs, old, value) -> updateHint());
        updateHint();
        amount.setPrefWidth(120);
        Button record = new Button("Record Payment");
        record.setDefaultButton(true);
        record.setOnAction(e -> recordPayment());
        VBox payment = card("Take Payment", methods, hint, row(new Label("Amount:"), amount, new Label("Reference:"), reference, record));

        VBox right = new VBox(12, detail, payment);
        HBox root = new HBox(12, list, right);
        HBox.setHgrow(right, Priority.ALWAYS);
        root.setPadding(new Insets(14));
        loadInvoices();
        return root;
    }

    private void loadInvoices() {
        Invoice keep = invoiceTable.getSelectionModel().getSelectedItem();
        invoiceTable.getItems().setAll(showPaid.isSelected() ? app.getBilling().invoicesByOutstanding() : app.getBilling().openInvoices());
        if (keep != null && invoiceTable.getItems().contains(keep)) {
            invoiceTable.getSelectionModel().select(keep);
        } else {
            invoiceTable.getSelectionModel().selectFirst();
        }
        showInvoice(invoiceTable.getSelectionModel().getSelectedItem());
    }

    private void showInvoice(Invoice invoice) {
        if (invoice == null) {
            detailTitle.setText("Choose an invoice");
            lineTable.getItems().clear();
            totalLabel.setText("");
            paidLabel.setText("");
            outstandingLabel.setText("");
            amount.clear();
            return;
        }
        detailTitle.setText(invoice.getNumber() + " - " + invoice.getStay().getBooking().getCode() + " - " + invoice.getStay().getBooking().getCheckIn() + " to " + invoice.getStay().getBooking().getCheckOut());
        lineTable.getItems().setAll(invoice.getLines());
        totalLabel.setText(money(invoice.total()));
        paidLabel.setText(money(invoice.paid()));
        outstandingLabel.setText(money(invoice.outstanding()));
        amount.setText(String.format("%.2f", invoice.outstanding()));
    }

    private String selectedMethod() {
        return method.getSelectedToggle() == null ? "Cash" : (String) method.getSelectedToggle().getUserData();
    }

    private void updateHint() {
        String name = selectedMethod();
        reference.setPromptText(referenceHint(name));
        if (name.equals("Cash")) {
            hint.setText("Enter the cash received in Reference. Change is shown after recording.");
        } else if (name.equals("Transfer")) {
            hint.setText("Enter the bank reference from the transfer slip.");
        } else {
            hint.setText("Run the card on the terminal, then enter the last 4 digits.");
        }
    }

    private void recordPayment() {
        Invoice invoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (invoice == null) {
            app.error("Choose an invoice first.");
            return;
        }
        double value;
        try {
            value = Double.parseDouble(amount.getText().replace(",", "").trim());
        } catch (NumberFormatException e) {
            app.error("Enter the amount as a number.");
            return;
        }
        try {
            Payment payment = app.getBilling().createPayment(selectedMethod(), value, reference.getText());
            app.getBilling().pay(invoice, payment);
            String message = money(value) + " THB paid by " + payment.getMethod().toLowerCase() + " on " + invoice.getNumber() + ".";
            if (payment instanceof CashPayment) {
                message += "\nChange: " + money(((CashPayment) payment).change()) + " THB";
            }
            message += invoice.isPaid() ? "\nThe invoice is now paid in full." : "\nStill outstanding: " + money(invoice.outstanding()) + " THB";
            app.info("Payment recorded successfully", message);
            reference.clear();
            loadInvoices();
        } catch (ValidationException e) {
            app.error(e.getMessage());
        }
    }

}
