package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import main.config.Config;
import utils.ZoneUtils;

/**
 * 🏔 TerrainElevationOverlay - Paints elevation grid as grayscale overlay on map.
 * Darker = lower terrain, brighter = higher elevation.
 */
public class TerrainElevationOverlay extends Pane {

    private final Canvas canvas;

    public TerrainElevationOverlay(double width, double height) {
        setPrefSize(width, height);
        canvas = new Canvas(width, height);
        getChildren().add(canvas);
        drawElevation();
    }

    private void drawElevation() {
        if (Config.ELEVATION_DATA == null) return;

        double[][] elevation = Config.ELEVATION_DATA;
        int rows = elevation.length;
        int cols = elevation[0].length;

        WritableImage image = new WritableImage(cols, rows);
        PixelWriter writer = image.getPixelWriter();

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (double[] row : elevation) {
            for (double h : row) {
                if (h < min) min = h;
                if (h > max) max = h;
            }
        }

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                double val = (elevation[y][x] - min) / (max - min + 1e-5); // Normalize
                int gray = (int) (val * 255);
                writer.setColor(x, y, Color.grayRgb(gray));
            }
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, getWidth(), getHeight());
    }

    public void refresh() {
        drawElevation();
    }
}
