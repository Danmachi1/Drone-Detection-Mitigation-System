package storage;

import sensors.SensorDataRecord;

import java.sql.*;
import java.util.List;

/**
 * 🗄️ TrackDatabase – Thin JDBC wrapper for an on-disk SQLite DB that stores
 * every SensorDataRecord.  Schema is created automatically on first open().
 *
 * DB schema:
 *   CREATE TABLE IF NOT EXISTS track (
 *     ts      INTEGER,
 *     x       REAL,  y REAL,
 *     vx      REAL,  vy REAL,
 *     alt     REAL,
 *     heading REAL,
 *     source  TEXT,
 *     rfId    TEXT,
 *     acoustic INT,
 *     visual   INT
 *   );
 */
public class TrackDatabase {

    private final String url;        // jdbc:sqlite:tracks.db
    private Connection   conn;

    public TrackDatabase(String filePath) {
        this.url = "jdbc:sqlite:" + filePath;
    }

    /* ── Lifecycle ─────────────────────────────────────────── */

    public void open() {
        try {
            conn = DriverManager.getConnection(url);
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS track (
                      ts INTEGER, x REAL, y REAL,
                      vx REAL, vy REAL, alt REAL,
                      heading REAL, source TEXT,
                      rfId TEXT, acoustic INT, visual INT)""");
            }
            System.out.println("📚 TrackDatabase opened " + url);
        } catch (Exception ex) {
            System.err.println("⚠️  TrackDatabase open failed: " + ex.getMessage());
        }
    }

    public void close() {
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
    }

    /* ── Batch insert ───────────────────────────────────────── */

    public void insertBatch(List<SensorDataRecord> batch) {
        if (batch == null || batch.isEmpty() || conn == null) return;

        String sql = """
            INSERT INTO track
            (ts,x,y,vx,vy,alt,heading,source,rfId,acoustic,visual)
            VALUES (?,?,?,?,?,?,?,?,?,?,?)""";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (SensorDataRecord r : batch) {
                ps.setLong (1, r.timestamp);
                ps.setDouble(2, r.x);        ps.setDouble(3, r.y);
                ps.setDouble(4, r.vx);       ps.setDouble(5, r.vy);
                ps.setDouble(6, r.altitude); ps.setDouble(7, r.headingRad);
                ps.setString(8, r.sourceSensor);
                ps.setString(9, r.rfSignatureId);
                ps.setInt   (10, r.acousticLevel);
                ps.setInt   (11, r.visuallyDetected ? 1 : 0);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (Exception ex) {
            System.err.println("⚠️  TrackDatabase insert failed: " + ex.getMessage());
        }
    }
}
