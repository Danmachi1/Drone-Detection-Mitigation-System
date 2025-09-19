package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 📁 ThreatRow - Table row representing a single threat.
 */
public class ThreatRow {

    private final StringProperty id;
    private final StringProperty classification;
    private final StringProperty confidence;
    private final StringProperty zone;

    public ThreatRow(String id, String classification, String confidence, String zone) {
        this.id = new SimpleStringProperty(id);
        this.classification = new SimpleStringProperty(classification);
        this.confidence = new SimpleStringProperty(confidence);
        this.zone = new SimpleStringProperty(zone);
    }

    public StringProperty idProperty() { return id; }
    public StringProperty classificationProperty() { return classification; }
    public StringProperty confidenceProperty() { return confidence; }
    public StringProperty zoneProperty() { return zone; }
}
	