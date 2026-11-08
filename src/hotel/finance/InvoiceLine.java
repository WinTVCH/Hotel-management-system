package hotel.finance;

public class InvoiceLine {
    private String item;
    private int quantity;
    private double unitPrice;
    private double amount;

    public InvoiceLine(String item, int quantity, double unitPrice, double amount) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getAmount() {
        return amount;
    }

}
