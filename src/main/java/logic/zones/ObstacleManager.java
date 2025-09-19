package logic.zones;

import java.util.ArrayList;
import java.util.List;

/**
 * 🧱 ObstacleManager - Manages real-world and synthetic 3D obstacles for drones.
 * Used in pathfinding, sensor occlusion, AI planning, and visual rendering.
 */
public class ObstacleManager {

    public static class Obstacle {
        public final String id;
        public final double centerX, centerY, height, radius;

        public Obstacle(String id, double x, double y, double height, double radius) {
            this.id = id;
            this.centerX = x;
            this.centerY = y;
            this.height = height;
            this.radius = radius;
        }

        /**
         * Checks if a point (x, y, z) is inside this obstacle.
         */
        public boolean contains(double x, double y, double z) {
            double distXY = Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2));
            return distXY < radius && z < height;
        }

        /**
         * Checks whether a 2D drone path intersects the obstacle base.
         */
        public boolean intersects(double[] from, double[] to) {
            double dx = to[0] - from[0];
            double dy = to[1] - from[1];
            double fx = from[0] - centerX;
            double fy = from[1] - centerY;

            double a = dx * dx + dy * dy;
            double b = 2 * (fx * dx + fy * dy);
            double c = fx * fx + fy * fy - radius * radius;

            double discriminant = b * b - 4 * a * c;
            return discriminant >= 0;
        }
    }

    private static final List<Obstacle> obstacles = new ArrayList<>();

    public static void addObstacle(String id, double x, double y, double height, double radius) {
        obstacles.add(new Obstacle(id, x, y, height, radius));
    }

    public static void clearAll() {
        obstacles.clear();
    }

    public static boolean isBlocked(double x, double y, double z) {
        return obstacles.stream().anyMatch(o -> o.contains(x, y, z));
    }

    public static boolean pathBlocked(double[] from, double[] to) {
        return obstacles.stream().anyMatch(o -> o.intersects(from, to));
    }

    public static List<Obstacle> getAllObstacles() {
        return new ArrayList<>(obstacles);
    }

    public static int count() {
        return obstacles.size();
    }
}
