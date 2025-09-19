package logic.mapping;

import logic.zones.Zone;
import logic.zones.ZoneManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 🌐 MapTileManager – Downloads and caches OpenStreetMap tiles
 * for offline use. Supports bounding box caching and zone overlays.
 */
public class MapTileManager {

    private static final String TILE_URL = "https://tile.openstreetmap.org/%d/%d/%d.png";
    private static final File TILE_CACHE_DIR = new File("cache/maps");
    private static final int ZOOM = 16;

    private static Canvas mapCanvas = null;

    private MapTileManager() {}

    // 🔌 Optional: Hook up UI canvas for drawing overlays
    public static void setCanvas(Canvas canvas) {
        mapCanvas = canvas;
    }

    public static void renderZone(String zoneName) {
        System.out.println("🗺 Rendering zone: " + zoneName);
        Zone z = ZoneManager.getAllZones().stream()
                .filter(zn -> zn.getId().equals(zoneName))
                .findFirst().orElse(null);

        if (z == null) {
            System.out.println("❌ Zone not found.");
            return;
        }

        double cx = z.getCenterX();
        double cy = z.getCenterY();
        double radius = z.getRadius();
        System.out.printf("📍 Zone Center=(%.1f, %.1f), Radius=%.1f\n", cx, cy, radius);

        // Cache bounding tiles
        double latMin = cy - radius / 10000;
        double latMax = cy + radius / 10000;
        double lonMin = cx - radius / 10000;
        double lonMax = cx + radius / 10000;
        cacheRegion(latMin, latMax, lonMin, lonMax);

        // Draw on canvas if attached
        if (mapCanvas != null) {
            GraphicsContext gc = mapCanvas.getGraphicsContext2D();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2.0);
            gc.strokeOval(cx - radius, cy - radius, radius * 2, radius * 2);
            gc.strokeText("Zone: " + zoneName, cx - radius, cy - radius - 5);
        }
    }

    public static boolean cacheRegion(double latMin, double latMax, double lonMin, double lonMax) {
        int xStart = lonToTileX(lonMin, ZOOM);
        int xEnd   = lonToTileX(lonMax, ZOOM);
        int yStart = latToTileY(latMax, ZOOM);
        int yEnd   = latToTileY(latMin, ZOOM);

        try {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    File tileFile = getTileFile(ZOOM, x, y);
                    if (!tileFile.exists()) {
                        tileFile.getParentFile().mkdirs();
                        URL url = new URL(String.format(TILE_URL, ZOOM, x, y));
                        BufferedImage img = ImageIO.read(url);
                        ImageIO.write(img, "png", tileFile);
                        System.out.printf("✅ Cached tile z=%d x=%d y=%d\n", ZOOM, x, y);
                    } else {
                        System.out.printf("🗂 Tile already cached: z=%d x=%d y=%d\n", ZOOM, x, y);
                    }
                }
            }
            return true;

        } catch (IOException e) {
            System.err.println("❌ Tile caching failed: " + e.getMessage());
            return false;
        }
    }

    public static void clearCache() {
        deleteRecursive(TILE_CACHE_DIR);
        TILE_CACHE_DIR.mkdirs();
        System.out.println("🗑 Tile cache cleared.");
    }

    private static File getTileFile(int zoom, int x, int y) {
        return new File(TILE_CACHE_DIR, String.format(Locale.US, "%d/%d/%d.png", zoom, x, y));
    }

    private static int lonToTileX(double lon, int zoom) {
        return (int) Math.floor((lon + 180.0) / 360.0 * (1 << zoom));
    }

    private static int latToTileY(double lat, int zoom) {
        double latRad = Math.toRadians(lat);
        return (int) Math.floor((1.0 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI) / 2.0 * (1 << zoom));
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
