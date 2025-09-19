package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import main.config.Config;
import utils.MapCacheManager;

/**
 * 🗺️ MapCacheControlPanel - Allows operator to manually cache or load
 * map tiles for offline use.
 */
public class MapCacheControlPanel extends VBox {

    private final TextField areaField = new TextField();

    public MapCacheControlPanel() {
        setSpacing(10);
        setPadding(new Insets(10));
        getChildren().add(new Label("🗺️ Map Caching Tools"));

        areaField.setPromptText("Enter Area Code (e.g., OTTAWA-01)");
        Button saveBtn = new Button("💾 Save Cache");
        Button loadBtn = new Button("📂 Load Cache");

        saveBtn.setOnAction(e -> {
            String area = areaField.getText().trim();
            if (!area.isEmpty()) {
                MapCacheManager.saveMapTiles(area);
                System.out.println("✅ Map tiles cached for: " + area);
            }
        });

        loadBtn.setOnAction(e -> {
            String area = areaField.getText().trim();
            if (!area.isEmpty()) {
                MapCacheManager.loadMapTiles(area);
                System.out.println("📂 Map tiles loaded for: " + area);
            }
        });

        getChildren().addAll(areaField, saveBtn, loadBtn);
    }
}
