package sensors.plugins;

import sensors.SensorDataRecord;
import sensors.SensorPlugin;
import sensors.SensorReplayManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplaySensorPlugin implements SensorPlugin {
    private SensorDataRecord lastRecord = null;
    private final SensorReplayManager replay = new SensorReplayManager();
    private final String csvPath;

    private long lastUpdate = 0;
    private boolean loaded  = false;

    public ReplaySensorPlugin(String csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<SensorDataRecord> poll() {
        if (!loaded) loadCsv();

        List<SensorDataRecord> list = new ArrayList<>();
        if (replay.hasMore()) {
            SensorDataRecord rec = replay.next();
            if (rec != null) {
                lastUpdate = rec.timestamp;
                lastRecord = rec;  // ✅ store for reuse
                list.add(rec);
            }
        }
        return list;
    }

    @Override public boolean isSimulationMode()        { return true; }
    @Override public void    setSimulationMode(boolean sim) { /* always sim */ }
    @Override public long    getLastUpdateTime()       { return lastUpdate; }

    private void loadCsv() {
        try {
            replay.loadCsv(csvPath);
            loaded = true;
            System.out.println("📼 ReplaySensorPlugin loaded " + csvPath);
        } catch (Exception ex) {
            System.err.println("⚠️  ReplaySensorPlugin failed to load CSV: " + ex.getMessage());
            loaded = true;
        }
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
            drones.put("Replay-Drone", new double[]{lastRecord.x, lastRecord.y});
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
        return "ReplaySensorPlugin";
    }

}
