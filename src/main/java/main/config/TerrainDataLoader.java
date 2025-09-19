package main.config;

import logic.zones.TerrainAwarenessManager;

/**
 * 🗺️ TerrainDataLoader – Thin facade over {@link TerrainAwarenessManager} so
 * components outside the zones package (e.g. EnergyEfficientRouting) can query
 * elevation data without importing zones.* classes directly.
 *
 * Delegates all work to the underlying manager.
 */
public final class TerrainDataLoader {
	private static double[][] simulatedFallback = null;
    public static double[][] getSimulatedFallback() {
        return simulatedFallback;
    }
    
    /** Loads a CSV file via the underlying manager. */
    public void loadCsv(String path) {
        TerrainAwarenessManager.loadFromCsv(path);
    }

    /** Returns elevation in metres (0.0 if unknown). */
    public double getElevation(double lat, double lon) {
        return TerrainAwarenessManager.getElevation(lat, lon);
    }

    /** Returns slope magnitude (rise/run). */
    public double getSlope(double lat, double lon) {
        return TerrainAwarenessManager.getSlope(lat, lon);
    }
    public static double[][] loadOrSimulate(String path, int width, int height) {
        try {
            TerrainAwarenessManager.loadFromCsv(path);
            return null;  // real data loaded into the manager
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load real elevation from " + path + ". Using simulated data.");
            simulatedFallback = generateSimulatedElevation(width, height);
            return simulatedFallback;
        }
  
    }
    public static double[][] generateSimulatedElevation(int width, int height) {
        double[][] elevation = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double base = 50 + 15 * Math.sin(x / 10.0) * Math.cos(y / 10.0);
                double noise = Math.random() * 5;
                elevation[y][x] = base + noise;
            }
        }
        return elevation;
    }

    
}
