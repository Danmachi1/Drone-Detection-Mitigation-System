package sensors.plugins;

import sensors.SensorDataRecord;
import sensors.SensorPlugin;

import java.util.*;

/**
 * 🌊 SonarSensorPlugin – Adapter for an ultrasonic / sonar altimeter that
 * measures ground–drone range (altitude) and rudimentary vertical velocity.
 *
 * • Simulation mode – emits sinusoidal height profile.
 * • Hardware mode – to be implemented via UART/I2C.
 */
public class SonarSensorPlugin implements SensorPlugin {

    private boolean simulationMode = true;
    private long lastUpdate = 0;
    private final Random rng = new Random();
    private double phaseRad = 0;
    private final double step = Math.PI / 30;

    private SensorDataRecord lastRecord = null;

    @Override
    public List<SensorDataRecord> poll() {
        return simulationMode ? genSimSample() : readHardwareSample();
    }

    @Override
    public boolean isSimulationMode() {
        return simulationMode;
    }

    @Override
    public void setSimulationMode(boolean s) {
        if (simulationMode != s) {
            simulationMode = s;
            if (!s) connectHardware();
        }
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdate;
    }

    private List<SensorDataRecord> genSimSample() {
        phaseRad += step;
        double altitude = 40 + 5 * Math.sin(phaseRad) + rng.nextGaussian();

        long ts = System.currentTimeMillis();
        lastUpdate = ts;

        SensorDataRecord rec = new SensorDataRecord(
                ts,
                0, 0,
                0, 0,
                altitude,
                0,
                "SonarSim"
        );

        lastRecord = rec;
        return Collections.singletonList(rec);
    }

    private void connectHardware() {
        System.out.println("⚠️  SonarSensorPlugin: hardware init stub.");
        // TODO: open serial port, configure baud rate, etc.
    }

    private List<SensorDataRecord> readHardwareSample() {
        lastUpdate = System.currentTimeMillis();
        // TODO: implement actual sonar device reading
        return Collections.emptyList();
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
            drones.put("SonarSim", new double[]{lastRecord.x, lastRecord.y});
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
        return "SonarSensorPlugin";
    }

}
