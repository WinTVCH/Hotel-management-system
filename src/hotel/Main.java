package hotel;

import hotel.storage.DataLoader;
import hotel.storage.DataStore;
import hotel.storage.ImportReport;
import hotel.ui.ScreenManager;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        DataStore store = new DataStore();
        ImportReport report = new DataLoader(store).loadAll(Paths.get("data"));
        System.out.println(report.summary());
        ScreenManager app = new ScreenManager(stage, store);
        app.showLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
