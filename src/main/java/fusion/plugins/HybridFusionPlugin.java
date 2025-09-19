package fusion.plugins;

import sensors.SensorDataRecord;
import fusion.FusionPlugin;          
import java.util.*;

/**
 * 🧠 HybridFusionPlugin - Combines UKF, rule-based estimates, and trust weights
 * to produce robust fused state estimates.
 */
public class HybridFusionPlugin implements FusionPlugin {

    private UnscentedKalmanFilter2D ukf = new UnscentedKalmanFilter2D();
    private DecisionTreeFusion decisionTree = new DecisionTreeFusion();
    private Map<String, Double> trustMap = new HashMap<>();
    private String currentMode = "Hybrid"; // Options: UKF, Tree, Hybrid, Fallback

    @Override
    public void ingest(List<SensorDataRecord> records) {
        if (records == null || records.isEmpty()) return;

        ukf.ingest(records);
        decisionTree.ingest(records);
    }

    @Override
    public double[] getFusedEstimate() {
        double[] ukfEst = ukf.getFusedEstimate();
        double[] treeEst = decisionTree.getFusedEstimate();

        // Default: equal weight, unless overridden
        double alpha = 0.6; // Weight toward UKF
        if ("UKF".equals(currentMode)) return ukfEst;
        if ("Tree".equals(currentMode)) return treeEst;

        // Apply hybrid blend
        double[] fused = new double[ukfEst.length];
        for (int i = 0; i < fused.length; i++) {
            fused[i] = alpha * ukfEst[i] + (1 - alpha) * treeEst[i];
        }

        return fused;
    }

    @Override
    public String getFusionType() {
        return "Hybrid (" + currentMode + ")";
    }

    @Override
    public void reset() {
        ukf = new UnscentedKalmanFilter2D();
        decisionTree = new DecisionTreeFusion();
    }

    @Override
    public void setSensorTrust(Map<String, Double> sensorTrustMap) {
        this.trustMap = sensorTrustMap;
        ukf.setSensorTrust(sensorTrustMap);
        decisionTree.setSensorTrust(sensorTrustMap);
    }

    /**
     * Allows external logic to switch mode (e.g. fallback during GPS loss).
     */
    public void setFusionMode(String mode) {
        this.currentMode = switch (mode) {
            case "UKF", "Tree", "Hybrid", "Fallback" -> mode;
            default -> "Hybrid";
        };
    }
}
