package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.*;

/**
 * 🌊 SimulatedSonarPlugin – emits mock sonar signals to mimic
 * underwater or echo-based drone detection (e.g., urban canyon ops).
 */
public class SimulatedSonarPlugin implements SensorPlugin {

    private boolean simulationMode = true;
    private long lastUpdateTime = 0;
    private final Random rand = new Random();
    private SensorDataRecord lastRecord = null;

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> batch = new ArrayList<>();
        long timestamp = System.currentTimeMillis();

        double x = 60 + rand.nextGaussian() * 3;
        double y = 90 + rand.nextGaussian() * 3;
        double vx = 0.3 + rand.nextGaussian() * 0.1;
        double vy = 0.4 + rand.nextGaussian() * 0.1;
        double alt = 25 + rand.nextGaussian(); // close-range echo
        double heading = Math.atan2(vy, vx);

        SensorDataRecord record = new SensorDataRecord(
                timestamp,
                x, y,
                vx, vy,
                alt, heading,
                "Sonar-Sim",
                null,
                0,
                false // sonar has no visual
        );

        lastRecord = record;
        batch.add(record);
        lastUpdateTime = timestamp;
        return batch;
    }

    public void shutdown() {
        // No cleanup necessary for simulated sonar
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
        if (lastRecord != null) {
            threats.add(new double[]{lastRecord.x, lastRecord.y});
        }
        return threats;
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        if (lastRecord != null) {
            drones.put("Sonar-Sim", new double[]{lastRecord.x, lastRecord.y});
        }
        return drones;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> path = new ArrayList<>();
        if (lastRecord != null) {
            double x = lastRecord.x;
            double y = lastRecord.y;
            double vx = lastRecord.vx;
            double vy = lastRecord.vy;
            for (int i = 1; i <= 5; i++) {
                path.add(new double[]{x + i * vx, y + i * vy});
            }
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
        return "SimulatedSonarPlugin";
    }

}
