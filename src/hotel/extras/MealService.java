package hotel.extras;

public class MealService extends ExtraService {
    private String mealType;

    public MealService(String id, String name, double unitPrice, String description, String mealType) {
        super(id, name, unitPrice, "person", description);
        this.mealType = mealType;
    }

    public String getMealType() {
        return mealType;
    }

    @Override
    public double calculateCharge() {
        return getUnitPrice() * getQuantity();
    }

    @Override
    public String getCategory() {
        return "Meals";
    }

    @Override
    public String getDetails() {
        return mealType + ", per person";
    }

}
