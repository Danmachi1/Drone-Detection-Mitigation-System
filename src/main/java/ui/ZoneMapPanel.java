package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import logic.mapping.MapTileManager;
import logic.zones.ZoneManager;

/**
 * 🗺 ZoneMapPanel - Displays operational map and supports zone definition and caching.
 * Can run in full offline mode using cached tiles and saved zones.
 */
public class ZoneMapPanel extends VBox {

    private final ComboBox<String> cachedZones = new ComboBox<>();
    private final Button defineZoneBtn = new Button("📍 Define New Zone");
    private final Button loadZoneBtn = new Button("📂 Load Cached Zone");
    private final Label statusLabel = new Label("🗺 Map ready.");
    private final Canvas canvas = new Canvas(800, 600); // 💡 Canvas for drawing zones

    public ZoneMapPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(
            new Label("🗺 Operational Map & Zones"),
            cachedZones,
            new VBox(5, defineZoneBtn, loadZoneBtn),
            statusLabel,
            canvas  // 👁 Show canvas
        );

        MapTileManager.setCanvas(canvas); // 🔌 Link canvas to tile manager
        refreshCachedZones();
        wireActions();
    }

    private void refreshCachedZones() {
        cachedZones.getItems().clear();
        cachedZones.getItems().addAll(ZoneManager.getAvailableZoneNames());
    }

    private void wireActions() {
        defineZoneBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("📍 Define Zone");
            dialog.setContentText("Enter zone name:");
            dialog.showAndWait().ifPresent(name -> {
                boolean result = ZoneManager.defineNewZone(name);
                statusLabel.setText(result ? "✅ Zone defined." : "❌ Failed to define zone.");
                refreshCachedZones();
            });
        });

        loadZoneBtn.setOnAction(e -> {
            String selected = cachedZones.getValue();
            if (selected == null) {
                statusLabel.setText("❗ Select a zone to load.");
                return;
            }

            boolean loaded = ZoneManager.loadZone(selected);
            if (loaded) {
                clearCanvas(); // 🎯 Clean before drawing
                MapTileManager.renderZone(selected);
                statusLabel.setText("✅ Zone loaded and rendered.");
            } else {
                statusLabel.setText("❌ Failed to load zone.");
            }
        });
    }

    private void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
