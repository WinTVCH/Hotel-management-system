package hotel.storage;

public class ItemError {
    private String fileName;
    private int index;
    private String message;

    public ItemError(String fileName, int index, String message) {
        this.fileName = fileName;
        this.index = index;
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public int getIndex() {
        return index;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return index < 0 ? fileName + ": " + message : fileName + " item " + index + ": " + message;
    }

}
