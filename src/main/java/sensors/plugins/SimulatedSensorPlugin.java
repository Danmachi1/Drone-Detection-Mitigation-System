package sensors.plugins;

import sensors.SensorDataRecord;
import sensors.SensorPlugin;

import java.util.*;

/**
 * 🧪 SimulatedSensorPlugin – Drop-in synthetic sensor that creates a single
 * moving target with configurable start position & velocity. It is useful
 * for unit tests or demo scenes when a specialised simulator is overkill.
 *
 * Example:
 *     var sim = new SimulatedSensorPlugin(-50, 80, 6, -4);
 *     sim.setId("SIM-01");
 */
public class SimulatedSensorPlugin implements SensorPlugin {

    private final Random rng = new Random();

    private double posX, posY;
    private double velX, velY;

    private String id = "SIM-GEN";
    private long lastStamp = 0;
    private boolean simulation = true;

    private SensorDataRecord lastRecord = null;

    public SimulatedSensorPlugin() {
        this(0, 0, 10, 0);
    }

    public SimulatedSensorPlugin(double x, double y, double vx, double vy) {
        this.posX = x;
        this.posY = y;
        this.velX = vx;
        this.velY = vy;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> list = new ArrayList<>();

        posX += velX + rng.nextGaussian() * 0.25;
        posY += velY + rng.nextGaussian() * 0.25;

        long ts = System.currentTimeMillis();
        lastStamp = ts;

        SensorDataRecord rec = new SensorDataRecord(
                ts,
                posX, posY,
                velX + rng.nextGaussian() * 0.2,
                velY + rng.nextGaussian() * 0.2,
                50,
                Math.atan2(velY, velX),
                id
        );

        lastRecord = rec;
        list.add(rec);
        return list;
    }

    @Override
    public boolean isSimulationMode() {
        return true;
    }

    @Override
    public void setSimulationMode(boolean sim) {
        // ignored (always sim)
    }

    @Override
    public long getLastUpdateTime() {
        return lastStamp;
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
        return "SimulatedSensorPlugin";
    }

}
