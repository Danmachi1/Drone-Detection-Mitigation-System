package utils;

import java.io.*;

/**
 * 🗺️ MapCacheManager – Handles disk-based map tile caching.
 * Supports saving and loading tiles for specific area codes.
 */
public final class MapCacheManager {

    private static final File CACHE_ROOT = new File("cache/maps");

    private MapCacheManager() {}

    /** Clears entire map cache. */
    public static void clear() {
        deleteRecursive(CACHE_ROOT);
    }

    /** Returns total size of cached tiles. */
    public static long sizeBytes() {
        return computeSize(CACHE_ROOT);
    }

    /** Saves current tiles (from runtime cache dir) into area folder. */
    public static void saveMapTiles(String areaCode) {
        File srcDir = new File("runtime/maptiles");  // Assume runtime rendering tiles are stored here
        File destDir = new File(CACHE_ROOT, areaCode);

        if (!srcDir.exists() || !srcDir.isDirectory()) {
            System.err.println("❌ No map tiles found in runtime cache to save.");
            return;
        }

        if (!destDir.exists()) destDir.mkdirs();
        try {
            copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            System.err.println("❌ Error saving map tiles: " + e.getMessage());
        }
    }

    /** Loads cached tiles from areaCode folder into runtime map rendering dir. */
    public static void loadMapTiles(String areaCode) {
        File srcDir = new File(CACHE_ROOT, areaCode);
        File destDir = new File("runtime/maptiles");

        if (!srcDir.exists() || !srcDir.isDirectory()) {
            System.err.println("❌ No cached tiles found for: " + areaCode);
            return;
        }

        if (!destDir.exists()) destDir.mkdirs();
        try {
            copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            System.err.println("❌ Error loading map tiles: " + e.getMessage());
        }
    }

    /* ────────────── Internal Utilities ────────────── */

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

    private static void copyDirectory(File source, File dest) throws IOException {
        if (!source.exists()) return;

        if (source.isDirectory()) {
            if (!dest.exists()) dest.mkdirs();

            for (String file : source.list()) {
                File srcFile = new File(source, file);
                File destFile = new File(dest, file);
                copyDirectory(srcFile, destFile);
            }
        } else {
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(dest)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        }
    }
}
