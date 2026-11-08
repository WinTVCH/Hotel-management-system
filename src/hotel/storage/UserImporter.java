package hotel.storage;

import com.google.gson.JsonObject;
import hotel.people.AccountType;
import hotel.people.UserAccount;

public class UserImporter extends JsonImporter<UserAccount> {

    public UserImporter(DataStore store) {
        super("users.json", store);
    }

    @Override
    protected UserAccount parseItem(JsonObject item) {
        String password = text(item, "password", null);
        String hash = password == null ? text(item, "passwordHash") : UserAccount.hash(password);
        AccountType type = AccountType.valueOf(text(item, "type"));
        return new UserAccount(text(item, "username"), hash, type, integer(item, "ownerId"), flag(item, "active", true));
    }

    @Override
    protected void addToStore(UserAccount item) {
        store.addAccount(item);
    }

}
