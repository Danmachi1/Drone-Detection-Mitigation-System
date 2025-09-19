package utils.map;

import java.io.File;

/**
 * 🗺️ MapCacheManager – Handles disk-based map tile caching.
 * This basic implementation assumes all map tiles are stored under
 * a known folder (e.g., ./cache/maps/...) and provides utilities
 * to clear the cache or compute its current size.
 */
public final class MapCacheManager {

    // Change this path if your map tiles are stored elsewhere
    private static final File CACHE_DIR = new File("cache/maps");

    private MapCacheManager() {}

    /** Deletes all cached files in the map cache directory. */
    public static void clear() {
        deleteRecursive(CACHE_DIR);
    }

    /** Computes total size of cached files (in bytes). */
    public static long sizeBytes() {
        return computeSize(CACHE_DIR);
    }

    /* ────────────────── Internal helpers ────────────────── */

    private static void deleteRecursive(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                deleteRecursive(child);
            }
        }
        f.delete();
    }

    private static long computeSize(File f) {
        if (f == null || !f.exists()) return 0;
        if (f.isFile()) return f.length();

        long total = 0;
        for (File child : f.listFiles()) {
            total += computeSize(child);
        }
        return total;
    }
}
