package hotel.storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hotel.finance.CardPayment;
import hotel.finance.CashPayment;
import hotel.finance.Payment;
import hotel.finance.TransferPayment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class JsonImporter<T> {
    private String fileName;
    protected DataStore store;

    public JsonImporter(String fileName, DataStore store) {
        this.fileName = fileName;
        this.store = store;
    }

    public String getFileName() {
        return fileName;
    }

    public List<T> importAll(Path dataDir, ImportReport report) {
        List<T> items = new ArrayList<>();
        Path file = dataDir.resolve(fileName);
        String text;
        try {
            text = Files.exists(file) ? Files.readString(file) : "";
        } catch (IOException e) {
            report.addError(new ItemError(fileName, -1, e.getMessage()));
            return items;
        }
        if (text.isBlank()) {
            report.addLoaded(fileName, 0);
            return items;
        }
        JsonArray array;
        try {
            array = JsonParser.parseString(text).getAsJsonArray();
        } catch (RuntimeException e) {
            report.addError(new ItemError(fileName, -1, "Not a JSON array"));
            return items;
        }
        for (int i = 0; i < array.size(); i++) {
            try {
                T item = parseItem(array.get(i).getAsJsonObject());
                addToStore(item);
                items.add(item);
            } catch (RuntimeException e) {
                report.addError(new ItemError(fileName, i, e.getMessage() == null ? e.toString() : e.getMessage()));
            }
        }
        report.addLoaded(fileName, items.size());
        return items;
    }

    protected abstract T parseItem(JsonObject item);

    protected abstract void addToStore(T item);

    protected String text(JsonObject item, String key) {
        JsonElement value = item.get(key);
        if (value == null || value.isJsonNull()) {
            throw new IllegalArgumentException("Missing \"" + key + "\"");
        }
        return value.getAsString();
    }

    protected String text(JsonObject item, String key, String fallback) {
        JsonElement value = item.get(key);
        return value == null || value.isJsonNull() ? fallback : value.getAsString();
    }

    protected double number(JsonObject item, String key) {
        return Double.parseDouble(text(item, key));
    }

    protected double number(JsonObject item, String key, double fallback) {
        return item.has(key) && !item.get(key).isJsonNull() ? item.get(key).getAsDouble() : fallback;
    }

    protected int integer(JsonObject item, String key) {
        return Integer.parseInt(text(item, key));
    }

    protected int integer(JsonObject item, String key, int fallback) {
        return item.has(key) && !item.get(key).isJsonNull() ? item.get(key).getAsInt() : fallback;
    }

    protected boolean flag(JsonObject item, String key, boolean fallback) {
        return item.has(key) && !item.get(key).isJsonNull() ? item.get(key).getAsBoolean() : fallback;
    }

    protected LocalDate date(JsonObject item, String key) {
        return LocalDate.parse(text(item, key));
    }

    protected LocalDateTime dateTime(JsonObject item, String key) {
        String value = text(item, key, null);
        return value == null ? null : LocalDateTime.parse(value);
    }

    protected Payment payment(JsonObject item) {
        String method = text(item, "method");
        double amount = number(item, "amount");
        String reference = text(item, "reference", "");
        Payment payment;
        if (method.equals("Cash")) {
            payment = new CashPayment(amount, reference.isEmpty() ? amount : Double.parseDouble(reference));
        } else if (method.equals("Transfer")) {
            payment = new TransferPayment(amount, reference);
        } else if (method.equals("Card")) {
            payment = new CardPayment(amount, reference);
        } else {
            throw new IllegalArgumentException("Unknown payment method " + method);
        }
        payment.setPaidAt(dateTime(item, "paidAt"));
        return payment;
    }

}
