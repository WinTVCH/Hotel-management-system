package hotel.storage;

import com.google.gson.JsonObject;
import hotel.people.UserAccount;

public class UserImporter extends JsonImporter<UserAccount> {

    @Override
    protected UserAccount parseItem(JsonObject item) {
        return null; // TODO
    }

}
