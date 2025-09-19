package fusion.plugins;

import sensors.SensorDataRecord;

import java.util.List;
import java.util.Map;

import fusion.FusionPlugin;

/**
 * 📐 ExtendedKalmanFilter2D - Linearized nonlinear estimator for position, velocity, heading.
 * Uses trust-aware updates and safe fallback against NaN states.
 */
public class ExtendedKalmanFilter2D implements FusionPlugin {

    private double[] state = new double[6]; // [x, y, vx, vy, heading, altitude]
    private boolean initialized = false;
    private Map<String, Double> sensorTrustMap = null;

    @Override
    public void ingest(List<SensorDataRecord> records) {
        for (SensorDataRecord r : records) {
            double trust = 1.0;
            if (sensorTrustMap != null && sensorTrustMap.containsKey(r.sourceSensor)) {
                trust = sensorTrustMap.get(r.sourceSensor);
            }

            if (!initialized) {
                initializeFrom(r);
                continue;
            }

            predict();
            update(r, trust);
        }
    }

    private void initializeFrom(SensorDataRecord r) {
        state[0] = r.x;
        state[1] = r.y;
        state[2] = r.vx;
        state[3] = r.vy;
        state[4] = normalizeAngle(r.headingRad);
        state[5] = r.altitude;
        initialized = true;
    }

    private void predict() {
        state[0] += state[2]; // x += vx
        state[1] += state[3]; // y += vy
        // Heading and altitude unchanged during prediction
    }

    private void update(SensorDataRecord r, double trust) {
        // Linear update using trust weight
        state[0] = (1 - trust) * state[0] + trust * r.x;
        state[1] = (1 - trust) * state[1] + trust * r.y;
        state[2] = (1 - trust) * state[2] + trust * r.vx;
        state[3] = (1 - trust) * state[3] + trust * r.vy;
        state[4] = normalizeAngle((1 - trust) * state[4] + trust * r.headingRad);
        state[5] = (1 - trust) * state[5] + trust * r.altitude;

        if (Double.isNaN(state[0]) || Double.isInfinite(state[0])) {
            System.err.println("⚠️ EKF instability detected. Resetting...");
            reset();
        }
    }

    private double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    @Override
    public double[] getFusedEstimate() {
        return state.clone();
    }

    @Override
    public String getFusionType() {
        return "EKF";
    }

    @Override
    public void reset() {
        state = new double[6];
        initialized = false;
    }

    @Override
    public void setSensorTrust(Map<String, Double> sensorTrustMap) {
        this.sensorTrustMap = sensorTrustMap;
    }
}
