package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.*;

/**
 * 🌡️ ThermalSensorPlugin – Emits temperature-band detections.
 * Simulation mode outputs a target whose surface temp oscillates (30–50 °C).
 * Hardware mode is stubbed for a FLIR/Lepton SDK.
 */
public class ThermalSensorPlugin implements SensorPlugin {

    private boolean simulation = true;
    private long lastUpdate = 0;

    private final Random rng = new Random();
    private double posX = -90, posY = 40, velX = 8, velY = -4;
    private double temp = 40, phase = 0;

    private SensorDataRecord lastRecord = null;

    @Override
    public List<SensorDataRecord> poll() {
        return simulation ? simFrame() : hwFrame();
    }

    @Override
    public boolean isSimulationMode() {
        return simulation;
    }

    @Override
    public void setSimulationMode(boolean s) {
        if (simulation != s) {
            simulation = s;
            if (!s) connect();
        }
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdate;
    }

    private List<SensorDataRecord> simFrame() {
        phase += 0.1;
        temp = 40 + 5 * Math.sin(phase);

        posX += velX + rng.nextGaussian() * 0.3;
        posY += velY + rng.nextGaussian() * 0.3;

        long ts = System.currentTimeMillis();
        lastUpdate = ts;

        SensorDataRecord rec = new SensorDataRecord(
                ts, posX, posY,
                velX, velY,
                temp, // using altitude field as surface temp
                Math.atan2(velY, velX),
                "ThermalSim"
        );

        lastRecord = rec;
        return Collections.singletonList(rec);
    }

    private void connect() {
        System.out.println("⚠️ ThermalSensorPlugin: hardware init stub.");
    }

    private List<SensorDataRecord> hwFrame() {
        lastUpdate = System.currentTimeMillis();
        return Collections.emptyList(); // TODO: hardware integration
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
            drones.put("ThermalSim", new double[]{lastRecord.x, lastRecord.y});
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
        return "ThermalSensorPlugin";
    }
}
