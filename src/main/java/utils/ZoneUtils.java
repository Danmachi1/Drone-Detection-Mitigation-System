package utils;

import javafx.geometry.Point2D;

/**
 * 🗺️ ZoneUtils – Converts real-world ENU zone coordinates to canvas X/Y pixels.
 * Uses dynamic scaling based on config or provided map bounds.
 */
public class ZoneUtils {

    // Define the ENU map bounds (meters) – override these with real config later
    private static final double MIN_X = -1000;  // west
    private static final double MAX_X = 1000;   // east
    private static final double MIN_Y = -1000;  // south
    private static final double MAX_Y = 1000;   // north
    /**
     * Converts lat/lon into normalized X/Y based on canvas size.
     * This is placeholder logic — you should replace it with proper projection if needed.
     */
    public static double[] mapLatLonToXY(double lat, double lon, double width, double height) {
        // Assuming lat: [-90, 90], lon: [-180, 180]
        double x = (lon + 180.0) / 360.0 * width;
        double y = (90.0 - lat) / 180.0 * height;
        return new double[]{x, y};
    }
    /**
     * Maps ENU coordinates (x, y) to canvas pixel positions.
     * Assumes (MIN_X, MAX_Y) maps to (0, 0) and (MAX_X, MIN_Y) maps to (width, height)
     */
    public static double[] mapXYToCanvas(double x, double y, double width, double height) {
        double xNorm = (x - MIN_X) / (MAX_X - MIN_X); // 0 to 1
        double yNorm = (MAX_Y - y) / (MAX_Y - MIN_Y); // invert Y axis

        double px = xNorm * width;
        double py = yNorm * height;
        return new double[]{px, py};
    }

    /**
     * Overload for ENU positions as array.
     */
    public static double[] mapXYToCanvas(double[] pos, double width, double height) {
        return mapXYToCanvas(pos[0], pos[1], width, height);
    }

    /**
     * Optionally: convert back from canvas pixels to ENU meters
     */
    public static double[] mapCanvasToXY(double px, double py, double width, double height) {
        double x = MIN_X + (px / width) * (MAX_X - MIN_X);
        double y = MAX_Y - (py / height) * (MAX_Y - MIN_Y);
        return new double[]{x, y};
        
    }
}
