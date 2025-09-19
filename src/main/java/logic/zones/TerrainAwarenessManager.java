package logic.zones;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * ⛰️ TerrainAwarenessManager − Keeps an in-memory elevation grid so AI, routing,
 * and threat logic can query height or slope cheaply.
 *
 * Coordinates are stored as latitude/longitude with 4-decimal precision
 * (~11 m resolution).  Data can be loaded at start-up from a CSV:
 *      lat,lon,elevationMeters
 *
 * Public API
 * ─────────────────────────────────────────
 *  • loadFromCsv(path)             – bulk load
 *  • setElevation(lat,lon,meters)  – ad-hoc
 *  • getElevation(lat,lon)         – query (meters)
 *  • getSlope(lat,lon)             – ∂elevation / ∂distance  (m / m)
 *  • clear()                       – wipe grid
 */
public final class TerrainAwarenessManager {

    /** Internal grid – key = "lat_lon" rounded to 4 dp. */
    private static final Map<String, Double> elevationGrid = new HashMap<>();

    /* ════════════════════════════════════════════════════════════════
       1.  PUBLIC LOAD / SETTERS
       ════════════════════════════════════════════════════════════════ */

    public static void loadFromCsv(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        	// skip header
        	br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;
                double lat = Double.parseDouble(parts[0].trim());
                double lon = Double.parseDouble(parts[1].trim());
                double elev = Double.parseDouble(parts[2].trim());
                setElevation(lat, lon, elev);
            }
            System.out.println("⛰️  Terrain grid loaded from " + path +
                               " – " + elevationGrid.size() + " points.");
        } catch (Exception ex) {
            System.err.println("⚠️  Failed to load terrain CSV: " + ex.getMessage());
        }
    }

    public static void setElevation(double lat, double lon, double meters) {
        elevationGrid.put(makeKey(lat, lon), meters);
    }

    public static void clear() {
        elevationGrid.clear();
    }

    /* ════════════════════════════════════════════════════════════════
       2.  QUERY METHODS
       ════════════════════════════════════════════════════════════════ */

    /** Returns elevation in metres; 0.0 if unknown. */
    public static double getElevation(double lat, double lon) {
        return elevationGrid.getOrDefault(makeKey(lat, lon), 0.0);
    }

    /**
     * Returns slope magnitude (rise/run) using a small 0.0005° probe (~55 m).
     * If neighbouring point missing, slope = 0.
     */
    public static double getSlope(double lat, double lon) {
        double base   = getElevation(lat, lon);
        double probeD = 0.0005;                       // ≈55 m at equator
        double elevN  = getElevation(lat + probeD, lon);
        double elevE  = getElevation(lat, lon + probeD);

        double rise   = Math.max(Math.abs(elevN - base), Math.abs(elevE - base));
        double run    = probeD * 111_320;             // metres per deg

        return run == 0 ? 0 : rise / run;
    }

    /* ════════════════════════════════════════════════════════════════
       3.  PRIVATE  HELPERS
       ════════════════════════════════════════════════════════════════ */

    /** Rounded key to avoid floating-point map explosion. */
    private static String makeKey(double lat, double lon) {
        return String.format("%.4f_%.4f", lat, lon);
    }
}
