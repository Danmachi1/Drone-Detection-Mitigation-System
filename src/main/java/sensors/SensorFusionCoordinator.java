package sensors;

import fusion.FusionPlugin;
import fusion.SensorFusionEngine;
import sensors.SensorPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 🔗 SensorFusionCoordinator – orchestrates the end-to-end data flow:
 *
 *   • polls all {@link SensorPlugin}s
 *   • pushes fresh records into the {@link SensorFusionEngine}
 *   • exposes the latest fused state
 *
 * Meant to be called once per control-loop tick (e.g. 30 Hz).
 */
public class SensorFusionCoordinator {

    private final MultiSensorManager     sensorMgr;
    private final SensorFusionEngine     fusion;
    private final List<FusionPlugin>     extraPlugins = new ArrayList<>();

    public SensorFusionCoordinator(MultiSensorManager mgr,
                                   SensorFusionEngine engine) {
        this.sensorMgr = mgr;
        this.fusion    = engine;
    }

    /** Optionally add post-fusion analysis plugins (Kalman smoothing, etc.). */
    public void addPlugin(FusionPlugin plugin) { extraPlugins.add(plugin); }

    /** Main tick – poll sensors, feed fusion, and return fused state. */
    public double[] update() {

        /* 1 ─ Gather fresh sensor rows */
        var batch = sensorMgr.pollAll();
        if (!batch.isEmpty()) {
            fusion.fuse(batch);         // update engine
        }

        /* 2 ─ Forward to any extra plugins */
        double[] est = fusion.getLastEstimate();
        if (est != null) {
            for (FusionPlugin p : extraPlugins) {
                p.ingest(null);         // ingest can be no-op or use shared buffer
            }
        }
        return est;
    }

    /** Convenience getter for external dashboards. */
    public double[] getLastEstimate() { return fusion.getLastEstimate(); }
}
