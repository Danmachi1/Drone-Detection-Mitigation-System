package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import utils.alerts.AlertManager;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 🚨 AlertPanel – Live system-alert viewer.
 * Subscribes to {@link AlertManager} and auto-scrolls as new messages arrive.
 */
public class AlertPanel extends VBox {

    private static final int MAX_ALERTS = 120;            // keep last N lines

    private final TextArea alertLog   = new TextArea();
    private final Queue<String> alertQueue = new LinkedList<>();

    public AlertPanel() {
        setPadding(new Insets(10));
        setSpacing(6);

        Label title = new Label("🚨 System Alerts");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        alertLog.setEditable(false);
        alertLog.setWrapText(true);
        alertLog.setPrefRowCount(18);

        ScrollPane scroller = new ScrollPane(alertLog);
        scroller.setFitToWidth(true);
        scroller.setFitToHeight(true);

        getChildren().addAll(title, scroller);

        subscribeToAlerts();
    }

    /* ------------------------------------------------------------------ */

    private void subscribeToAlerts() {
        AlertManager.subscribe((severity, message) -> Platform.runLater(() -> {
            String ts = LocalDateTime.now().toString();
            String entry = "[" + ts + "][" + severity + "] " + message;

            alertQueue.add(entry);
            if (alertQueue.size() > MAX_ALERTS) alertQueue.poll();

            alertLog.setText(String.join("\n", alertQueue));
            alertLog.setScrollTop(Double.MAX_VALUE);        // auto-scroll
        }));
    }
}
