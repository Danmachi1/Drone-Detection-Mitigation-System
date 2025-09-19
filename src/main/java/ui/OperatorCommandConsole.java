package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import logic.engage.DefenderDroneController;
import logic.engage.InterceptionPlanner;

/**
 * 🎮 OperatorCommandConsole - Central command panel for sending
 * mission commands, overrides, and drone tasking.
 */
public class OperatorCommandConsole extends VBox {

    private final DefenderDroneController controller;
    private final InterceptionPlanner planner;
    private final Label status = new Label("🟢 System ready.");

    public OperatorCommandConsole(DefenderDroneController controller, InterceptionPlanner planner) {
        this.controller = controller;
        this.planner = planner;

        setSpacing(10);
        setPadding(new Insets(10));
        getChildren().add(new Label("🎮 Operator Command Console"));

        TextField droneId = new TextField();
        droneId.setPromptText("Enter Drone ID (e.g., D1)");

        ComboBox<InterceptionPlanner.InterceptorRole> roleSelect = new ComboBox<>();
        roleSelect.getItems().addAll(InterceptionPlanner.InterceptorRole.values());
        roleSelect.setValue(InterceptionPlanner.InterceptorRole.KAMIKAZE); // default
        roleSelect.setPromptText("Select Interceptor Role");

        Button dispatch = new Button("🚀 Dispatch Interceptor");
        Button rtb = new Button("↩️ Return to Base");
        Button abort = new Button("⛔ Abort Mission");

        dispatch.setOnAction(e -> {
            String d = droneId.getText().trim();
            InterceptionPlanner.InterceptorRole role = roleSelect.getValue();
            if (!d.isEmpty() && role != null) {
                planner.assignInterception(d, role);
                updateStatus("🚀 Assigned drone " + d + " to intercept with role " + role);
            } else {
                updateStatus("❌ Please enter a valid Drone ID and Role.");
            }
        });

        rtb.setOnAction(e -> {
            String d = droneId.getText().trim();
            if (!d.isEmpty()) {
                controller.returnToBase(d);
                updateStatus("↩️ " + d + " ordered to return.");
            } else {
                updateStatus("❌ Enter drone ID for R2B.");
            }
        });

        abort.setOnAction(e -> {
            String d = droneId.getText().trim();
            if (!d.isEmpty()) {
                controller.abortMission(d);
                updateStatus("⛔ Mission aborted for " + d);
            } else {
                updateStatus("❌ Enter drone ID to abort.");
            }
        });

        getChildren().addAll(
            droneId,
            new Label("Select Interceptor Role:"),
            roleSelect,
            dispatch,
            rtb,
            abort,
            status
        );
    }

    private void updateStatus(String msg) {
        status.setText(msg);
    }
}
