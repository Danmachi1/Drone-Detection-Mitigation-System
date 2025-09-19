package ui;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.config.Config;
import main.config.UIModeConstants;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 🦅 FalconVisionOverlay - Simulates advanced vision enhancement overlays (edge highlight, pattern burst).
 */
public class FalconVisionOverlay extends StackPane {

    private final Canvas canvas;
    private final Random random = new Random();

    public FalconVisionOverlay(double width, double height) {
        setPrefSize(width, height);
        canvas = new Canvas(width, height);
        getChildren().add(canvas);

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> drawOverlay());
            }
        }, 0, 4000);
    }

    private void drawOverlay() {
        if (!"FalconVision".equalsIgnoreCase(UIModeConstants.VISION_MODE)) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.rgb(255, 255, 0, 0.6));
        gc.setLineWidth(1.5);

        // Simulate vision highlights – future: plug in visual sensor input
        for (int i = 0; i < 20; i++) {
            double x = random.nextDouble() * canvas.getWidth();
            double y = random.nextDouble() * canvas.getHeight();
            gc.strokeOval(x, y, 8, 8);
        }

        gc.setFill(Color.rgb(255, 255, 255, 0.05));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // ghost effect
    }
}