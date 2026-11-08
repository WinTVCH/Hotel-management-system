package hotel.logic;

import hotel.errors.ValidationException;
import hotel.people.AccountType;
import hotel.people.Guest;
import hotel.people.Person;
import hotel.people.UserAccount;
import hotel.storage.DataStore;

public class AuthService {
    private DataStore store;

    public AuthService(DataStore store) {
        this.store = store;
    }

    public Session login(String username, String password, AccountType expected) throws ValidationException {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new ValidationException("Enter your user name and password.");
        }
        UserAccount account = store.findAccount(username);
        if (account == null || !account.checkPassword(password)) {
            throw new ValidationException("The user name or password is incorrect.");
        }
        if (account.getType() != expected) {
            throw new ValidationException(expected == AccountType.GUEST ? "This is a staff account. Choose Staff to sign in." : "This is a guest account. Choose Guest to sign in.");
        }
        Person owner = store.ownerOf(account);
        if (owner == null) {
            throw new ValidationException("This account is not linked to a person.");
        }
        return new Session(account, owner);
    }

    public Session registerGuest(String name, String phone, String email, String idNumber, String password, String confirm) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Enter your full name.");
        }
        if (email == null || !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new ValidationException("Enter a valid e-mail address. It is your user name.");
        }
        if (store.findAccount(email) != null) {
            throw new ValidationException("An account with this e-mail already exists.");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("The password must be at least 6 characters.");
        }
        if (!password.equals(confirm)) {
            throw new ValidationException("Passwords do not match.");
        }
        Guest guest = new Guest(name.trim(), phone == null ? "" : phone.trim(), email.trim(), store.nextGuestId(), idNumber == null ? "" : idNumber.trim());
        store.addGuest(guest);
        UserAccount account = UserAccount.create(email.trim(), password, AccountType.GUEST, guest.getGuestId());
        store.addAccount(account);
        store.save();
        return new Session(account, guest);
    }

    public void changePassword(Session session, String oldPassword, String newPassword) throws ValidationException {
        if (newPassword == null || newPassword.length() < 6) {
            throw new ValidationException("The new password must be at least 6 characters.");
        }
        if (!session.getAccount().changePassword(oldPassword, newPassword)) {
            throw new ValidationException("The current password is incorrect.");
        }
        store.save();
    }

}
