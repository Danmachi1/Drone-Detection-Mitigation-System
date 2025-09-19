package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import logic.swarm.DroneAgent;
import logic.swarm.SwarmManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 🔋 BatteryStatusPanel – Grid of progress-bars (one per drone) showing current
 * state-of-charge.  Auto-refreshes every 2 s.
 */
public class BatteryStatusPanel extends GridPane {

    private final SwarmManager swarm;
    private final Map<String, ProgressBar> bars = new HashMap<>();

    public BatteryStatusPanel(SwarmManager swarm) {
        this.swarm = swarm;

        setPadding(new Insets(10));
        setHgap(8); setVgap(4);
        add(new Label("Drone"), 0, 0);
        add(new Label("Charge"), 1, 0);

        refresh();          // initial fill

        /* schedule periodic update */
        javafx.animation.Timeline tl = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(2), e -> refresh()));
        tl.setCycleCount(javafx.animation.Animation.INDEFINITE);
        tl.play();
    }

    /* ---------------------------------------------------------------- */

    private void refresh() {
        int row = 1;
        for (DroneAgent d : swarm.getSwarm()) {
            final int rowFinal = row;  // ✅ capture row value for lambda use

            ProgressBar bar = bars.computeIfAbsent(d.getId(), id -> {
                ProgressBar b = new ProgressBar(1);
                b.setPrefWidth(160);
                add(new Label(id), 0, rowFinal);
                add(b, 1, rowFinal);
                return b;
            });

            bar.setProgress(d.getBatteryLevel() / 100.0);
            row++;
        }
    }

}
