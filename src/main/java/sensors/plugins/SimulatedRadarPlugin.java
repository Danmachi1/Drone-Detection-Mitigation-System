package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.*;

/**
 * 🛰️ SimulatedRadarPlugin – Full 3D multi-agent swarm emulator with realistic threat dynamics.
 */
public class SimulatedRadarPlugin implements SensorPlugin {

    private static final int NUM_THREATS = 1;
    private static final double RADAR_RANGE = 250;

    private final double[] x = new double[NUM_THREATS];
    private final double[] y = new double[NUM_THREATS];
    private final double[] z = new double[NUM_THREATS];
    private final double[] vx = new double[NUM_THREATS];
    private final double[] vy = new double[NUM_THREATS];
    private final double[] vz = new double[NUM_THREATS];
    private final boolean[] stealth = new boolean[NUM_THREATS];
    private final BehaviorType[] behavior = new BehaviorType[NUM_THREATS];

    private final List<SensorDataRecord> lastRecords = new ArrayList<>();
    private final Random rand;
    private final int instanceId;

    private long lastSimTime = System.currentTimeMillis();
    private long lastUpdateTime = 0;
    private double sweepAngle = 0;
    private boolean simulationMode = true;
    private boolean active = true;

    // Behavior types
    private enum BehaviorType { SCOUT, AGGRESSOR, DECOY }

    public SimulatedRadarPlugin(int id) {
        this.instanceId = id;
        this.rand = new Random(System.currentTimeMillis() + id * 1000L);

        for (int i = 0; i < NUM_THREATS; i++) {
            x[i] = 50 + rand.nextDouble() * 100;
            y[i] = 50 + rand.nextDouble() * 100;
            z[i] = 20 + rand.nextDouble() * 30;
            vx[i] = 1 + rand.nextDouble();
            vy[i] = 1 + rand.nextDouble();
            vz[i] = (rand.nextDouble() - 0.5) * 0.5;
            stealth[i] = rand.nextDouble() < 0.2;
            behavior[i] = BehaviorType.values()[rand.nextInt(BehaviorType.values().length)];
        }
    }

    @Override
    public List<SensorDataRecord> poll() {
   

        List<SensorDataRecord> batch = new ArrayList<>();
        long now = System.currentTimeMillis();
        double dt = (now - lastSimTime) / 1000.0;
        lastRecords.clear();

        for (int i = 0; i < NUM_THREATS; i++) {
        	double heading = Math.atan2(vy[i], vx[i]);
            String source;
            if (i == 0) {
                source = "Drone-Sim-" + instanceId + "-D" + i; // treat as drone
            } else {
                source = "Radar-Sim-" + instanceId + "-T" + i; // treat as threat
            }


            SensorDataRecord record = new SensorDataRecord(
                now, x[i], y[i], vx[i], vy[i], z[i], heading,
                "Radar-Sim-" + instanceId + "-T" + i
            );

            if (!stealth[i]) {
                batch.add(record);
                lastRecords.add(record);
            }
            // Add behavior-based zig-zag or swarm logic
            double zigzagX = Math.sin(now / 1000.0 + i) * 0.15;
            double zigzagY = Math.cos(now / 1000.0 + i) * 0.15;

            switch (behavior[i]) {
                case SCOUT -> {
                    vx[i] += rand.nextGaussian() * 0.05 + zigzagX;
                    vy[i] += rand.nextGaussian() * 0.05 + zigzagY;
                }
                case AGGRESSOR -> {
                    vx[i] += 0.2 + rand.nextGaussian() * 0.1;
                    vy[i] += 0.2 + rand.nextGaussian() * 0.1;
                }
                case DECOY -> {
                    vx[i] += rand.nextGaussian() * 0.15;
                    vy[i] += rand.nextGaussian() * 0.15;
                }
            }

            vz[i] += rand.nextGaussian() * 0.02;

            x[i] += vx[i] * dt + rand.nextGaussian() * 0.1;
            y[i] += vy[i] * dt + rand.nextGaussian() * 0.1;
            z[i] += vz[i] * dt;

            // Reflect off radar bounds
            if (x[i] < 0 || x[i] > RADAR_RANGE) vx[i] *= -1;
            if (y[i] < 0 || y[i] > RADAR_RANGE) vy[i] *= -1;
            if (z[i] < 10 || z[i] > 60) vz[i] *= -1;

            // Avoid center zone (No-fly zone)
            double dx = x[i] - 125, dy = y[i] - 125;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < 50) {
                vx[i] += dx / dist * 1.5;
                vy[i] += dy / dist * 1.5;
            }

        }

        lastSimTime = now;
        lastUpdateTime = now;
        sweepAngle = (sweepAngle + 1.5) % 360;

        System.out.printf("[SimRadar-%d] Visible Threats: %d\n", instanceId, batch.size());
        return batch;
    }

    // Plugin meta
    @Override public boolean isSimulationMode() { return simulationMode; }
    @Override public void setSimulationMode(boolean sim) { this.simulationMode = sim; }
    @Override public long getLastUpdateTime() { return lastUpdateTime; }
    @Override public boolean isActive() { return active; }
    @Override public void activate() { active = true; }
    @Override public void deactivate() { active = false; }
    @Override public String getPluginName() { return "SimulatedRadarPlugin-" + instanceId; }
    @Override public double getSweepAngle() { return sweepAngle; }
    @Override public double getRangeMeters() { return RADAR_RANGE; }

    // Sensor data for rendering
    @Override
    public List<double[]> getThreatPositions() {
        List<double[]> threats = new ArrayList<>();
        for (SensorDataRecord r : new ArrayList<>(lastRecords)) {
            threats.add(new double[]{r.x, r.y});
        }
        return threats;
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        for (SensorDataRecord r : lastRecords) {
            if (r.sourceSensor.startsWith("Drone-Sim")) {
                drones.put(r.sourceSensor, new double[]{r.x, r.y});
            }
        }
        return drones;
    }


    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> paths = new ArrayList<>();
        for (SensorDataRecord r : new ArrayList<>(lastRecords)) {
            for (int i = 1; i <= 3; i++) {
                paths.add(new double[]{r.x + i * r.vx * 0.8, r.y + i * r.vy * 0.8});
            }
        }
        return paths;
    }


    public void shutdown() {}
}
