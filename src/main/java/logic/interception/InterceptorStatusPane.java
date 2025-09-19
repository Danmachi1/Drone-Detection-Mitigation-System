package logic.interception;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import logic.engage.InterceptionPlanner.*;

/**
 * 🖥️ InterceptorStatusPane - Displays live status of a single interceptor drone in the UI.
 */
public class InterceptorStatusPane extends VBox {

    private final Label idLabel = new Label();
    private final Label statusLabel = new Label();
    private final Label batteryLabel = new Label();
    private final Label targetLabel = new Label();
    private final Label roleLabel = new Label();

    public InterceptorStatusPane() {
        getChildren().addAll(idLabel, statusLabel, batteryLabel, targetLabel, roleLabel);
        setSpacing(4);
        setStyle("-fx-padding: 6; -fx-border-color: gray; -fx-border-radius: 5; -fx-background-radius: 5;");
    }

    public void update(String id, InterceptorDrone.Status status, double battery,
                       String targetId, InterceptorRole role) {
        idLabel.setText("Drone ID: " + id);
        statusLabel.setText("Status: " + status);
        batteryLabel.setText("Battery: " + String.format("%.1f%%", battery));
        targetLabel.setText("Target: " + (targetId != null ? targetId : "None"));
        roleLabel.setText("Role: " + role);
    }
}
