package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.*;

/**
 * 👁️ SimulatedVisualPlugin – simulates a camera-based optical detection system.
 * Used to confirm visual presence of aerial objects.
 */
public class SimulatedVisualPlugin implements SensorPlugin {

    private boolean simulationMode = true;
    private long lastUpdateTime = 0;
    private final Random rand = new Random();
    private SensorDataRecord lastRecord = null;

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> batch = new ArrayList<>();
        long timestamp = System.currentTimeMillis();

        double x = 120 + rand.nextGaussian() * 4;
        double y = 190 + rand.nextGaussian() * 4;
        double vx = 1.2 + rand.nextGaussian() * 0.5;
        double vy = 0.8 + rand.nextGaussian() * 0.5;
        double alt = 35 + rand.nextGaussian() * 2;
        double heading = Math.atan2(vy, vx);

        SensorDataRecord record = new SensorDataRecord(
                timestamp,
                x, y,
                vx, vy,
                alt, heading,
                "Visual-Sim",
                null,
                0,
                true   // confirmed visual
        );

        lastRecord = record;
        batch.add(record);
        lastUpdateTime = timestamp;
        return batch;
    }

    public void shutdown() {
        // No hardware to clean up in sim mode
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
            drones.put("Visual-Sim", new double[]{lastRecord.x, lastRecord.y});
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
        return "SimulatedVisualPlugin";
    }

}
