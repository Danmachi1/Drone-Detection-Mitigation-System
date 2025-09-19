package sensors;

import java.util.List;
import java.util.Map;

import fusion.TargetTrackManager;

/**
 * 🔌 SensorPlugin – Contract for pull-style sensor adapters that emit
 * {@link SensorDataRecord} objects. The fusion engine polls each plugin
 * regularly; real implementations talk to hardware, while simulated ones
 * generate synthetic tracks.
 */
public interface SensorPlugin {
    // Shared tracker (optional)
    TargetTrackManager targetTrackManager = new TargetTrackManager();

    /** Poll the sensor once; return zero or more new records. */
    List<SensorDataRecord> poll();

    /** True when the plugin is running in simulation mode. */
    boolean isSimulationMode();

    /** Enable / disable simulation mode (tests or demos). */
    void setSimulationMode(boolean sim);

    /** Epoch-ms when {@link #poll()} last produced data (0 = never). */
    long getLastUpdateTime();

    /** Returns a list of all currently detected threat positions. */
    List<double[]> getThreatPositions();

    /** Returns a map of all drone positions, keyed by drone ID. */
    Map<String, double[]> getDronePositions();

    /** Returns a list of predicted threat path points. */
    List<double[]> getPredictedPaths();

    /** Returns the human-friendly plugin name (e.g., "Radar", "RF") */
    String getPluginName();

    /** Whether the plugin is currently active (enabled) */
    boolean isActive();

    /** Activates this plugin (UI toggle ON) */
    void activate();

    /** Deactivates this plugin (UI toggle OFF) */
    void deactivate();
    /** Returns the current sweep angle (0–360°) for this radar. */
    default double getSweepAngle() { return 0; }

    /** Returns the detection range of this radar in meters. */
    default double getRangeMeters() { return 200; }
    /** Optional unique ID used for UI toggle tracking (e.g., "Radar-Sim0"). */
    default String getUniqueId() {
        return getPluginName();
    }

}
