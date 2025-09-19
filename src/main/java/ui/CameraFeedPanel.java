package ui;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import utils.CameraFeedManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 🎥 CameraFeedPanel – Displays the live (or simulated) camera feed.
 * ---------------------------------------------------------------
 * • If {@link CameraFeedManager#getFrame()} returns non-null, that frame is
 *   drawn.  Otherwise we render a simple flashing placeholder so the operator
 *   sees the panel is alive.
 * • Updates at ~10 FPS via a background Timer.
 */
public class CameraFeedPanel extends StackPane {

    private final Canvas canvas = new Canvas(640, 360);
    private final Timer  timer  = new Timer("cam-feed", true);
    private boolean flash = false;            // placeholder toggler

    public CameraFeedPanel() {
        getChildren().add(canvas);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() { Platform.runLater(CameraFeedPanel.this::drawFrame); }
        }, 0, 100);                           // 10 FPS
    }

    /* ─────────────────────────────────────────────────────────── */

    private void drawFrame() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image frame = CameraFeedManager.getFrame();  // may be null in sim

        if (frame != null) {
            gc.drawImage(frame, 0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            flash = !flash;
            gc.setFill(flash ? Color.DARKGRAY : Color.GRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.fillText("No camera feed", 20, 25);
        }
    }

    /** Stop timer – call when pane is removed or app exits. */
    public void shutdown() { timer.cancel(); }
}
