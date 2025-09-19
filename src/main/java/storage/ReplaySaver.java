package storage;

import sensors.SensorDataRecord;

import java.util.List;

/**
 * 📼 ReplaySaver – Dumps a complete simulation run to a CSV file
 * that can later be consumed by ReplaySensorPlugin.
 */
public final class ReplaySaver {

    private final CsvExporter exporter;

    public ReplaySaver(String csvPath) {
        exporter = new CsvExporter(csvPath);
        exporter.open();
    }

    public void append(List<SensorDataRecord> batch) {
        exporter.write(batch);
    }

    /** Must be called at sim end to close file handle. */
    public void close() {
        exporter.close();
    }
}
