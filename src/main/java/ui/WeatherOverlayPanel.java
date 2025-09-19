package ui;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.config.Config;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 🌦️ WeatherOverlayPanel - Paints weather effects like rain, fog, or wind
 * as semi-transparent overlays onto the UI map.
 */
public class WeatherOverlayPanel extends StackPane {

    private final Canvas canvas;

    public WeatherOverlayPanel(double width, double height) {
        setPrefSize(width, height);
        canvas = new Canvas(width, height);
        getChildren().add(canvas);
        drawWeather();
        
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> drawWeather());
            }
        }, 0, 5000);
    }

    private void drawWeather() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (Config.WEATHER_DATA == null) return;

        String condition = Config.WEATHER_DATA.get("condition");
        double severity = Double.parseDouble(Config.WEATHER_DATA.getOrDefault("severity", "0.5"));

        if (condition.contains("rain")) {
            gc.setFill(Color.rgb(0, 0, 255, severity * 0.3));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        } else if (condition.contains("fog")) {
            gc.setFill(Color.rgb(200, 200, 200, severity * 0.4));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        } else if (condition.contains("wind")) {
            gc.setStroke(Color.rgb(180, 180, 255, severity * 0.6));
            gc.setLineWidth(1.5);
            for (int i = 0; i < 30 * severity; i++) {
                double y = Math.random() * canvas.getHeight();
                gc.strokeLine(0, y, canvas.getWidth(), y + 10);
            }
        }
    }
}
