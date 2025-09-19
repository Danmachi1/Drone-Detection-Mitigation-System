package fusion;

import main.config.*;
import fusion.plugins.ExtendedKalmanFilter2D;
import fusion.plugins.FallbackFusionPlugin;
import fusion.plugins.HybridFusionPlugin;
import fusion.plugins.KalmanFilter2D;
import fusion.plugins.MultiLayerFusionController;
import fusion.plugins.RuleBasedFusionPlugin;
import fusion.plugins.UnscentedKalmanFilter2D;

/**
 * 🏭 FusionFactory - Centralized factory for producing fusion plugins
 * based on mode selection and configuration settings.
 */
public class FusionFactory {

    public static FusionPlugin createFusionPlugin() {
        String mode = Config.getString("fusion.mode").toLowerCase();

        switch (mode) {
            case "kalman":
                return new KalmanFilter2D();
            case "ekf":
                return new ExtendedKalmanFilter2D();
            case "ukf":
                return new UnscentedKalmanFilter2D();
            case "hybrid":
                return new HybridFusionPlugin();
            case "rules":
                return new RuleBasedFusionPlugin();
            case "multi":
                return new MultiLayerFusionController();
            case "fallback":
                return new FallbackFusionPlugin();
            default:
                System.err.println("⚠️ Unknown fusion mode: " + mode + ". Using fallback plugin.");
                return new FallbackFusionPlugin();
        }
    }

    /**
     * Allows creating a plugin directly by type string
     * (used in runtime plugin reloads or diagnostics).
     */
    public static FusionPlugin createFusionPluginByType(String type) {
        if (type == null) return new FallbackFusionPlugin();
        switch (type.toLowerCase()) {
        case "kalman":
            return new KalmanFilter2D();
        case "ekf":
            return new ExtendedKalmanFilter2D();
        case "ukf":
            return new UnscentedKalmanFilter2D();
        case "hybrid":
            return new HybridFusionPlugin();
        case "rules":
            return new RuleBasedFusionPlugin();
        case "multi":
            return new MultiLayerFusionController();
        case "fallback":
            return new FallbackFusionPlugin();
        default:
            System.err.println("⚠️ Unknown plugin type: " + type + ". Using fallback plugin.");
            return new FallbackFusionPlugin();
    }
}
}
