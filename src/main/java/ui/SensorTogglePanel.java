package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sensors.MultiSensorManager;
import sensors.SensorPlugin;

import java.util.List;

/**
 * 🛰 SensorTogglePanel - Lets operator toggle individual sensor plugins (radar, RF, thermal, etc).
 * Supports mission-specific tuning and fallback sensor logic.
 */
public class SensorTogglePanel extends VBox {

    private final MultiSensorManager sensorManager;
    private final VBox sensorListBox = new VBox(5);
    private final Label statusLabel = new Label("🔧 Sensor control panel ready.");

    public SensorTogglePanel(MultiSensorManager manager) {
        this.sensorManager = manager;

        setSpacing(10);
        setPadding(new Insets(10));
        getChildren().addAll(new Label("🛰 Sensor Plugin Control"), sensorListBox, statusLabel);

        loadSensorToggles();
    }

    private void loadSensorToggles() {
        sensorListBox.getChildren().clear();
        List<SensorPlugin> plugins = sensorManager.getAllPlugins();

        for (String key : sensorManager.getPluginKeys()) {
            SensorPlugin plugin = sensorManager.getPluginByKey(key);
            String label = key;  // e.g., "Radar-Sim0", "Radar-Real"

            CheckBox box = new CheckBox(label);
            box.setSelected(plugin.isActive());

            box.setOnAction(e -> {
                boolean enabled = box.isSelected();
                if (enabled) {
                    sensorManager.enablePlugin(label);
                    statusLabel.setText("✅ " + label + " enabled.");
                } else {
                    sensorManager.disablePlugin(label);
                    statusLabel.setText("⚠️ " + label + " disabled.");
                }
            });

            sensorListBox.getChildren().add(box);
        }


        }
    }

