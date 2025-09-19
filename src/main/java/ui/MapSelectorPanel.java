package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import utils.map.MapManager;

/**
 * 🗺 MapSelectorPanel - Lets the operator choose between map sources/modes.
 * Modes include: Live (Google/OSM), Cached, Offline Tiles, or Custom Import.
 */
public class MapSelectorPanel extends VBox {

    private final Label currentLabel = new Label("🗺 Map Mode: " + MapManager.getCurrentMode());

    public MapSelectorPanel() {
        setSpacing(10);
        setPadding(new Insets(10));
	
	        getChildren().addAll(new Label("🗺 Map Source Selector"), buildSelector(), currentLabel);
    }

    private ComboBox<String> buildSelector() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(
            "Live-Google",
            "Live-OSM",
            "Cached",
            "Offline",
            "Custom"
        );

        combo.setValue(MapManager.getCurrentMode());
        combo.setOnAction(e -> {
            String selected = combo.getValue();
            MapManager.setMapMode(selected);
            currentLabel.setText("🗺 Map Mode: " + selected);
        });

        return combo;
    }
}
