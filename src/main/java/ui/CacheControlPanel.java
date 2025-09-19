package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import utils.map.MapManager;

/**
 * 🧭 CacheControlPanel - UI panel to define areas to cache for offline missions.
 * Supports bounding box input and calls `MapManager.cacheArea(...)`.
 */
public class CacheControlPanel extends VBox {

    private final TextField lat1Field = new TextField();
    private final TextField lon1Field = new TextField();
    private final TextField lat2Field = new TextField();
    private final TextField lon2Field = new TextField();
    private final Label statusLabel = new Label("🧭 Enter bounding box and cache.");

    public CacheControlPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(
            new Label("🧭 Offline Map Cache Control"),
            buildForm(),
            buildButton(),
            statusLabel
        );
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        grid.add(new Label("Lat1:"), 0, 0);
        grid.add(lat1Field, 1, 0);
        grid.add(new Label("Lon1:"), 0, 1);
        grid.add(lon1Field, 1, 1);
        grid.add(new Label("Lat2:"), 0, 2);
        grid.add(lat2Field, 1, 2);
        grid.add(new Label("Lon2:"), 0, 3);
        grid.add(lon2Field, 1, 3);

        return grid;
    }

    private Button buildButton() {
        Button cacheBtn = new Button("🗺 Cache Selected Area");

        cacheBtn.setOnAction(e -> {
            try {
                double lat1 = Double.parseDouble(lat1Field.getText());
                double lon1 = Double.parseDouble(lon1Field.getText());
                double lat2 = Double.parseDouble(lat2Field.getText());
                double lon2 = Double.parseDouble(lon2Field.getText());

                boolean result = MapManager.cacheArea(lat1, lon1, lat2, lon2);
                statusLabel.setText(result ? "✅ Area cached." : "❌ Caching failed.");

            } catch (Exception ex) {
                statusLabel.setText("❌ Invalid input.");
            }
        });

        return cacheBtn;
    }
}
