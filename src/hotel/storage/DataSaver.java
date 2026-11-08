package hotel.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotel.bookings.Booking;
import hotel.bookings.GroupBooking;
import hotel.bookings.Stay;
import hotel.extras.ExtraService;
import hotel.extras.LaundryService;
import hotel.extras.LocalPartner;
import hotel.extras.MealService;
import hotel.extras.TourActivity;
import hotel.extras.TourPackage;
import hotel.extras.TransportService;
import hotel.finance.Invoice;
import hotel.finance.InvoiceLine;
import hotel.finance.Payment;
import hotel.housekeeping.HousekeepingTask;
import hotel.housekeeping.SupplyItem;
import hotel.people.Guest;
import hotel.people.Role;
import hotel.people.Staff;
import hotel.people.UserAccount;
import hotel.pricing.LongStayPromotion;
import hotel.pricing.Promotion;
import hotel.rooms.Bungalow;
import hotel.rooms.FamilyRoom;
import hotel.rooms.Room;
import hotel.rooms.RoomRate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataSaver {
    private DataStore store;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DataSaver(DataStore store) {
        this.store = store;
    }

    public void saveAll(Path dataDir) {
        write(dataDir, "users.json", users());
        write(dataDir, "guests.json", guests());
        write(dataDir, "staff.json", staff());
        write(dataDir, "rooms.json", rooms());
        write(dataDir, "room_rates.json", rates());
        write(dataDir, "promotions.json", promotions());
        write(dataDir, "local_partners.json", partners());
        write(dataDir, "tour_activities.json", activities());
        write(dataDir, "services.json", services());
        write(dataDir, "bookings.json", bookings());
        write(dataDir, "stays.json", stays());
        write(dataDir, "housekeeping_tasks.json", tasks());
        write(dataDir, "supplies.json", supplies());
    }

    private void write(Path dataDir, String fileName, JsonArray array) {
        try {
            Files.createDirectories(dataDir);
            Files.writeString(dataDir.resolve(fileName), gson.toJson(array));
        } catch (IOException e) {
            System.err.println("Could not save " + fileName + ": " + e.getMessage());
        }
    }

    private JsonArray users() {
        JsonArray array = new JsonArray();
        for (UserAccount account : store.getAccounts()) {
            JsonObject item = new JsonObject();
            item.addProperty("username", account.getUsername());
            item.addProperty("passwordHash", account.getPasswordHash());
            item.addProperty("type", account.getType().name());
            item.addProperty("ownerId", account.getOwnerId());
            item.addProperty("active", account.isActive());
            array.add(item);
        }
        return array;
    }

    private JsonArray guests() {
        JsonArray array = new JsonArray();
        for (Guest guest : store.guestsById()) {
            JsonObject item = new JsonObject();
            item.addProperty("guestId", guest.getGuestId());
            item.addProperty("name", guest.getName());
            item.addProperty("phone", guest.getPhone());
            item.addProperty("email", guest.getEmail());
            item.addProperty("idNumber", guest.getIdNumber());
            array.add(item);
        }
        return array;
    }

    private JsonArray staff() {
        JsonArray array = new JsonArray();
        for (Staff member : store.getStaff()) {
            JsonObject item = new JsonObject();
            item.addProperty("staffId", member.getStaffId());
            item.addProperty("name", member.getName());
            item.addProperty("phone", member.getPhone());
            item.addProperty("email", member.getEmail());
            item.addProperty("salary", member.getSalary());
            item.addProperty("shift", member.getShift());
            JsonArray roles = new JsonArray();
            for (Role role : member.getRoles()) {
                JsonObject data = new JsonObject();
                data.addProperty("type", role.getClass().getSimpleName());
                data.addProperty("roleId", role.getRoleId());
                data.addProperty("rank", role.getRank());
                roles.add(data);
            }
            item.add("roles", roles);
            array.add(item);
        }
        return array;
    }

    private JsonArray rooms() {
        JsonArray array = new JsonArray();
        for (Room room : store.getProperty().getRooms()) {
            JsonObject item = new JsonObject();
            item.addProperty("number", room.getRoomNumber());
            item.addProperty("type", room.getTypeName());
            item.addProperty("price", room.getPrice());
            item.addProperty("status", room.getStatus().name());
            if (room instanceof FamilyRoom) {
                item.addProperty("extraBeds", ((FamilyRoom) room).getExtraBeds());
            }
            if (room instanceof Bungalow) {
                item.addProperty("cleaningFee", ((Bungalow) room).getCleaningFee());
            }
            array.add(item);
        }
        return array;
    }

    private JsonArray rates() {
        JsonArray array = new JsonArray();
        for (RoomRate rate : store.getRates()) {
            JsonObject item = new JsonObject();
            item.addProperty("name", rate.getName());
            item.addProperty("roomType", rate.getRoomType());
            item.addProperty("from", rate.getFrom().toString());
            item.addProperty("to", rate.getTo().toString());
            item.addProperty("percent", rate.getPercent());
            array.add(item);
        }
        return array;
    }

    private JsonArray promotions() {
        JsonArray array = new JsonArray();
        for (Promotion promotion : store.getPromotions()) {
            JsonObject item = new JsonObject();
            item.addProperty("code", promotion.getCode());
            item.addProperty("type", promotion.getType());
            item.addProperty("description", promotion.getDescription());
            item.addProperty("value", promotion.getValue());
            if (promotion instanceof LongStayPromotion) {
                item.addProperty("minNights", ((LongStayPromotion) promotion).getMinNights());
            }
            array.add(item);
        }
        return array;
    }

    private JsonArray partners() {
        JsonArray array = new JsonArray();
        for (LocalPartner partner : store.getPartners()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", partner.getId());
            item.addProperty("name", partner.getName());
            item.addProperty("phone", partner.getPhone());
            item.addProperty("commission", partner.getCommission());
            array.add(item);
        }
        return array;
    }

    private JsonArray activities() {
        JsonArray array = new JsonArray();
        for (TourActivity activity : store.getActivities()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", activity.getId());
            item.addProperty("name", activity.getName());
            item.addProperty("minutes", activity.getMinutes());
            item.addProperty("extraCost", activity.getExtraCost());
            item.addProperty("partnerId", activity.getPartner() == null ? "" : activity.getPartner().getId());
            array.add(item);
        }
        return array;
    }

    private JsonArray services() {
        JsonArray array = new JsonArray();
        for (ExtraService service : store.getServices()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", service.getId());
            item.addProperty("name", service.getName());
            item.addProperty("unitPrice", service.getUnitPrice());
            item.addProperty("description", service.getDescription());
            if (service instanceof MealService) {
                item.addProperty("type", "MEAL");
                item.addProperty("mealType", ((MealService) service).getMealType());
            } else if (service instanceof LaundryService) {
                item.addProperty("type", "LAUNDRY");
                item.addProperty("express", ((LaundryService) service).isExpress());
            } else if (service instanceof TransportService) {
                item.addProperty("type", "TRANSPORT");
                item.addProperty("route", ((TransportService) service).getRoute());
                item.addProperty("maxPassengers", ((TransportService) service).getMaxPassengers());
            } else if (service instanceof TourPackage) {
                item.addProperty("type", "TOUR");
                JsonArray ids = new JsonArray();
                for (TourActivity activity : ((TourPackage) service).getActivities()) {
                    ids.add(activity.getId());
                }
                item.add("activities", ids);
            }
            array.add(item);
        }
        return array;
    }

    private JsonArray bookings() {
        JsonArray array = new JsonArray();
        for (Booking booking : store.getBookings()) {
            JsonObject item = new JsonObject();
            item.addProperty("code", booking.getCode());
            item.addProperty("guestId", booking.getGuest().getGuestId());
            JsonArray rooms = new JsonArray();
            for (Room room : booking.getRooms()) {
                rooms.add(room.getRoomNumber());
            }
            item.add("rooms", rooms);
            item.addProperty("checkIn", booking.getCheckIn().toString());
            item.addProperty("checkOut", booking.getCheckOut().toString());
            item.addProperty("status", booking.getStatus().name());
            item.addProperty("channel", booking.getChannel().name());
            if (booking.getPromotion() != null) {
                item.addProperty("promotion", booking.getPromotion().getCode());
            }
            item.addProperty("requests", booking.getRequests());
            if (booking.getDeposit() != null) {
                item.add("deposit", payment(booking.getDeposit()));
            }
            if (booking instanceof GroupBooking) {
                GroupBooking group = (GroupBooking) booking;
                item.addProperty("groupName", group.getGroupName());
                JsonArray members = new JsonArray();
                for (Guest member : group.getMembers()) {
                    members.add(member.getGuestId());
                }
                item.add("members", members);
            }
            array.add(item);
        }
        return array;
    }

    private JsonArray stays() {
        JsonArray array = new JsonArray();
        for (Stay stay : store.getStays()) {
            JsonObject item = new JsonObject();
            item.addProperty("bookingCode", stay.getBooking().getCode());
            item.addProperty("checkedInAt", stay.getCheckedInAt().toString());
            if (stay.getCheckedOutAt() != null) {
                item.addProperty("checkedOutAt", stay.getCheckedOutAt().toString());
            }
            Invoice invoice = stay.getInvoice();
            if (invoice != null) {
                JsonObject data = new JsonObject();
                data.addProperty("number", invoice.getNumber());
                JsonArray lines = new JsonArray();
                for (InvoiceLine line : invoice.getLines()) {
                    JsonObject lineData = new JsonObject();
                    lineData.addProperty("item", line.getItem());
                    lineData.addProperty("quantity", line.getQuantity());
                    lineData.addProperty("unitPrice", line.getUnitPrice());
                    lineData.addProperty("amount", line.getAmount());
                    lines.add(lineData);
                }
                data.add("lines", lines);
                JsonArray payments = new JsonArray();
                for (Payment payment : invoice.getPayments()) {
                    payments.add(payment(payment));
                }
                data.add("payments", payments);
                item.add("invoice", data);
            }
            array.add(item);
        }
        return array;
    }

    private JsonArray tasks() {
        JsonArray array = new JsonArray();
        for (HousekeepingTask task : store.getTasks()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", task.getId());
            item.addProperty("room", task.getRoom().getRoomNumber());
            item.addProperty("job", task.getJob());
            item.addProperty("priority", task.getPriority().name());
            item.addProperty("assignee", task.getAssignee() == null ? 0 : task.getAssignee().getStaffId());
            item.addProperty("done", task.isDone());
            item.addProperty("createdAt", task.getCreatedAt().toString());
            array.add(item);
        }
        return array;
    }

    private JsonArray supplies() {
        JsonArray array = new JsonArray();
        for (SupplyItem supply : store.getInventory().getItems()) {
            JsonObject item = new JsonObject();
            item.addProperty("name", supply.getName());
            item.addProperty("quantity", supply.getQuantity());
            item.addProperty("reorderLevel", supply.getReorderLevel());
            array.add(item);
        }
        return array;
    }

    private JsonObject payment(Payment payment) {
        JsonObject item = new JsonObject();
        item.addProperty("method", payment.getMethod());
        item.addProperty("amount", payment.getAmount());
        item.addProperty("reference", payment.getReference());
        if (payment.getPaidAt() != null) {
            item.addProperty("paidAt", payment.getPaidAt().toString());
        }
        return item;
    }

}
