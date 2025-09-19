package sensors;

/**
 * 📡 SensorDataRecord – Normalised, timestamped output from any SkyShield sensor
 * (radar, RF sniffer, acoustic mic-array, etc.).  All downstream components –
 * fusion, prediction, threat engines – rely on this immutable DTO.
 */
public class SensorDataRecord {

    /* ── Core kinematic state ─────────────────────────────────────── */
    public final long   timestamp;     // epoch ms
    public final double x	,  y;         // metres (ENU)
    public final double vx, vy;        // m / s
    public final double altitude;      // metres (AGL)
    public final double headingRad;    // radians (0 = east, CCW +)

    /* ── Meta-data / multi-modal features ─────────────────────────── */
    public final String  sourceSensor;   // “Radar-A”, “Thermal-Cam-2” …
    public final String  rfSignatureId;  // hash/key if RF library matched (null = none)
    public final int     acousticLevel;  // dB SPL (0-255 if quantised)
    public final boolean visuallyDetected; // true if optical pipeline confirms

    /* ─────────────────────────────────────────────────────────────── */

    /** Minimal constructor used by most real-time pipelines. */
    public SensorDataRecord(
            long timestamp,
            double x,          double y,
            double vx,         double vy,
            double altitude,   double headingRad,
            String sourceSensor) {

        this(timestamp, x, y, vx, vy, altitude, headingRad,
             sourceSensor, null, 0, false);
    }

    /** Full constructor for replay or offline ingestion. */
    public SensorDataRecord(
            long    timestamp,
            double  x,          double y,
            double  vx,         double vy,
            double  altitude,   double headingRad,
            String  sourceSensor,
            String  rfSignatureId,
            int     acousticLevel,
            boolean visuallyDetected) {

        this.timestamp        = timestamp;
        this.x                = x;
        this.y                = y;
        this.vx               = vx;
        this.vy               = vy;
        this.altitude         = altitude;
        this.headingRad       = headingRad;
        this.sourceSensor     = sourceSensor;
        this.rfSignatureId    = rfSignatureId;
        this.acousticLevel    = acousticLevel;
        this.visuallyDetected = visuallyDetected;
    }

    /* ── Convenience getters (optional) ───────────────────────────── */

    public double speed()   { return Math.hypot(vx, vy); }
    public double bearing() { return headingRad; }

    @Override public String toString() {
        return String.format(
            "[%d] %s  (%.1f, %.1f, alt %.1f)  v=(%.1f,%.1f)  rf=%s  dB=%d  vis=%s",
            timestamp, sourceSensor, x, y, altitude, vx, vy,
            rfSignatureId, acousticLevel, visuallyDetected);
    }
}
