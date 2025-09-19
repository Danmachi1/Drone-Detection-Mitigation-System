package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.swarm.SwarmManager;
import fusion.SensorFusionEngine;
import sensors.MultiSensorManager;
import storage.DataPersistenceEngine;
import main.MainApp;

/**
 * 🖥️ Operator Command Center window.
 */
public class MainUI extends Application {
	  /* === backend – injected by MainApp before launch ============= */
    private static MainApp backend;
    public  static void setBackend(MainApp b) { backend = b; }
    /* ================  fields *without* singleton look-ups  ================ */
    private SwarmManager       swarm;
    private SensorFusionEngine fusion;
    private MultiSensorManager sensors;

    private final DataPersistenceEngine store =
        new DataPersistenceEngine("tracks.db", "track_log.csv", false);

    /* --------------------------------------------------------------------- */
    @Override
    public void start(Stage primary) {

        /* -------- now it’s safe to access MainApp.get() ------------------- */
        this.swarm   = MainApp.get().getSwarm();
        this.fusion  = MainApp.get().getFusion();
        this.sensors = MainApp.get().getSensors();

        TabPane rootTabs = new TabPane();

        rootTabs.getTabs().add(tab("🗺 Map",
                uiSafe(() -> new LiveMapPane(sensors))));

        rootTabs.getTabs().add(tab("🚁 Drones",
                uiSafe(() -> new DroneControlPanel(swarm))));

        rootTabs.getTabs().add(tab("🔋 Battery",
                uiSafe(() -> new BatteryStatusPanel(swarm))));

        rootTabs.getTabs().add(tab("⚠ Alerts",
                uiSafe(AlertPanel::new)));

        rootTabs.getTabs().add(tab("☁ Sensors",
                uiSafe(() -> new SensorTogglePanel(sensors))));

        rootTabs.getTabs().add(tab("⚙ Config",
                uiSafe(ConfigControlPanel::new)));
        rootTabs.getTabs().add(tab("📜 Logs",
                uiSafe(SystemLogPanel::new)));


        rootTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane root = new BorderPane(rootTabs);
        Scene scene = new Scene(root, 1400, 900);

        primary.setTitle("🛡 SkyShield – Operator Command Center");
        primary.setScene(scene);
        primary.show();
    }
    /* --------------------------------------------------------------------- */
    private Tab tab(String title, javafx.scene.Node content) {
        return new Tab(title, content == null
                ? new javafx.scene.control.Label("Pane not yet implemented")
                : content);
    }
    private interface PaneFactory { javafx.scene.Node create(); }
    private javafx.scene.Node uiSafe(PaneFactory pf) {
        try { return pf.create(); }
        catch (Throwable t) {
            return new javafx.scene.control.Label("Unavailable: " + t.getMessage());
        }
    }
}
