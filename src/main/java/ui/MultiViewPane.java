package ui;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.config.Config;

/**
 * 🖥 MultiViewPane - Displays synchronized multi-mode views:
 * Normal camera, FalconVision, Infrared, and Thermal overlays.
 */
public class MultiViewPane extends GridPane {

    private final StackPane normalView = createView("Normal", Color.LIGHTGRAY);
    private final StackPane falconView = createView("FalconVision", Color.GOLD);
    private final StackPane infraredView = createView("Infrared", Color.ORANGERED);
    private final StackPane thermalView = createView("Thermal", Color.DARKRED);

    public MultiViewPane() {
        setHgap(5);
        setVgap(5);
        setPadding(new Insets(10));
        setPrefSize(800, 600);
        refresh();
    }

    public void refresh() {
        getChildren().clear();

        String mode = Config.getString("vision.mode", "Normal");

        switch (mode) {
            case "MultiView" -> {
                add(normalView, 0, 0);
                add(falconView, 1, 0);
                add(infraredView, 0, 1);
                add(thermalView, 1, 1);
            }
            case "FalconVision" -> getChildren().add(falconView);
            case "Infrared" -> getChildren().add(infraredView);
            case "Thermal" -> getChildren().add(thermalView);
            default -> getChildren().add(normalView);
        }
    }

    private StackPane createView(String label, Color color) {
        Rectangle bg = new Rectangle(380, 280, color);
        bg.setOpacity(0.5);
        Text text = new Text("🔲 " + label + " Feed");
        StackPane pane = new StackPane(bg, text);
        pane.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        return pane;
    }
}
