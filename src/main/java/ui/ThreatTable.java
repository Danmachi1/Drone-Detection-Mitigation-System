package ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import logic.threat.*;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 📊 ThreatTable - Real-time list of currently identified threats,
 * with classification, confidence, and location details.
 */
public class ThreatTable extends VBox {

    private final TableView<ThreatRow> table = new TableView<>();
    private final ThreatDecisionEngine engine = new ThreatDecisionEngine();

    public ThreatTable() {
        setSpacing(10);
        getChildren().add(new Label("🚨 Threat Intelligence Feed"));
        		
        setupTable();
        getChildren().add(table);

        // Auto-refresh every 3 seconds
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> refresh());
            }
        }, 0, 3000);
    }

    private void setupTable() {
        TableColumn<ThreatRow, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().idProperty());

        TableColumn<ThreatRow, String> classCol = new TableColumn<>("Type");
        classCol.setCellValueFactory(c -> c.getValue().classificationProperty());

        TableColumn<ThreatRow, String> confCol = new TableColumn<>("Confidence");
        confCol.setCellValueFactory(c -> c.getValue().confidenceProperty());

        TableColumn<ThreatRow, String> zoneCol = new TableColumn<>("Zone");
        zoneCol.setCellValueFactory(c -> c.getValue().zoneProperty());

        table.getColumns().addAll(idCol, classCol, confCol, zoneCol);
        table.setPrefHeight(400);
    }

    private void refresh() {
        table.getItems().clear();
        Map<String, ThreatDecisionEngine.ThreatInfo> threats = engine.getLastThreats();

        for (Map.Entry<String, ThreatDecisionEngine.ThreatInfo> entry : threats.entrySet()) {
            String id = entry.getKey();
            ThreatDecisionEngine.ThreatInfo info = entry.getValue();

            ThreatRow row = new ThreatRow(id, info.type, String.format("%.1f%%", info.confidence * 100), info.zone);
            table.getItems().add(row);
        }
    }
}
