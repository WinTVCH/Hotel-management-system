package hotel.storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportReport {
    private Map<String, Integer> loaded = new LinkedHashMap<>();
    private List<ItemError> errors = new ArrayList<>();

    public void addLoaded(String fileName, int count) {
        loaded.put(fileName, count);
    }

    public void addError(ItemError error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<ItemError> getErrors() {
        return errors;
    }

    public int totalLoaded() {
        int total = 0;
        for (int count : loaded.values()) {
            total += count;
        }
        return total;
    }

    public String summary() {
        StringBuilder text = new StringBuilder();
        text.append("Loaded ").append(totalLoaded()).append(" records from ").append(loaded.size()).append(" files");
        for (ItemError error : errors) {
            text.append("\n").append(error);
        }
        return text.toString();
    }

}
