package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import logic.control.SystemControlManager;

/**
 * 🎮 OperatorControlPanel - Master override and configuration panel.
 * Allows toggling system AI modes, sensor modules, and storage options.
 */
public class OperatorControlPanel extends VBox {

    private final ComboBox<String> aiModeSelector = new ComboBox<>();
    private final CheckBox radarToggle = new CheckBox("📡 Radar");
    private final CheckBox rfToggle = new CheckBox("📶 RF");
    private final CheckBox visualToggle = new CheckBox("📷 Visual");
    private final CheckBox acousticToggle = new CheckBox("🎙 Acoustic");
    private final CheckBox sqlLoggingToggle = new CheckBox("🗄 SQL Storage");
    private final CheckBox overrideLockToggle = new CheckBox("🔐 Override Lock");
    private final ToggleButton systemActiveBtn = new ToggleButton("🟢 System Active");

    private final Label statusLabel = new Label("🎮 Operator control ready.");

    public OperatorControlPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        aiModeSelector.getItems().addAll("AI", "Rule-Based", "Hybrid");
        aiModeSelector.setValue("Hybrid");

        getChildren().addAll(
            new Label("🎮 Operator Master Control"),
            new Label("🧠 AI Mode"), aiModeSelector,
            radarToggle, rfToggle, visualToggle, acousticToggle,
            sqlLoggingToggle, overrideLockToggle, systemActiveBtn,
            statusLabel
        );

        wireActions();
    }

    private void wireActions() {
        aiModeSelector.setOnAction(e -> {
            String mode = aiModeSelector.getValue();
            SystemControlManager.setAiMode(mode);
            statusLabel.setText("✅ AI mode set to " + mode);
        });

        radarToggle.setOnAction(e -> SystemControlManager.toggleSensor("radar", radarToggle.isSelected()));
        rfToggle.setOnAction(e -> SystemControlManager.toggleSensor("rf", rfToggle.isSelected()));
        visualToggle.setOnAction(e -> SystemControlManager.toggleSensor("visual", visualToggle.isSelected()));
        acousticToggle.setOnAction(e -> SystemControlManager.toggleSensor("acoustic", acousticToggle.isSelected()));

        sqlLoggingToggle.setOnAction(e -> SystemControlManager.setLoggingMode(sqlLoggingToggle.isSelected() ? "sql" : "json"));

        overrideLockToggle.setOnAction(e -> SystemControlManager.setOverrideLock(overrideLockToggle.isSelected()));

        systemActiveBtn.setOnAction(e -> {
            boolean active = systemActiveBtn.isSelected();
            systemActiveBtn.setText(active ? "🟢 System Active" : "🔴 System Passive");
            SystemControlManager.setSystemActive(active);
            statusLabel.setText("🔄 System state updated.");
        });
    }
}
