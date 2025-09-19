package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.*;

/**
 * 🔊 SimulatedRfSensorPlugin – Emits a synthetic RF-fingerprint track.
 * Useful for unit-testing classifiers that look for RF signatures.
 */
public class SimulatedRfSensorPlugin implements SensorPlugin {

    private final Random rng = new Random();
    private long lastUpdate = 0;
    private String rfId = "RF-SIM-" + Integer.toHexString(rng.nextInt(0xFFFF));

    private double x = 80, y = -120;
    private double vx = -6, vy = 9;

    private SensorDataRecord lastRecord = null;

    @Override
    public List<SensorDataRecord> poll() {
        if (!active) return new ArrayList<>();
        List<SensorDataRecord> out = new ArrayList<>();

        x += vx + rng.nextGaussian() * 0.2;
        y += vy + rng.nextGaussian() * 0.2;

        long ts = System.currentTimeMillis();
        lastUpdate = ts;

        SensorDataRecord rec = new SensorDataRecord(
                ts, x, y,
                vx + rng.nextGaussian() * 0.15,
                vy + rng.nextGaussian() * 0.15,
                0, // altitude unknown
                Math.atan2(vy, vx),
                "SimRF", rfId, 0, false);

        lastRecord = rec;
        out.add(rec);
        return out;
    }

    @Override public boolean isSimulationMode()          { return true; }
    @Override public void setSimulationMode(boolean s)   { /* always sim */ }
    @Override public long getLastUpdateTime()            { return lastUpdate; }

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
            String id = (lastRecord.rfSignatureId != null) ? lastRecord.rfSignatureId : "SimRF";
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
        return "SimulatedRfSensorPlugin";
    }

}
