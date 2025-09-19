package storage;

import sensors.SensorDataRecord;

import java.io.*;
import java.util.List;

/**
 * 📑 CsvExporter – Simple rolling CSV writer for quick log inspection.
 * Fields align with SensorDataRecord.  Call write(batch) each tick;
 * remember to close() on shutdown.
 */
public class CsvExporter {

    private final File   file;
    private BufferedWriter bw;

    public CsvExporter(String path) { this.file = new File(path); }

    public void open() {
        try {
            bw = new BufferedWriter(new FileWriter(file, /*append*/ true));
            if (file.length() == 0) writeHeader();
            System.out.println("📝 CsvExporter logging to " + file.getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("⚠️  CsvExporter open failed: " + ex.getMessage());
        }
    }

    private void writeHeader() throws IOException {
        bw.write("ts,x,y,vx,vy,alt,heading,source,rfId,acoustic,visual\n");
    }

    /** Appends a batch of records; flushes every call. */
    public void write(List<SensorDataRecord> batch) {
        if (bw == null || batch == null || batch.isEmpty()) return;
        try {
            for (SensorDataRecord r : batch) {
                bw.write(r.timestamp + "," + r.x + "," + r.y + ","
                        + r.vx + "," + r.vy + ","
                        + r.altitude + "," + r.headingRad + ","
                        + r.sourceSensor + "," + safe(r.rfSignatureId) + ","
                        + r.acousticLevel + "," + (r.visuallyDetected ? 1 : 0) + "\n");
            }
            bw.flush();
        } catch (IOException ex) {
            System.err.println("⚠️  CsvExporter write failed: " + ex.getMessage());
        }
    }

    public void close() {
        try { if (bw != null) bw.close(); } catch (IOException ignored) {}
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
