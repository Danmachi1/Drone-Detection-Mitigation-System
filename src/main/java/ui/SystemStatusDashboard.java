package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;   // or VBox / StackPane
import javafx.scene.canvas.Canvas;
import logic.status.SystemHealthMonitor;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 📊 SystemStatusDashboard - Displays live health metrics of all major modules.
 * Shows CPU/memory usage, sensor activity, and AI/logic engine states.
 */
public class SystemStatusDashboard extends VBox {

    private final Label cpuLabel = new Label("CPU: --");
    private final Label memLabel = new Label("Memory: --");
    private final VBox moduleList = new VBox(5);

    public SystemStatusDashboard() {
        setSpacing(10);
        setPadding(new Insets(10));

        getChildren().addAll(
            new Label("📊 System Status Dashboard"),
            cpuLabel, memLabel,
            new Separator(),
            new Label("📦 Subsystem Modules"),
            moduleList
        );

        startMonitoring();
    }

    private void startMonitoring() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Platform.runLater(() -> updateStatus());
            }
        }, 0, 3000);
    }

    private void updateStatus() {
        cpuLabel.setText("🧠 CPU Usage: " + SystemHealthMonitor.getCpuUsage() + "%");
        memLabel.setText("💾 Memory: " + SystemHealthMonitor.getMemoryUsage() + "%");

        moduleList.getChildren().clear();
        Map<String, Boolean> moduleStates = SystemHealthMonitor.getModuleHealthStates();

        for (Map.Entry<String, Boolean> entry : moduleStates.entrySet()) {
            String name = entry.getKey();
            boolean up = entry.getValue();
            Label status = new Label((up ? "🟢" : "🔴") + " " + name);
            moduleList.getChildren().add(status);
        }
    }
}
