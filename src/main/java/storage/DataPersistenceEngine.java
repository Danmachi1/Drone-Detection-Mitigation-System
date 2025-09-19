package storage;

import sensors.SensorDataRecord;

import java.util.List;

/**
 * 💾 DataPersistenceEngine – Unified façade that writes sensor / fusion data to
 * multiple storage back-ends:
 *
 *   • {@link TrackDatabase} – primary SQLite datastore
 *   • {@link CsvExporter}   – optional rolling CSV file (for quick inspection)
 *
 * Call {@link #persistBatch(List)} each control-loop tick; it fans records out
 * to every enabled sink.  Flush or close resources via {@link #shutdown()}.
 */
public class DataPersistenceEngine {

    private final TrackDatabase db;
    private final CsvExporter   csv;
    private final boolean       csvEnabled;

    public DataPersistenceEngine(String dbPath,
                                 String csvPath,
                                 boolean enableCsv) {

        this.db         = new TrackDatabase(dbPath);
        this.csv        = new CsvExporter(csvPath);
        this.csvEnabled = enableCsv;

        db.open();
        if (csvEnabled) csv.open();
    }

    /** Persist an entire batch of SensorDataRecord rows. */
    public void persistBatch(List<SensorDataRecord> records) {
        if (records == null || records.isEmpty()) return;

        db.insertBatch(records);
        if (csvEnabled) csv.write(records);
    }

    /** Force-flush & close all sinks – call on app shutdown. */
    public void shutdown() {
        db.close();
        if (csvEnabled) csv.close();
    }
}
