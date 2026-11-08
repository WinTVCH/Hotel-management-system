package hotel.storage;

import com.google.gson.JsonObject;
import hotel.extras.LocalPartner;

public class LocalPartnerImporter extends JsonImporter<LocalPartner> {

    public LocalPartnerImporter(DataStore store) {
        super("local_partners.json", store);
    }

    @Override
    protected LocalPartner parseItem(JsonObject item) {
        return new LocalPartner(text(item, "id"), text(item, "name"), text(item, "phone", ""), number(item, "commission", 0));
    }

    @Override
    protected void addToStore(LocalPartner item) {
        store.getPartners().add(item);
    }

}
