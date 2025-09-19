package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🔊 RealAcousticPlugin – connects to live microphone arrays or acoustic
 * direction-finding hardware (e.g., TDOA). Outputs directional sound detections.
 */
public class RealAcousticPlugin implements SensorPlugin {

    private boolean simulationMode = false;
    private long lastUpdateTime = 0;

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> results = new ArrayList<>();

        // Placeholder: simulate detection
        long timestamp = System.currentTimeMillis();
        double x = 275.0;
        double y = 225.0;
        double vx = 0.0;
        double vy = 0.0;
        double altitude = 45.0;
        double heading = 0.0;
        int acousticLevel = 140;

        SensorDataRecord record = new SensorDataRecord(
            timestamp,
            x, y,
            vx, vy,
            altitude, heading,
            "Acoustic-Real",
            null,
            acousticLevel,
            false
        );

        results.add(record);
        lastUpdateTime = timestamp;

        return results;
    }

   
    public void shutdown() {
        // Real microphone array shutdown logic if needed
    }

    @Override
    public boolean isSimulationMode() {
        return simulationMode;
    }

    @Override
    public void setSimulationMode(boolean sim) {
        this.simulationMode = sim;
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    @Override
    public List<double[]> getThreatPositions() {
        List<double[]> threats = new ArrayList<>();
        threats.add(new double[]{275.0, 225.0});
        return threats;
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        drones.put("Acoustic-Real", new double[]{275.0, 225.0});
        return drones;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> path = new ArrayList<>();
        double vx = 0.0, vy = 0.0;
        double x = 275.0, y = 225.0;
        for (int i = 0; i < 5; i++) {
            x += vx;
            y += vy;
            path.add(new double[]{x, y});
        }
        return path;
    }
    private boolean active = true;

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getPluginName() {
        return "Acoustic";
    }

}
