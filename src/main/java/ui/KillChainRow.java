package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 🧱 KillChainRow - Represents a single row in the kill chain table.
 */
public class KillChainRow {

    private final StringProperty threatId;
    private final StringProperty classification;
    private final StringProperty logicMode;
    private final StringProperty assignedDrone;

    public KillChainRow(String threatId, String classification, String logicMode, String assignedDrone) {
        this.threatId = new SimpleStringProperty(threatId);
        this.classification = new SimpleStringProperty(classification);
        this.logicMode = new SimpleStringProperty(logicMode);
        this.assignedDrone = new SimpleStringProperty(assignedDrone);
    }

    public StringProperty threatIdProperty() { return threatId; }
    public StringProperty classificationProperty() { return classification; }
    public StringProperty logicModeProperty() { return logicMode; }
    public StringProperty assignedDroneProperty() { return assignedDrone; }
}
