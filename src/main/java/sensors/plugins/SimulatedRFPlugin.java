package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 📶 SimulatedRFPlugin – simulates RF signal detection for specific drone IDs.
 */
public class SimulatedRFPlugin implements SensorPlugin {
    private boolean active = true;

    private boolean simulationMode = true;
    private long lastUpdateTime = 0;
    private final Random rand = new Random();

    private SensorDataRecord lastRecord = null;

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> batch = new ArrayList<>();
        long timestamp = System.currentTimeMillis();

        double x = 150 + rand.nextGaussian() * 10;
        double y = 300 + rand.nextGaussian() * 10;
        double vx = 3 + rand.nextGaussian();
        double vy = 2 + rand.nextGaussian();
        double alt = 40 + rand.nextGaussian() * 3;
        double heading = Math.atan2(vy, vx);
        String rfSignature = "RF-A1-" + (1000 + rand.nextInt(9000));

        SensorDataRecord record = new SensorDataRecord(
                timestamp,
                x, y,
                vx, vy,
                alt, heading,
                "RF-Sim",
                rfSignature,
                0,
                false
        );

        lastRecord = record;
        batch.add(record);
        lastUpdateTime = timestamp;
        return batch;
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
            String id = (lastRecord.rfSignatureId != null) ? lastRecord.rfSignatureId : "RF-Sim";
            drones.put(id, new double[]{lastRecord.x, lastRecord.y});
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
        return "SimulatedRfSensorPlugin";
    }

    public void shutdown() {
        // No-op for simulation
    }
}
