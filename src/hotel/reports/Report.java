package hotel.reports;

import hotel.storage.DataStore;
import java.time.LocalDate;

public abstract class Report {
    private String title;
    protected DataStore store;
    private LocalDate from;
    private LocalDate to;

    public Report(String title, DataStore store, LocalDate from, LocalDate to) {
        this.title = title;
        this.store = store;
        this.from = from;
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    protected String header() {
        return title + " Report\nPeriod: " + from + " to " + to + "\n" + "-".repeat(56) + "\n";
    }

    protected String money(double amount) {
        return String.format("%,.2f", amount);
    }

    public abstract String render();

}
