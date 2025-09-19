package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import logic.swarm.DroneAgent;
import logic.swarm.SwarmManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 🔋 BatteryMonitorPane – Small line-chart that shows battery %
 * for every drone over the last ~90 seconds (rolling window).
 */
public class BatteryMonitorPane extends BorderPane {

    private static final int WINDOW = 90;     // seconds of history

    private final SwarmManager swarm;
    private final NumberAxis   xAxis = new NumberAxis();
    private final NumberAxis   yAxis = new NumberAxis(0,100,10);
    private final LineChart<Number,Number> chart =
            new LineChart<>(xAxis, yAxis);

    /** One data series per drone-ID */
    private final Map<String, XYChart.Series<Number,Number>> seriesMap
            = new HashMap<>();

    public BatteryMonitorPane(SwarmManager swarm) {
        this.swarm = swarm;

        setPadding(new Insets(10));
        Label title = new Label("🔋 Battery Monitor");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        xAxis.setLabel("t (s)");
        yAxis.setLabel("%");

        setTop(title);
        setCenter(chart);

        /* schedule UI update every second */
        javafx.animation.Timeline tl = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1), e -> refresh()));
        tl.setCycleCount(javafx.animation.Animation.INDEFINITE);
        tl.play();
    }

    /* ---------------------------------------------------------------- */

    private void refresh() {
        double t = (System.currentTimeMillis() / 1000.0) % 10_000; // wrap

        for (DroneAgent d : swarm.getSwarm()) {
            seriesMap.computeIfAbsent(d.getId(), id -> {
                XYChart.Series<Number,Number> s = new XYChart.Series<>();
                chart.getData().add(s);
                return s;
            });
            XYChart.Series<Number,Number> s = seriesMap.get(d.getId());
            s.getData().add(new XYChart.Data<>(t, d.getBatteryLevel()));

            /* keep window size */
            while (s.getData().size() > WINDOW) s.getData().remove(0);
        }
    }
}
