package hotel.rooms;

import hotel.people.*;
import java.util.List;
import java.util.ArrayList;

public abstract class Room {
    private String RoomNumber;
    private double Price;
    private List<Person> Guests = new ArrayList<>();

    public Room(String RoomNumber, double Price) {
        this.RoomNumber = RoomNumber
        this.Price = Price
    }

    public String getRoomNumber() {
        return this.RoomNumber;
    }

    protected double getPrice() {
        return this.Price;
    }

    public List<Person> getGuests() {
        return Guests;
    }

    public abstract double calculateRate();
}