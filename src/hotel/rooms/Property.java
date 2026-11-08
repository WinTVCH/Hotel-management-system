package hotel.rooms;

import java.util.ArrayList;
import java.util.List;

public class Property {
    private String name;
    private String address;
    private List<Room> rooms = new ArrayList<>();

    public Property(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    public Room findRoom(String roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    public int countByStatus(RoomStatus status) {
        int count = 0;
        for (Room room : rooms) {
            if (room.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public List<Room> roomsOfType(String typeName) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            if (typeName == null || room.getTypeName().equals(typeName)) {
                result.add(room);
            }
        }
        return result;
    }

}
