package fusion.plugins;

import sensors.SensorDataRecord;

import java.util.List;
import java.util.Map;

import fusion.FusionPlugin;

/**
 * 📉 KalmanFilter2D - Standard linear Kalman Filter for 2D tracking.
 * Tracks position and velocity in x and y directions.
 */
public class KalmanFilter2D implements FusionPlugin {

    private double[] state = new double[4]; // [x, y, vx, vy]
    private double[][] covariance = new double[4][4];
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
                // Initialize state with first record
                state[0] = record.x;
                state[1] = record.y;
                state[2] = record.vx;
                state[3] = record.vy;
                initialized = true;
                initializeCovariance();
                continue;
            }

            predict();
            update(record, trust);
        }
    }

    private void initializeCovariance() {
        for (int i = 0; i < 4; i++) {
            covariance[i][i] = 1.0; // Start with identity matrix
        }
    }

    private void predict() {
        // Very basic motion model with no acceleration (constant velocity)
        state[0] += state[2];
        state[1] += state[3];
    }

    private void update(SensorDataRecord record, double trust) {
        // Simple update: blend current state with observation
        state[0] = (1 - trust) * state[0] + trust * record.x;
        state[1] = (1 - trust) * state[1] + trust * record.y;
        state[2] = (1 - trust) * state[2] + trust * record.vx;
        state[3] = (1 - trust) * state[3] + trust * record.vy;
    }

    @Override
    public double[] getFusedEstimate() {
        return new double[]{state[0], state[1], state[2], state[3], Math.atan2(state[3], state[2]), 0.0};
    }

    @Override
    public String getFusionType() {
        return "Kalman";
    }

    @Override
    public void reset() {
        initialized = false;
        state = new double[4];
        covariance = new double[4][4];
    }

    @Override
    public void setSensorTrust(Map<String, Double> sensorTrustMap) {
        this.sensorTrustMap = sensorTrustMap;
    }
}

