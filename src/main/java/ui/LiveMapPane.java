package ui;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import sensors.MultiSensorManager;
import sensors.SensorDataRecord;
import sensors.SensorPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 🗺️ LiveMapPane – real-time drone & threat visualisation.
 */
public class LiveMapPane extends StackPane {
    final double RADAR_RANGE_METERS = 200;
    final double RADAR_RADIUS_PIXELS = 400;
    final double SCALE = RADAR_RADIUS_PIXELS / RADAR_RANGE_METERS;

    private final Canvas canvas = new Canvas(1400, 850);
    private final MultiSensorManager sensorManager;
    private final Label legend = new Label("🟥 Threat │ 🟦 Drone │ 🟨 Prediction");

    public LiveMapPane(MultiSensorManager sensorManager) {
        this.sensorManager = sensorManager;
        getChildren().addAll(canvas, legend);
        legend.setTranslateY(-400);
        legend.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-padding: 6px;");
        startDrawing();
    }

    /** 30 Hz repaint loop. */
    private void startDrawing() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(gc);
            }
        }.start();
    }

    private void draw(GraphicsContext gc) {
        sensorManager.pollAll();

        // Clear canvas
        gc.setGlobalAlpha(1.0);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        // Radar grid circles
        gc.setStroke(Color.DARKGREEN);
        gc.setLineWidth(1);
        for (int r = 100; r <= 400; r += 100) {
            gc.strokeOval(centerX - r, centerY - r, r * 2, r * 2);
        }

        // Crosshair
        gc.strokeLine(centerX - 400, centerY, centerX + 400, centerY);
        gc.strokeLine(centerX, centerY - 400, centerX, centerY + 400);

        Collection<SensorPlugin> plugins = sensorManager.getActivePlugins();

        int labelOffset = 0;
        for (SensorPlugin plugin : plugins) {
            double pluginRange = plugin.getRangeMeters();
            double pluginSweep = plugin.getSweepAngle();
            double pluginScale = RADAR_RADIUS_PIXELS / pluginRange;

            // Draw plugin-specific sweep arc
            gc.setFill(Color.rgb(0, 255, 0, 0.1));
            gc.fillArc(centerX - RADAR_RADIUS_PIXELS, centerY - RADAR_RADIUS_PIXELS,
                    RADAR_RADIUS_PIXELS * 2, RADAR_RADIUS_PIXELS * 2,
                    -pluginSweep, -30, ArcType.ROUND);

            // Render data points
            renderPoints(gc, plugin.getThreatPositions(), Color.RED, centerX, centerY, pluginSweep, pluginScale);
            renderPoints(gc, plugin.getDronePositions().values(), Color.BLUE, centerX, centerY, pluginSweep, pluginScale);
            renderPoints(gc, plugin.getPredictedPaths(), Color.GOLD, centerX, centerY, pluginSweep, pluginScale);

            // Draw plugin name + threat count
            gc.setFill(Color.WHITE);
            gc.fillText(plugin.getPluginName() + ": " + plugin.getThreatPositions().size() + " threats",
                    10, 20 + (labelOffset++) * 16);
        }
    }

    /**
     * Renders dots and optional velocity lines.
     */
   // 🔧 Helper to render dots within sweep beam

 // 🔧 Helper to render dots within sweep beam (with fade for prediction)
    private void renderPoints(GraphicsContext gc, Collection<double[]> points,
                              Color color, double cx, double cy,
                              double sweepAngle, double scale) {
        int count = 0;
        for (double[] pos : points) {
            if (pos == null || pos.length < 2) continue;

            double dx = pos[0];
            double dy = -pos[1]; // flip Y for screen

            double angleToDot = Math.toDegrees(Math.atan2(dy, dx));
            if (angleToDot < 0) angleToDot += 360;

            double diff = Math.abs(angleToDot - sweepAngle);
            if (diff > 180) diff = 360 - diff;

            if (diff <= 15) { // 30° total beam
                double px = cx + dx * scale;
                double py = cy + dy * scale;

                // 🟡 Prediction trails have fading alpha
                if (Color.GOLD.equals(color)) {
                    gc.setGlobalAlpha(0.3 + 0.1 * (count % 3)); // stagger alpha
                } else {
                    gc.setGlobalAlpha(1.0);
                }

                gc.setFill(color);
                gc.fillOval(px - 4, py - 4, 8, 8);
                count++;

                // Optional: cap number of visible prediction points
                if (Color.GOLD.equals(color) && count >= 15) break;
            }
        }

        gc.setGlobalAlpha(1.0); // reset alpha
    }
}