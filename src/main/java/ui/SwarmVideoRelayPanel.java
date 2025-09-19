package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import logic.visual.VisualRelayManager;

import java.util.List;

/**
 * 🎥 SwarmVideoRelayPanel - Displays live video feeds from drones.
 * Supports switching drone and view mode (FalconVision, IR, MultiView, Normal).
 */
public class SwarmVideoRelayPanel extends VBox {

    private final ComboBox<String> droneSelector = new ComboBox<>();
    private final ComboBox<String> visionModeSelector = new ComboBox<>();
    private final Label statusLabel = new Label("🎥 Select drone and view mode.");
    private final Label videoPlaceholder = new Label("[ 🔴 Live Video Feed Placeholder ]");

    public SwarmVideoRelayPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(
            new Label("🎥 Swarm Video Relay Panel"),
            droneSelector,
            visionModeSelector,
            buildControlButton(),
            videoPlaceholder,
            statusLabel
        );

        loadDrones();
        loadVisionModes();
    }

    private void loadDrones() {
        List<String> drones = VisualRelayManager.getAvailableDrones();
        droneSelector.getItems().clear();
        droneSelector.getItems().addAll(drones);
    }

    private void loadVisionModes() {
        visionModeSelector.getItems().clear();
        visionModeSelector.getItems().addAll("Normal", "FalconVision", "Infrared", "MultiView");
        visionModeSelector.setValue("Normal");
    }

    private Button buildControlButton() {
        Button loadFeedBtn = new Button("📺 Load Feed");
        loadFeedBtn.setOnAction(e -> {
            String droneId = droneSelector.getValue();
            String mode = visionModeSelector.getValue();

            if (droneId == null || mode == null) {
                statusLabel.setText("❗ Please select drone and vision mode.");
                return;
            }

            boolean success = VisualRelayManager.requestStream(droneId, mode);
            if (success) {
                videoPlaceholder.setText("[ ✅ Displaying " + mode + " for " + droneId + " ]");
                statusLabel.setText("✅ Stream active.");
            } else {
                statusLabel.setText("❌ Failed to load video stream.");
            }
        });

        return loadFeedBtn;
    }
}
