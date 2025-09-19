package sensors;

import sensors.SensorHealthMonitor.Status;
import java.util.Map;
import fusion.SensorFusionEngine;

/**
 * 🔄 SensorFallbackController – Switches fusion engine into degraded-sensor
 * modes when one or more critical sensors become CRITICAL.
 */
public class SensorFallbackController {

    private final SensorHealthMonitor health;
    private final fusion.SensorFusionEngine fusion;

    public SensorFallbackController(SensorHealthMonitor health,
                                    fusion.SensorFusionEngine fusion) {
        this.health = health;
        this.fusion = fusion;
    }

    /** Call once per control-loop tick.  Switches fusion mode as needed. */
    public void heartbeat() {
        health.update();

        Map<String, Status> status = health.getAllStatuses();
        boolean radarDown = status.getOrDefault("Radar",  Status.OK)      == Status.CRITICAL;
        boolean acousticDown = status.getOrDefault("Acoustic", Status.OK) == Status.CRITICAL;

        if (radarDown && acousticDown) {
        	fusion.setFusionMode(SensorFusionEngine.FusionMode.HYBRID);
        } else if (radarDown) {
        	fusion.setFusionMode(SensorFusionEngine.FusionMode.UKF);
        } else {
        	fusion.setFusionMode(SensorFusionEngine.FusionMode.KALMAN);
        }
    }
}
