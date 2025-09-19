package main.config;

import logic.zones.Zone;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 🚫 NoFlyZoneManager – Loads no-fly‐zone definitions from a CSV or JSON file at
 * start-up and registers them in {@link Config} so any component can query
 * <code>Config.getZoneList("nofly")</code>.
 *
 * CSV format (simple or with polygon):
 *      #id,centerX,centerY,radiusMeters[,lat1,lon1;lat2,lon2;...]
 */
public final class NoFlyZoneManager {

    /** Loads NFZ data from a CSV file and registers with Config. */
    public static void loadFromCsv(String path) {
        List<Zone> zones = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        	// skip header
        	br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String id     = parts[0].trim();
                double cx     = Double.parseDouble(parts[1].trim());
                double cy     = Double.parseDouble(parts[2].trim());
                double radius = Double.parseDouble(parts[3].trim());

                List<double[]> polygon = null;
                if (parts.length > 4) {
                    polygon = new ArrayList<>();
                    String[] points = parts[4].trim().split(";");
                    for (String point : points) {
                        String[] latlon = point.trim().split(",");
                        if (latlon.length == 2) {
                            double lat = Double.parseDouble(latlon[0]);
                            double lon = Double.parseDouble(latlon[1]);
                            polygon.add(new double[]{lat, lon});
                        }
                    }
                }

                zones.add(new Zone(id, cx, cy, radius, polygon));
            }
            System.out.println("🚫 Loaded " + zones.size() + " no-fly zones from " + path);
        } catch (Exception ex) {
            System.err.println("⚠️  Failed to load NFZ CSV: " + ex.getMessage());
        }

        /* Register with global Config so ZoneManager can access. */
        Config.registerZoneList("nofly", zones);
    }

    private NoFlyZoneManager() { /* util class */ }
}
