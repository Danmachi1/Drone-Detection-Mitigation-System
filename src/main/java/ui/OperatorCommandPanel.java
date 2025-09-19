package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import logic.engage.DefenderDroneController;
import logic.engage.DefenderDroneController.DroneCommand;
import logic.engage.DefenderDroneController.DroneStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 🎮 OperatorCommandPanel - Provides manual control options for individual drones.
 * Supports tactical commands: Takeoff, Pause, Resume, Emergency Land.
 */
public class OperatorCommandPanel extends VBox {

    private final ComboBox<String> droneCombo = new ComboBox<>();
    private final Label statusLabel = new Label("🎮 Select a drone and command.");
    private final DefenderDroneController controller;

    public OperatorCommandPanel(DefenderDroneController controller) {
        this.controller = controller;

        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(
            new Label("🎮 Operator Drone Command"),
            droneCombo,
            buildCommandButtons(),
            statusLabel
        );

        loadActiveDrones();
    }

    private void loadActiveDrones() {
        Map<String, DroneCommand> drones = controller.getAllDroneCommands();
        droneCombo.getItems().clear();
        droneCombo.getItems().addAll(
            drones.entrySet().stream()
                .filter(e -> e.getValue().status != DroneStatus.LOST)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
        );
    }

    private VBox buildCommandButtons() {
        Button takeoff = new Button("🛫 Takeoff");
        Button pause = new Button("⏸ Pause");
        Button resume = new Button("▶️ Resume");
        Button land = new Button("🛬 Emergency Land");

        takeoff.setOnAction(e -> send("takeoff"));
        pause.setOnAction(e -> send("pause"));
        resume.setOnAction(e -> send("resume"));
        land.setOnAction(e -> send("land"));

        return new VBox(5, takeoff, pause, resume, land);
    }

    private void send(String action) {
        String selected = droneCombo.getValue();
        if (selected == null) {
            statusLabel.setText("⚠️ No drone selected.");
            return;
        }

        DroneCommand cmd = controller.getAllDroneCommands().get(selected);
        if (cmd == null) {
            statusLabel.setText("❌ Drone not found.");
            return;
        }

        switch (action.toLowerCase()) {
            case "takeoff" -> {
                controller.dispatch(selected);
                statusLabel.setText("🛫 Drone " + selected + " dispatched.");
            }
            case "pause" -> {
                cmd.status = DroneStatus.PAUSED;
                statusLabel.setText("⏸ Drone " + selected + " paused.");
            }
            case "resume" -> {
                cmd.status = DroneStatus.ENGAGING;
                statusLabel.setText("▶️ Drone " + selected + " resumed.");
            }
            case "land" -> {
                controller.returnToBase(selected);
                statusLabel.setText("🛬 Drone " + selected + " returning to base.");
            }
            default -> {
                statusLabel.setText("❓ Unknown action.");
            }
        }
    }
}
