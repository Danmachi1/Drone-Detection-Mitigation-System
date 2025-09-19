package sensors.plugins;

import sensors.SensorPlugin;
import sensors.SensorDataRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 📡 RealRadarSensorPlugin – connects to physical radar hardware.
 * Supports SDK or serial/USB interface. Placeholder for real integration.
 */
public class RealRadarSensorPlugin implements SensorPlugin {
	private double realX = 150.0;
	private double realY = 120.0;
	private double realVX = 1.5;
	private double realVY = 0.8;
	private long lastRealTime = System.currentTimeMillis();
	private double sweepAngle = 0;
	private final double radarRangeMeters = 250;  // customize per radar


    private boolean simulationMode = false;
    private long lastUpdateTime = 0;

    @Override
    public List<SensorDataRecord> poll() {
        List<SensorDataRecord> results = new ArrayList<>();

        long timestamp = System.currentTimeMillis();
        double dt = (timestamp - lastRealTime) / 1000.0;

        // Update position like a real object
        realX += realVX * dt;
        realY += realVY * dt;

        // Add slow drifting
        realVX += Math.sin(timestamp / 1000.0) * 0.01;
        realVY += Math.cos(timestamp / 1000.0) * 0.01;

        double altitude = 80.0;
        double heading = Math.atan2(realVY, realVX);

        SensorDataRecord record = new SensorDataRecord(
            timestamp,
            realX, realY,
            realVX, realVY,
            altitude, heading,
            "Radar-Real",
            null,
            0,
            false
        );

        results.add(record);
        lastUpdateTime = timestamp;
        lastRealTime = timestamp;
        sweepAngle += 1.2;
        if (sweepAngle > 360) sweepAngle = 0;


        return results;
    }

    
    public void shutdown() {
        // Implement radar SDK/API cleanup if needed
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
        threats.add(new double[]{150.0, 120.0});
        return threats;
    }

    @Override
    public Map<String, double[]> getDronePositions() {
        Map<String, double[]> drones = new HashMap<>();
        drones.put("Radar-Real", new double[]{150.0, 120.0});
        return drones;
    }

    @Override
    public List<double[]> getPredictedPaths() {
        List<double[]> path = new ArrayList<>();
        double x = 150.0, y = 120.0;
        double vx = 1.0, vy = 1.2;
        for (int i = 0; i < 5; i++) {
            x += vx;
            y += vy;
            path.add(new double[]{x, y});
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
        return "Radar";
    }
    @Override
    public double getSweepAngle() {
        return sweepAngle;
    }

    @Override
    public double getRangeMeters() {
        return radarRangeMeters;
    }


}
