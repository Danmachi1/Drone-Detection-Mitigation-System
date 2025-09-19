package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import logic.recon.ReconMissionManager;
import logic.recon.ReconMode;

import java.util.Optional;

/**
 * 🛰 ReconSweepPanel - UI to launch and monitor recon sweep missions.
 * Operator can configure zone, radius, and scan mode (bio-inspired behaviors).
 */
public class ReconSweepPanel extends VBox {

    private final TextField zoneField = new TextField();
    private final Spinner<Integer> radiusSpinner = new Spinner<>(50, 2000, 200, 50);
    private final ComboBox<String> modeCombo = new ComboBox<>();
    private final Button launchButton = new Button("🚀 Launch Recon Sweep");
    private final Label statusLabel = new Label("🛰 Recon idle.");

    public ReconSweepPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        modeCombo.getItems().addAll("BeeRoles", "FalconVision", "BatEcho", "SharkScan", "AntSwarm");
        modeCombo.setValue("BeeRoles");

        getChildren().addAll(
            new Label("🛰 Bio-Inspired Recon Sweep"),
            new Label("📍 Zone/Area Name"), zoneField,
            new Label("📡 Sweep Radius (m)"), radiusSpinner,
            new Label("🧬 Recon Mode"), modeCombo,
            launchButton,
            statusLabel
        );

        launchButton.setOnAction(e -> launchRecon());
    }

    private void launchRecon() {
        String zone = zoneField.getText().trim();
        int radius = radiusSpinner.getValue();
        String modeName = modeCombo.getValue();

        if (zone.isEmpty()) {
            statusLabel.setText("❗ Zone required.");
            return;
        }

        ReconMode mode = ReconMode.fromName(modeName);
        boolean launched = ReconMissionManager.launchSweep(zone, radius, mode);

        statusLabel.setText(launched ? "✅ Recon sweep launched." : "❌ Failed to launch sweep.");
    }
}
