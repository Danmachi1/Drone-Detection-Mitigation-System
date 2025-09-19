package sensors.plugins;

import sensors.SensorDataRecord;
import sensors.SensorPlugin;

import java.util.*;

/**
 * 🔊 AcousticSensorPlugin – Simulates a microphone array using beamforming-inspired directional estimation.
 * Outputs bearing-based tracking for acoustic drones with extrapolated trajectory prediction.
 */
public class AcousticSensorPlugin implements SensorPlugin {

    private final Random rng = new Random();
    private boolean simulation = true;
    private long lastUpdateTime = 0;

    private double posX = -150;
    private double posY = 100;
    private double velX = 10;
    private double velY = -6;

    private SensorDataRecord lastRecord;

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> list = new ArrayList<>();
        if (!simulation) return list;

        posX += velX + rng.nextGaussian();  // Add small noise
        posY += velY + rng.nextGaussian();

        long ts = System.currentTimeMillis();
        lastUpdateTime = ts;

        int acousticDb = 60 + (int) (rng.nextGaussian() * 5);  // 60 dB ± noise

        SensorDataRecord rec = new SensorDataRecord(
                ts,
                posX, posY,
                velX, velY,
                0,                           // No altitude data from acoustic
                Math.atan2(velY, velX),     // heading in radians
                "AcousticSensor",
                null,
                acousticDb,
                false
        );

        lastRecord = rec;
        list.add(rec);
        return list;
    }

    @Override
    public List<double[]> getThreatPositions() {
        if (lastRecord == null) return new ArrayList<>();
        return Collections.singletonList(new double[]{lastRecord.x, lastRecord.y});
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        if (lastRecord != null) {
            drones.put("acoustic-target", new double[]{lastRecord.x, lastRecord.y});
        }
        return drones;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> path = new ArrayList<>();
        if (lastRecord != null) {
            for (int t = 1; t <= 5; t++) {  // Predict next 5 seconds linearly
                double futureX = lastRecord.x + lastRecord.vx * t;
                double futureY = lastRecord.y + lastRecord.vy * t;
                path.add(new double[]{futureX, futureY});
            }
        }
        return path;
    }

    @Override
    public boolean isSimulationMode() {
        return simulation;
    }

    @Override
    public void setSimulationMode(boolean sim) {
        this.simulation = sim;
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public String getPluginName() {
        return "Acoustic";
    }


 
    private boolean active = true; // default to enabled

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

}
