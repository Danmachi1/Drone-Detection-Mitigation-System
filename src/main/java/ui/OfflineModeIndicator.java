package ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import main.config.Config;
import main.config.UIModeConstants;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 🔌 OfflineModeIndicator - Shows live status of offline/fallback mode for operators.
 */
public class OfflineModeIndicator extends HBox {

    private final Label statusLabel = new Label("🟢 Checking...");

    public OfflineModeIndicator() {
        getChildren().add(statusLabel);
        refresh();

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> refresh());
            }
        }, 0, 4000);
    }

    private void refresh() {
        boolean isOffline = UIModeConstants.IS_OFFLINE_MODE;
        boolean usingSimulation = UIModeConstants.IS_OFFLINE_MODE;

        if (isOffline && usingSimulation) {
            statusLabel.setText("📴 Offline Mode + Simulation Active");
            statusLabel.setStyle("-fx-text-fill: orange;");
        } else if (isOffline) {
            statusLabel.setText("📴 Offline Mode");
            statusLabel.setStyle("-fx-text-fill: red;");
        } else if (usingSimulation) {
            statusLabel.setText("🧪 Simulation Mode");
            statusLabel.setStyle("-fx-text-fill: blue;");
        } else {
            statusLabel.setText("✅ Online / Live Sensors");
            statusLabel.setStyle("-fx-text-fill: green;");
        }
    }
}
