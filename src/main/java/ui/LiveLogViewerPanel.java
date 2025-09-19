package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.logging.LogManager;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 📜 LiveLogViewerPanel - Displays live logs from all subsystems.
 * Supports export, scrollable view, and live diagnostic output.
 */
public class LiveLogViewerPanel extends VBox {

    private final TextArea logArea = new TextArea();
    private final Button clearBtn = new Button("🧹 Clear");
    private final Button exportBtn = new Button("💾 Export");

    public LiveLogViewerPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(20);

        getChildren().addAll(
            new Label("📜 Live System Logs"),
            logArea,
            new HBox(10, clearBtn, exportBtn)
        );

        clearBtn.setOnAction(e -> logArea.clear());
        exportBtn.setOnAction(e -> exportLog());

        subscribeToLogs();
    }

    private void subscribeToLogs() {
        utils.logging.LogManager.addLogListener(new utils.logging.LogManager.LogListener() {
            @Override
            public void onLog(long timestamp, String level, String message) {
                Platform.runLater(() -> {
                    if (logArea.getText().length() > 100_000) {
                        logArea.clear(); // Prevent excessive buildup
                    }
                    logArea.appendText("[" + timestamp + "][" + level + "] " + message + "\n");
                });
            }
        });
    }


    private void exportLog() {
        try {
            String filename = "skyshield-log-" + LocalDateTime.now().toString().replace(":", "-") + ".txt";
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(logArea.getText());
            }
            showConfirmation("✅ Log saved as " + filename);
        } catch (IOException e) {
            showConfirmation("❌ Failed to save log.");
        }
    }

    private void showConfirmation(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
