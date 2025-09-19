package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import main.MainApp;
import main.config.Config;

/**
 * ⚙️ ConfigControlPanel – Displays editable config fields bound to the Config class.
 * Supports toggling simulation mode, selecting fusion plugins, and applying at runtime.
 */
public class ConfigControlPanel extends VBox {

    private final CheckBox simModeCheck = new CheckBox("Simulation Mode");
    private final ComboBox<String> fusionPluginBox = new ComboBox<>();
    private final Label status = new Label("📝 Modify and Save Config");

    public ConfigControlPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        // Load existing config values
        boolean simEnabled = "true".equalsIgnoreCase(Config.getString("USE_SIMULATION"));
        String fusionType = Config.getString("FUSION_PLUGIN", "Hybrid");

        // Setup controls
        simModeCheck.setSelected(simEnabled);

        fusionPluginBox.getItems().addAll("Kalman", "UKF", "Hybrid", "RuleBased", "Fallback");
        fusionPluginBox.setValue(fusionType);

        Button save = new Button("💾 Save");
        save.setOnAction(e -> {
            // Save to config
            boolean newSim = simModeCheck.isSelected();
            String newFusion = fusionPluginBox.getValue();

            Config.setString("USE_SIMULATION", String.valueOf(newSim));
            Config.setString("FUSION_PLUGIN", newFusion);

            // Live reload
            MainApp.get().getSensors().reloadSensors(newSim);            // 🔁 sensors
            MainApp.get().getCoordinator().reloadFromConfig();
                // 🔁 fusion

            status.setText("✅ Settings applied live. Simulation: " + newSim +
                    ", Fusion: " + newFusion);
        });

        getChildren().addAll(
                new Label("🧩 System Configuration"),
                simModeCheck,
                new Label("🔀 Fusion Plugin Type:"),
                fusionPluginBox,
                save,
                status
        );
    }
}
