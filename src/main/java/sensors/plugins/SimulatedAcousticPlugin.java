package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * 🔊 SimulatedAcousticPlugin – simulates directional acoustic data
 * from a microphone array for sound-based drone detection.
 */
public class SimulatedAcousticPlugin implements SensorPlugin {

    private boolean simulationMode = true;
    private long lastUpdateTime = 0;
    private final Random rand = new Random();
    private SensorDataRecord lastRecord = null;


    @Override
    public List<SensorDataRecord> poll() {


        List<SensorDataRecord> batch = new ArrayList<>();
        long timestamp = System.currentTimeMillis();

        // Simulate a sound-based detection
        double x = 110 + rand.nextGaussian() * 6;
        double y = 95 + rand.nextGaussian() * 6;
        double vx = 1.1 + rand.nextGaussian() * 0.4;
        double vy = 1.0 + rand.nextGaussian() * 0.4;
        double alt = 38 + rand.nextGaussian() * 1.5;
        double heading = Math.atan2(vy, vx);
        int acousticLevel = 75 + rand.nextInt(20);  // 75–95 dB

        SensorDataRecord record = new SensorDataRecord(
                timestamp,
                x, y,
                vx, vy,
                alt, heading,
                "Acoustic-Sim",
                null,
                acousticLevel,
                false
        );
		// inside poll() after creating `record`:
    	lastRecord = record;

        batch.add(record);
        lastUpdateTime = timestamp;
        return batch;
    }

    
    public void shutdown() {
        // No cleanup required for simulated plugin
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
            drones.put("Acoustic-Sim", new double[]{lastRecord.x, lastRecord.y});
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
        return "SimulatedAcousticPlugin";
    }


}
