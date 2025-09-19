package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import logic.engage.DefenderDroneController;
import logic.engage.EngagementManager;

/**
 * ⚠️ OperatorCommandPane - Central override controls for mission logic,
 * AI mode switching, and emergency actions.
 */
public class OperatorCommandPane extends VBox {

    private final EngagementManager engagementManager;
    private final DefenderDroneController droneController;

    public OperatorCommandPane(EngagementManager engagementManager) {
        this.engagementManager = engagementManager;
        this.droneController = (engagementManager != null)
                ? engagementManager.getDroneController()
                : new DefenderDroneController();

        setSpacing(12);
        setPadding(new Insets(10));
        getChildren().add(new Label("🛑 Operator Override Controls"));

        // AI Mode Toggle
        Label aiLabel = new Label("AI Mode:");
        ComboBox<String> aiMode = new ComboBox<>();
        aiMode.getItems().addAll("RULE", "AI", "HYBRID");
        aiMode.setValue("HYBRID");
        aiMode.setOnAction(e -> System.out.println("🔁 Logic mode changed to: " + aiMode.getValue()));

        // Emergency Stop
        Button abortAllBtn = new Button("🚨 Abort All Missions");
        abortAllBtn.setOnAction(e -> {
            engagementManager.abortAll();
            System.out.println("🛑 All missions aborted.");
        });

        // Reset All
        Button resetAllBtn = new Button("♻️ Reset All Drones");
        resetAllBtn.setOnAction(e -> {
            for (String id : droneController.getAllDroneCommands().keySet()) {
                droneController.resetDrone(id);
            }
            System.out.println("🔄 All drones reset.");
        });

        // Sensor Mode Toggle
        Button toggleSensorModeBtn = new Button("🧭 Toggle Sensor Mode");
        toggleSensorModeBtn.setOnAction(e -> {
            // placeholder for switching between real/sim
            System.out.println("🧪 Sensor mode toggled.");
        });

        getChildren().addAll(aiLabel, aiMode, abortAllBtn, resetAllBtn, toggleSensorModeBtn);
    }
}
