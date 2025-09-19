package utils.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import javax.imageio.ImageIO;

/**
 * 🌍 MapManager – Downloads, caches, and manages OpenStreetMap tiles for offline use.
 * Also manages active map mode (Live, Cached, Offline, etc.) for UI and logic.
 */
public final class MapManager {

    private static final String TILE_URL = "https://tile.openstreetmap.org/%d/%d/%d.png";
    private static final File CACHE_DIR = new File("cache/maps");

    public enum MapMode {
        LIVE_GOOGLE,
        LIVE_OSM,
        CACHED,
        OFFLINE,
        CUSTOM
    }

    private static MapMode currentMode = MapMode.LIVE_GOOGLE;

    private MapManager() {}

    /* ───────────────────────────────────────
     * 🧭 Map Mode Management (UI & Backend)
     * ─────────────────────────────────────── */

    /** Returns current map mode (e.g., "Live-Google") for display. */
    public static String getCurrentMode() {
        return switch (currentMode) {
            case LIVE_GOOGLE -> "Live-Google";
            case LIVE_OSM    -> "Live-OSM";
            case CACHED      -> "Cached";
            case OFFLINE     -> "Offline";
            case CUSTOM      -> "Custom";
        };
    }

    /** Updates internal map mode from UI string (case-insensitive). */
    public static void setMapMode(String modeStr) {
        switch (modeStr.toUpperCase()) {
            case "LIVE-GOOGLE" -> currentMode = MapMode.LIVE_GOOGLE;
            case "LIVE-OSM"    -> currentMode = MapMode.LIVE_OSM;
            case "CACHED"      -> currentMode = MapMode.CACHED;
            case "OFFLINE"     -> currentMode = MapMode.OFFLINE;
            case "CUSTOM"      -> currentMode = MapMode.CUSTOM;
            default -> System.err.println("⚠️ Unknown map mode: " + modeStr);
        }
        System.out.println("🧭 Map mode set to: " + getCurrentMode());
    }

    /** Internal access to raw enum (for render engine etc.) */
    public static MapMode getCurrentMapMode() {
        return currentMode;
    }

    /* ───────────────────────────────────────
     * 📦 Tile Caching and Downloading
     * ─────────────────────────────────────── */

    /** Converts longitude to tile X index. */
    private static int lonToTileX(double lon, int zoom) {
        return (int) Math.floor((lon + 180.0) / 360.0 * (1 << zoom));
    }

    /** Converts latitude to tile Y index. */
    private static int latToTileY(double lat, int zoom) {
        double latRad = Math.toRadians(lat);
        return (int) Math.floor((1.0 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI) / 2.0 * (1 << zoom));
    }

    /**
     * Downloads tiles in bounding box at zoom level 16 and caches locally.
     */
    public static boolean cacheArea(double lat1, double lon1, double lat2, double lon2) {
        int zoom = 16;

        int x1 = lonToTileX(Math.min(lon1, lon2), zoom);
        int x2 = lonToTileX(Math.max(lon1, lon2), zoom);
        int y1 = latToTileY(Math.max(lat1, lat2), zoom);
        int y2 = latToTileY(Math.min(lat1, lat2), zoom);

        try {
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    File tileFile = new File(CACHE_DIR, String.format(Locale.US, "%d/%d/%d.png", zoom, x, y));
                    if (!tileFile.exists()) {
                        tileFile.getParentFile().mkdirs();
                        URL url = new URL(String.format(TILE_URL, zoom, x, y));
                        BufferedImage img = ImageIO.read(url);
                        ImageIO.write(img, "png", tileFile);
                        System.out.printf("✅ Downloaded tile %d/%d/%d\n", zoom, x, y);
                    } else {
                        System.out.printf("🗂 Cached tile %d/%d/%d already exists.\n", zoom, x, y);
                    }
                }
            }
            return true;

        } catch (IOException e) {
            System.err.println("❌ MapManager: Caching failed - " + e.getMessage());
            return false;
        }
    }

    /** Computes size of cached tiles (in bytes). */
    public static long sizeBytes() {
        return getDirSize(CACHE_DIR);
    }

    /** Clears all cached tiles from disk. */
    public static void clear() {
        deleteRecursive(CACHE_DIR);
        CACHE_DIR.mkdirs();
    }

    private static long getDirSize(File dir) {
        if (dir == null || !dir.exists()) return 0;
        long size = 0;
        for (File file : dir.listFiles()) {
            size += file.isDirectory() ? getDirSize(file) : file.length();
        }
        return size;
    }

    private static void deleteRecursive(File dir) {
        if (dir == null || !dir.exists()) return;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) deleteRecursive(file);
            else file.delete();
        }
        dir.delete();
    }
}
