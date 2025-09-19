package logic.prediction;

import java.util.ArrayList;
import java.util.List;

/**
 * 🔮 PathPredictor - Predicts future trajectory based on recent positions.
 * Supports curvature detection and motion classification.
 */
public class PathPredictor {

    public enum TrajectoryType {
        STRAIGHT, CIRCULAR, ZIGZAG, DECELERATING, UNCLASSIFIED
    }

    private final List<double[]> recentPositions = new ArrayList<>();
    private final int historySize = 5;
    private final double curvatureThreshold = 0.2;

    /**
     * Updates the position history and returns the predicted next location.
     * @param x current X position
     * @param y current Y position
     * @return predicted [x, y] location
     */
    public double[] predict(double x, double y) {
        recentPositions.add(new double[]{x, y});
        if (recentPositions.size() > historySize) {
            recentPositions.remove(0);
        }

        if (recentPositions.size() < 3) {
            return new double[]{x, y}; // Not enough data
        }

        // Compute average direction
        double[] last = recentPositions.get(recentPositions.size() - 1);
        double[] prev = recentPositions.get(recentPositions.size() - 2);

        double dx = last[0] - prev[0];
        double dy = last[1] - prev[1];

        // Apply a basic projection
        double futureX = last[0] + dx;
        double futureY = last[1] + dy;

        return new double[]{futureX, futureY};
    }

    /**
     * Classifies the trajectory shape based on path curvature.
     */
    public TrajectoryType classify() {
        if (recentPositions.size() < 4) return TrajectoryType.UNCLASSIFIED;

        double totalAngleChange = 0.0;

        for (int i = 2; i < recentPositions.size(); i++) {
            double[] a = recentPositions.get(i - 2);
            double[] b = recentPositions.get(i - 1);
            double[] c = recentPositions.get(i);

            double angle1 = Math.atan2(b[1] - a[1], b[0] - a[0]);
            double angle2 = Math.atan2(c[1] - b[1], c[0] - b[0]);

            double dTheta = Math.abs(angle2 - angle1);
            if (dTheta > Math.PI) dTheta = 2 * Math.PI - dTheta;

            totalAngleChange += dTheta;
        }

        double averageChange = totalAngleChange / (recentPositions.size() - 2);

        if (averageChange < 0.1) return TrajectoryType.STRAIGHT;
        if (averageChange > 0.5) return TrajectoryType.CIRCULAR;
        if (averageChange >= 0.1 && averageChange <= 0.5) return TrajectoryType.ZIGZAG;

        return TrajectoryType.UNCLASSIFIED;
    }
    /**
     * Clears the current path history.
     */
    public void reset() {
        recentPositions.clear();
    }
}
