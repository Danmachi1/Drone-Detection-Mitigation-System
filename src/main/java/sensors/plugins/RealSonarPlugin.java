package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🌊 RealSonarPlugin – represents an actual sonar module, such as an
 * underwater acoustic or echolocation-based sensor for detecting
 * hovering or low-altitude drones using reflective sound waves.
 */
public class RealSonarPlugin implements SensorPlugin {

    private boolean simulationMode = false;
    private long lastUpdateTime = 0;

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> results = new ArrayList<>();

        // Placeholder: simulate sonar detection result
        long timestamp = System.currentTimeMillis();
        double x = 120.0;
        double y = 95.0;
        double vx = 0.0;
        double vy = 0.0;
        double altitude = 10.0; // Sonar is usually for close-range/low-altitude
        double heading = 0.0;

        SensorDataRecord record = new SensorDataRecord(
            timestamp,
            x, y,
            vx, vy,
            altitude, heading,
            "Sonar-Real",
            null,
            0,
            false
        );

        results.add(record);
        lastUpdateTime = timestamp;

        return results;
    }

    
    public void shutdown() {
        // Placeholder: close sonar communication port or shutdown hardware interface
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
        threats.add(new double[]{120.0, 95.0});
        return threats;
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        drones.put("Sonar-Real", new double[]{120.0, 95.0});
        return drones;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> path = new ArrayList<>();
        double x = 120.0;
        double y = 95.0;
        for (int i = 0; i < 5; i++) {
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
        return "RealSonarPlugin";
    }

}
