package hotel.housekeeping;

public class SupplyItem {
    private String name;
    private int quantity;
    private int reorderLevel;

    public SupplyItem(String name, int quantity, int reorderLevel) {
        this.name = name;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public boolean use(int amount) {
        if (amount > quantity) {
            return false;
        }
        quantity -= amount;
        return true;
    }

    public void restock(int amount) {
        quantity += amount;
    }

    public boolean isLow() {
        return quantity <= reorderLevel;
    }

}
