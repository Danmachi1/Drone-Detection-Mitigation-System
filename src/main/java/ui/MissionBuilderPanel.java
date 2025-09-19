package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import logic.mission.MissionManager;
import logic.mission.MissionProfile;

import java.util.Optional;

/**
 * 🧭 MissionBuilderPanel - UI to create, configure, and save/load swarm missions.
 * Defines target zone, drone count, and behavior (e.g. FalconVision).
 */
public class MissionBuilderPanel extends VBox {

    private final TextField missionIdField = new TextField();
    private final TextField zoneNameField = new TextField();
    private final Spinner<Integer> droneCountSpinner = new Spinner<>(1, 50, 5);
    private final ComboBox<String> visionModeCombo = new ComboBox<>();
    private final Label statusLabel = new Label("🧭 Ready to configure mission.");

    public MissionBuilderPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        visionModeCombo.getItems().addAll("Normal", "FalconVision", "Infrared", "MultiView");
        visionModeCombo.setValue("Normal");

        getChildren().addAll(
            new Label("🧭 Mission ID"), missionIdField,
            new Label("📍 Zone / Area Name"), zoneNameField,
            new Label("🚁 Number of Drones"), droneCountSpinner,
            new Label("🛰 Vision Mode"), visionModeCombo,
            buildButtons(),
            statusLabel
        );
    }

    private HBox buildButtons() {
        Button saveBtn = new Button("💾 Save Mission");
        Button loadBtn = new Button("📂 Load Mission");

        saveBtn.setOnAction(e -> saveMission());
        loadBtn.setOnAction(e -> loadMission());

        return new HBox(10, saveBtn, loadBtn);
    }

    private void saveMission() {
        String id = missionIdField.getText().trim();
        String zone = zoneNameField.getText().trim();
        int drones = droneCountSpinner.getValue();
        String mode = visionModeCombo.getValue();

        if (id.isEmpty() || zone.isEmpty()) {
            statusLabel.setText("❗ Mission ID and zone required.");
            return;
        }

        MissionProfile mission = new MissionProfile(id, zone, drones, mode);
        boolean success = MissionManager.saveMission(mission);
        statusLabel.setText(success ? "✅ Mission saved." : "❌ Save failed.");
    }

    private void loadMission() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("🔍 Load Existing Mission");
        dialog.setContentText("Enter Mission ID:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(id -> {
            MissionProfile m = MissionManager.loadMission(id);
            if (m == null) {
                statusLabel.setText("❌ Mission not found.");
            } else {
                missionIdField.setText(m.getId());
                zoneNameField.setText(m.getZone());
                droneCountSpinner.getValueFactory().setValue(m.getDroneCount());
                visionModeCombo.setValue(m.getVisionMode());
                statusLabel.setText("✅ Mission loaded.");
            }
        });
    }
}
