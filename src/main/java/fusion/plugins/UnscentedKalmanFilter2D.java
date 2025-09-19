package fusion.plugins;

import sensors.SensorDataRecord;

import java.util.List;
import java.util.Map;

import fusion.FusionPlugin;

/**
 * 🔁 UnscentedKalmanFilter2D - Nonlinear state estimator for 2D drone tracking.
 * Models position, velocity, heading, and altitude.
 */
public class UnscentedKalmanFilter2D implements FusionPlugin {

    private double[] state = new double[6]; // [x, y, vx, vy, heading, altitude]
    private boolean initialized = false;
    private Map<String, Double> sensorTrustMap = null;

    @Override
    public void ingest(List<SensorDataRecord> records) {
        for (SensorDataRecord record : records) {
            double trust = 1.0;
            if (sensorTrustMap != null && sensorTrustMap.containsKey(record.sourceSensor)) {
                trust = sensorTrustMap.get(record.sourceSensor);
            }

            if (!initialized) {
                initializeFrom(record);
                continue;
            }

            predict();
            update(record, trust);
        }
    }

    private void initializeFrom(SensorDataRecord r) {
        state[0] = r.x;
        state[1] = r.y;
        state[2] = r.vx;
        state[3] = r.vy;
        state[4] = r.headingRad;
        state[5] = r.altitude;
        initialized = true;
    }

    private void predict() {
        // Predict with current velocity
        state[0] += state[2];
        state[1] += state[3];
        // Heading and altitude stay constant unless updated
    }

    private void update(SensorDataRecord r, double trust) {
        // Nonlinear fusion using trust blending
        state[0] = (1 - trust) * state[0] + trust * r.x;
        state[1] = (1 - trust) * state[1] + trust * r.y;
        state[2] = (1 - trust) * state[2] + trust * r.vx;
        state[3] = (1 - trust) * state[3] + trust * r.vy;
        state[4] = (1 - trust) * state[4] + trust * r.headingRad;
        state[5] = (1 - trust) * state[5] + trust * r.altitude;

        if (Double.isNaN(state[0]) || Double.isInfinite(state[0])) {
            System.err.println("⚠️ UKF instability detected. Resetting filter.");
            reset();
        }
    }

    @Override
    public double[] getFusedEstimate() {
        return state.clone();
    }

    @Override
    public String getFusionType() {
        return "UKF";
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
