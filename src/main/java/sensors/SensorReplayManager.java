package sensors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 📼 SensorReplayManager – Replays a recorded CSV log of SensorDataRecord lines
 * for offline testing.  Format must match fields in {@link SensorDataRecord}.
 *
 * CSV columns:
 *   ts,x,y,vx,vy,alt,heading,source
 */
public class SensorReplayManager {

    private final List<SensorDataRecord> cache = new ArrayList<>();
    private int cursor = 0;

    public void loadCsv(String path) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                cache.add(new SensorDataRecord(
                        Long.parseLong(p[0].trim()),
                        Double.parseDouble(p[1].trim()),
                        Double.parseDouble(p[2].trim()),
                        Double.parseDouble(p[3].trim()),
                        Double.parseDouble(p[4].trim()),
                        Double.parseDouble(p[5].trim()),
                        Double.parseDouble(p[6].trim()),
                        p.length > 7 ? p[7].trim() : "Replay"));
            }
            System.out.println("📼 Loaded " + cache.size() + " sensor rows from " + path);
        }
    }

    /** Returns the next record (null if EOF). */
    public SensorDataRecord next() {
        return cursor < cache.size() ? cache.get(cursor++) : null;
    }

    public boolean hasMore() { return cursor < cache.size(); }

    public void reset() { cursor = 0; }
}
