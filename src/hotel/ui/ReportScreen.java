package hotel.ui;

import hotel.reports.GuestReport;
import hotel.reports.HousekeepingReport;
import hotel.reports.OccupancyReport;
import hotel.reports.Report;
import hotel.reports.RevenueReport;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ReportScreen extends Screen {
    private String initial;

    public ReportScreen(ScreenManager app, String initial) {
        super(app);
        this.initial = initial;
    }

    @Override
    public String getTitle() {
        return "Reports";
    }

    @Override
    public Node build() {
        ComboBox<String> kind = new ComboBox<>();
        kind.getItems().addAll("Occupancy", "Revenue", "Housekeeping", "Guests");
        kind.setValue(initial);
        DatePicker from = new DatePicker(LocalDate.now().minusDays(6));
        DatePicker to = new DatePicker(LocalDate.now());
        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPrefHeight(480);
        output.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 13px;");
        Button show = new Button("Show Report");
        show.setDefaultButton(true);
        show.setOnAction(e -> {
            if (from.getValue() == null || to.getValue() == null || to.getValue().isBefore(from.getValue())) {
                app.error("Choose a valid date range.");
                return;
            }
            Report report = create(kind.getValue(), from.getValue(), to.getValue());
            output.setText(report.render());
            app.setStatus(report.getTitle() + " report created");
        });
        VBox root = new VBox(12, card("Report", row(new Label("Report:"), kind, new Label("From:"), from, new Label("To:"), to, show)), card("Result", output));
        root.setPadding(new Insets(14));
        show.fire();
        return root;
    }

    private Report create(String kind, LocalDate from, LocalDate to) {
        switch (kind) {
            case "Revenue":
                return new RevenueReport(app.getStore(), from, to);
            case "Housekeeping":
                return new HousekeepingReport(app.getStore(), from, to);
            case "Guests":
                return new GuestReport(app.getStore(), from, to);
            default:
                return new OccupancyReport(app.getStore(), from, to);
        }
    }

}
