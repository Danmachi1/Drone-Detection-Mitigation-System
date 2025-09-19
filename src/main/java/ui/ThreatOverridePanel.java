package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import logic.engage.EngagementManager;

import java.util.Map;

/**
 * ☢️ ThreatOverridePanel - Lets operator override the automated kill chain.
 * Supports Escalate, Abort, and Deprioritize per threat.
 */
public class ThreatOverridePanel extends VBox {

    private final ComboBox<String> threatSelector = new ComboBox<>();
    private final Label statusLabel = new Label("☢️ Threat override ready.");
    private final EngagementManager engagementManager;

    public ThreatOverridePanel(EngagementManager engagementManager) {
        this.engagementManager = engagementManager;  // ✅ Store instance
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(
            new Label("☢️ Manual Threat Override"),
            threatSelector,
            buildOverrideButtons(),
            statusLabel
        );

        loadThreats();
    }

    private void loadThreats() {
        threatSelector.getItems().clear();
        Map<String, String> threats = engagementManager.getActiveThreatStatuses();  // ✅ Use instance

        for (Map.Entry<String, String> entry : threats.entrySet()) {
            String id = entry.getKey();
            String status = entry.getValue();
            threatSelector.getItems().add(id + " [" + status + "]");
        }
    }

    private VBox buildOverrideButtons() {
        Button escalateBtn = new Button("🚀 Escalate");
        Button abortBtn = new Button("🛑 Abort");
        Button deprioritizeBtn = new Button("⬇️ Deprioritize");

        escalateBtn.setOnAction(e -> sendOverride("escalate"));
        abortBtn.setOnAction(e -> sendOverride("abort"));
        deprioritizeBtn.setOnAction(e -> sendOverride("deprioritize"));

        return new VBox(5, escalateBtn, abortBtn, deprioritizeBtn);
    }

    private void sendOverride(String action) {
        String selected = threatSelector.getValue();
        if (selected == null || !selected.contains("[")) {
            statusLabel.setText("❗ Select a valid threat.");
            return;
        }

        String threatId = selected.split(" ")[0];
        boolean result = EngagementManager.manualOverride(threatId, action);  // ✅ static is okay here

        if (result) {
            statusLabel.setText("✅ " + action + " applied to " + threatId);
        } else {
            statusLabel.setText("❌ Override failed.");
        }
    }
}
