package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import logic.engage.DefenderDroneController;

import java.util.Map;

/**
 * 🏠 ReturnToBaseControl - Allows operator to manually trigger drone returns.
 * Also supports future auto-return on low battery or mission end.
 */
public class ReturnToBaseControl extends VBox {

    private final DefenderDroneController controller = DefenderDroneController.getInstance();

    private final ComboBox<String> droneCombo = new ComboBox<>();
    private final Label statusLabel = new Label("⚙️ Select a drone to return.");

    public ReturnToBaseControl() {
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(new Label("🏠 Return to Base Panel"), droneCombo, buildButtons(), statusLabel);
        loadDrones();
    }

    private void loadDrones() {
        Map<String, Double> levels = controller.getDroneBatteryLevels(); // ✅ instance call
        droneCombo.getItems().clear();

        for (Map.Entry<String, Double> entry : levels.entrySet()) {
            String id = entry.getKey();
            double level = entry.getValue();
            droneCombo.getItems().add(id + " - " + (int)(level * 100) + "%");
        }
    }

    private HBox buildButtons() {
        Button manualReturn = new Button("⏎ Return Selected");
        Button autoReturn = new Button("⚡ Return All < 25%");

        manualReturn.setOnAction(e -> {
            int index = droneCombo.getSelectionModel().getSelectedIndex();
            if (index < 0) {
                statusLabel.setText("❗ Select a drone first.");
                return;
            }
            String droneId = droneCombo.getItems().get(index).split(" - ")[0];
            boolean success = controller.sendReturnCommand(droneId); // ✅ instance call

            statusLabel.setText(success ? "✅ " + droneId + " returning." : "❌ Command failed.");
        });

        autoReturn.setOnAction(e -> {
            int count = 0;
            for (Map.Entry<String, Double> entry : controller.getDroneBatteryLevels().entrySet()) { // ✅ instance call
                if (entry.getValue() < 0.25) {
                    boolean sent = controller.sendReturnCommand(entry.getKey()); // ✅ instance call
                    if (sent) count++;
                }
            }
            statusLabel.setText("🔋 Auto-return triggered for " + count + " drone(s).");
        });

        return new HBox(10, manualReturn, autoReturn);
    }
}
