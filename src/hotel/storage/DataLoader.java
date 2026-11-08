package hotel.storage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    private DataStore store;

    public DataLoader(DataStore store) {
        this.store = store;
    }

    public ImportReport loadAll(Path dataDir) {
        ImportReport report = new ImportReport();
        List<JsonImporter<?>> importers = new ArrayList<>();
        importers.add(new UserImporter(store));
        importers.add(new GuestImporter(store));
        importers.add(new StaffImporter(store));
        importers.add(new RoomImporter(store));
        importers.add(new RoomRateImporter(store));
        importers.add(new PromotionImporter(store));
        importers.add(new LocalPartnerImporter(store));
        importers.add(new TourActivityImporter(store));
        importers.add(new ServiceImporter(store));
        importers.add(new BookingImporter(store));
        importers.add(new StayImporter(store));
        importers.add(new HousekeepingTaskImporter(store));
        importers.add(new SupplyImporter(store));
        for (JsonImporter<?> importer : importers) {
            importer.importAll(dataDir, report);
        }
        store.setDataDir(dataDir);
        return report;
    }

}
