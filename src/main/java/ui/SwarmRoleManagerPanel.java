package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import logic.engage.DefenderDroneController;

import java.util.Map;
import java.util.Set;

/**
 * 🧠 SwarmRoleManagerPanel - Allows operator to assign or change roles of drones.
 * Supports bio-inspired and tactical dynamic role assignment.
 */
public class SwarmRoleManagerPanel extends VBox {

    private final ComboBox<String> droneCombo = new ComboBox<>();
    private final ComboBox<String> roleCombo = new ComboBox<>();
    private final Label statusLabel = new Label("🎮 Select drone and new role.");

    private static final Set<String> ROLES = Set.of(
        "Recon", "Interceptor", "Jammer", "Relay", "Decoy", "Sniper", "Escort", "Blocker", "Kamikaze"
    );

    public SwarmRoleManagerPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(
            new Label("🧠 Swarm Role Assignment"),
            new Label("🛰 Drone:"), droneCombo,
            new Label("🎭 Role:"), roleCombo,
            buildButtons(),
            statusLabel
        );

        loadDrones();
        loadRoles();
    }

    private void loadDrones() {
        droneCombo.getItems().clear();
        for (Map.Entry<String, String> entry : DefenderDroneController.getInstance().getDroneRoles().entrySet()) {
            String droneId = entry.getKey();
            String role = entry.getValue();
            droneCombo.getItems().add(droneId + " (" + role + ")");
        }
    }

    private void loadRoles() {
        roleCombo.getItems().clear();
        roleCombo.getItems().addAll(ROLES);
    }

    private Button buildButtons() {
        Button assignBtn = new Button("🎯 Assign Role");
        assignBtn.setOnAction(e -> assignRole());
        return assignBtn;
    }

    private void assignRole() {
        int droneIndex = droneCombo.getSelectionModel().getSelectedIndex();
        String selectedRole = roleCombo.getSelectionModel().getSelectedItem();

        if (droneIndex < 0 || selectedRole == null) {
            statusLabel.setText("⚠️ Please select both drone and role.");
            return;
        }

        String droneId = droneCombo.getItems().get(droneIndex).split(" ")[0];
        boolean result = DefenderDroneController.getInstance().assignRole(droneId, selectedRole);

        if (result) {
            statusLabel.setText("✅ Role '" + selectedRole + "' assigned to " + droneId);
        } else {
            statusLabel.setText("❌ Role assignment failed.");
        }
    }
}
