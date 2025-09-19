package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import main.config.Config;
import logic.zones.Zone;
import utils.ZoneUtils;

import java.util.List;

/**
 * 🚫 NoFlyZoneOverlay - Visually renders no-fly zones defined in config onto the UI map layer.
 */
public class NoFlyZoneOverlay extends Pane {

    private final Canvas canvas;

    public NoFlyZoneOverlay(double width, double height) {
        setPrefSize(width, height);
        canvas = new Canvas(width, height);
        getChildren().add(canvas);
        drawZones();
    }

    public void drawZones() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Zone> zones = Config.getZoneList("no_fly_zones");

        for (Zone zone : zones) {
            List<double[]> polygon = zone.getPolygon();
            if (polygon == null || polygon.size() < 3) continue;

            double[] xPoints = new double[polygon.size()];
            double[] yPoints = new double[polygon.size()];

            for (int i = 0; i < polygon.size(); i++) {
                double[] latlon = polygon.get(i);
                double[] xy = ZoneUtils.mapLatLonToXY(latlon[0], latlon[1], getWidth(), getHeight());
                xPoints[i] = xy[0];
                yPoints[i] = xy[1];
            }

            gc.setFill(Color.rgb(255, 0, 0, 0.3));
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.fillPolygon(xPoints, yPoints, polygon.size());
            gc.strokePolygon(xPoints, yPoints, polygon.size());
        }
    }

    public void refresh() {
        drawZones();
    }
}
