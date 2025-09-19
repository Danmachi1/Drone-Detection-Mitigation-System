package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import logic.engage.EngagementManager;
import logic.threat.Threat;
import logic.threat.ThreatDatabase;

import java.util.List;

/**
 * 🎯 KillChainOverridePanel - Manual override of automated kill chain decisions.
 * Lets operator force actions on live threats: track, neutralize, or ignore.
 */
public class KillChainOverridePanel extends VBox {

    private final ComboBox<String> threatCombo = new ComboBox<>();
    private final Label statusLabel = new Label("🔍 Select a threat to override.");
    private List<Threat> threats;

    public KillChainOverridePanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(new Label("🎯 Kill Chain Override"), threatCombo, buildButtons(), statusLabel);
        loadThreats();
    }

    private void loadThreats() {
        threats = ThreatDatabase.getAllThreats();
        threatCombo.getItems().clear();
        for (Threat t : threats) {
            threatCombo.getItems().add(t.getId() + " - " + t.getType());
        }
    }

    private HBox buildButtons() {
        Button track = new Button("👁 Track");
        Button neutralize = new Button("💥 Neutralize");
        Button ignore = new Button("🚫 Ignore");

        track.setOnAction(e -> applyOverride("track"));
        neutralize.setOnAction(e -> applyOverride("neutralize"));
        ignore.setOnAction(e -> applyOverride("ignore"));

        return new HBox(10, track, neutralize, ignore);
    }

    private void applyOverride(String action) {
        int index = threatCombo.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            statusLabel.setText("⚠️ No threat selected.");
            return;
        }

        Threat selected = threats.get(index);
        boolean result = EngagementManager.manualOverride(selected.getId(), action);

        if (result) {
            statusLabel.setText("✅ " + action.toUpperCase() + " command sent to " + selected.getId());
        } else {
            statusLabel.setText("❌ Failed to override threat.");
        }
    }
}
