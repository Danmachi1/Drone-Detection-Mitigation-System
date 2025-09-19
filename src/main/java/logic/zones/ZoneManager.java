package logic.zones;

import main.config.Config;
import utils.MathUtils;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.stream.Collectors;

/**
 * 🗺️ ZoneManager - Manages static and dynamic zones (no-fly, asset zones, etc.).
 * Supports threat reasoning, drone planning, and UI visualization.
 */
public class ZoneManager {

    private static final List<Zone> noFlyZones = new ArrayList<>();
    private static final List<Zone> assetZones = new ArrayList<>();

    /**
     * Loads zones from the config (e.g. defined in JSON).
     */	
    public static void initializeZones() {
        noFlyZones.clear();
        assetZones.clear();

        List<Zone> nfz = Config.getZoneList("zones.noFly");
        for (int i = 0; i < nfz.size(); i++) {
            Zone z = nfz.get(i);
            noFlyZones.add(new Zone("NFZ_" + i, z.getCenterX(), z.getCenterY(), z.getRadius(), z.getPolygon()));
        }

        List<Zone> assets = Config.getZoneList("zones.assets");
        for (int i = 0; i < assets.size(); i++) {
            Zone z = assets.get(i);
            assetZones.add(new Zone("ASSET_" + i, z.getCenterX(), z.getCenterY(), z.getRadius(), z.getPolygon()));
        }
    }

    public static boolean isInsideNoFly(double[] pos) {
        return noFlyZones.stream().anyMatch(z -> z.contains(pos));
    }

    public static boolean isInsideRestricted(double[] pos) {
        return isInsideNoFly(pos); // Can be extended with custom logic
    }

    public static boolean isNearAsset(double[] pos) {
        return assetZones.stream().anyMatch(z -> z.isNear(pos, 20.0));
    }

    public static boolean isHeadingToNoFly(double[] pos, double[] vel) {
        return noFlyZones.stream().anyMatch(z -> z.isHeadingToward(pos, vel));
    }

    public static boolean isHeadingToAsset(double[] pos, double[] vel) {
        return assetZones.stream().anyMatch(z -> z.isHeadingToward(pos, vel));
    }

    public static List<Zone> getAllZones() {
        List<Zone> all = new ArrayList<>();
        all.addAll(noFlyZones);
        all.addAll(assetZones);
        return all;
    }


    /**
     * Returns names of all cached zones (from disk).
     */
    public static List<String> getAvailableZoneNames() {
        File folder = new File("cache/zones");
        if (!folder.exists()) return new ArrayList<>();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".zone"));
        if (files == null) return new ArrayList<>();
        return List.of(files).stream()
                .map(f -> f.getName().replace(".zone", ""))
                .collect(Collectors.toList());
    }

    /**
     * Defines and saves a new empty zone (for now a placeholder).
     */
    public static boolean defineNewZone(String name) {
        try {
            File dir = new File("cache/zones");
            dir.mkdirs();
            File f = new File(dir, name + ".zone");
            try (PrintWriter pw = new PrintWriter(f)) {
                pw.println(name + ",0,0,100"); // ID, cx, cy, radius
            }
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to define zone: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a zone from disk and adds it to asset zones.
     */
    public static boolean loadZone(String name) {
        File f = new File("cache/zones/" + name + ".zone");
        if (!f.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            String[] parts = line.split(",");
            String id = parts[0];
            double cx = Double.parseDouble(parts[1]);
            double cy = Double.parseDouble(parts[2]);
            double r = Double.parseDouble(parts[3]);
            Zone z = new Zone(id, cx, cy, r, new ArrayList<>());
            assetZones.add(z);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to load zone: " + e.getMessage());
            return false;
        }
    }

}
