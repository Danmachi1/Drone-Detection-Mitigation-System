package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import utils.map.MapCacheManager;

/**
 * 🗄️ CacheControllerPanel – Tiny UI to inspect and clear the map-tile cache
 * managed by {@link MapCacheManager}.  Refresh button lets you verify disk
 * usage after a purge.
 */
public class CacheControllerPanel extends VBox {

    private final Label  sizeLabel = new Label();
    private final Button clearBtn  = new Button("Clear Cache");
    private final Button refreshBtn = new Button("Refresh");

    public CacheControllerPanel() {
        setPadding(new Insets(10));
        setSpacing(8);

        Label title = new Label("🗄️ Map-Cache Controller");
        title.setStyle("-fx-font-weight:bold; -fx-font-size:14px;");

        clearBtn.setOnAction(e -> {
            MapCacheManager.clear();
            updateSize();
        });

        refreshBtn.setOnAction(e -> updateSize());

        getChildren().addAll(title, sizeLabel, clearBtn, refreshBtn);
        updateSize();
    }

    /* ---------------------------------------------------------------- */

    private void updateSize() {
        long bytes = MapCacheManager.sizeBytes();
        sizeLabel.setText("Current cache size: " + human(bytes));
    }

    private static String human(long bytes) {
        if (bytes < 1_024)             return bytes + " B";
        if (bytes < 1_024*1_024)       return (bytes/1_024) + " KB";
        if (bytes < 1_024*1_024*1024)  return (bytes/1_024/1_024) + " MB";
        return (bytes/1_024/1_024/1_024) + " GB";
    }
}
