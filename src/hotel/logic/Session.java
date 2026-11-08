package hotel.logic;

import hotel.people.AccountType;
import hotel.people.Guest;
import hotel.people.Person;
import hotel.people.Staff;
import hotel.people.UserAccount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private UserAccount account;
    private Person person;
    private LocalDateTime startedAt;

    public Session(UserAccount account, Person person) {
        this.account = account;
        this.person = person;
        this.startedAt = LocalDateTime.now();
    }

    public UserAccount getAccount() {
        return account;
    }

    public Person getPerson() {
        return person;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public boolean isGuest() {
        return account.getType() == AccountType.GUEST;
    }

    public Guest getGuest() {
        return isGuest() ? (Guest) person : null;
    }

    public Staff getStaff() {
        return isGuest() ? null : (Staff) person;
    }

    public List<String> getMenuItems() {
        if (isGuest()) {
            List<String> items = new ArrayList<>();
            items.add("My Stays");
            items.add("Book a Room");
            return items;
        }
        return getStaff().getMenuItems();
    }

    public String getRoleLabel() {
        if (isGuest()) {
            return "Guest";
        }
        return getStaff().getMainRole() == null ? "Staff" : getStaff().getMainRole().getName();
    }

    public LocalDate today() {
        return LocalDate.now();
    }

}
