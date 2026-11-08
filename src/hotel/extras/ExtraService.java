package hotel.extras;

import hotel.finance.Chargeable;
import hotel.finance.InvoiceLine;

public abstract class ExtraService implements Chargeable {
    private String id;
    private String name;
    private double unitPrice;
    private String unit;
    private String description;
    private int quantity = 1;

    public ExtraService(String id, String name, double unitPrice, String unit, String description) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public abstract double calculateCharge();

    public abstract String getCategory();

    public abstract String getDetails();

    @Override
    public InvoiceLine toInvoiceLine() {
        return new InvoiceLine(name, quantity, unitPrice, calculateCharge());
    }

    @Override
    public String toString() {
        return name;
    }

}
