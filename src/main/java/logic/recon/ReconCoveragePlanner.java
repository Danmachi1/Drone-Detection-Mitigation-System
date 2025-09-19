package logic.recon;

import logic.zones.ZoneManager;
import logic.zones.TerrainAwarenessManager;

import java.util.*;

/**
 * 🛰️ ReconCoveragePlanner - Plans a full-area sweep using multiple recon drones.
 * Splits region into tiles, assigns routes, avoids no-fly zones and steep terrain.
 */
public class ReconCoveragePlanner {

    public static class Tile {
        public final double lat, lon;
        public boolean assigned = false;

        public Tile(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    private final List<Tile> gridTiles = new ArrayList<>();

    /**
     * Generates the tile grid within a bounding box.
     */
    public void generateTileGrid(double latMin, double lonMin, double latMax, double lonMax, double spacingMeters) {
        gridTiles.clear();

        for (double lat = latMin; lat <= latMax; lat += 0.0005) {
            for (double lon = lonMin; lon <= lonMax; lon += 0.0005) {
                double elev = TerrainAwarenessManager.getElevation(lat, lon);
                boolean blocked = ZoneManager.isInsideNoFly(new double[]{lon, lat});
                if (!blocked && elev < 250) {
                    gridTiles.add(new Tile(lat, lon));
                }
            }
        }

        System.out.println("🧭 ReconPlanner: Generated " + gridTiles.size() + " safe tiles.");
    }

    /**
     * Assigns tiles to available drone IDs round-robin.
     */
    public Map<String, List<Tile>> assignTilesToDrones(List<String> droneIds) {
        Map<String, List<Tile>> plan = new HashMap<>();
        if (droneIds.isEmpty()) return plan;

        for (String id : droneIds) {
            plan.put(id, new ArrayList<>());
        }

        int index = 0;
        for (Tile tile : gridTiles) {
            if (!tile.assigned) {
                String assignedTo = droneIds.get(index % droneIds.size());
                plan.get(assignedTo).add(tile);
                tile.assigned = true;
                index++;
            }
        }

        return plan;
    }

    /**
     * Clears previous grid/tile plan.
     */
    public void reset() {
        gridTiles.clear();
    }
}
