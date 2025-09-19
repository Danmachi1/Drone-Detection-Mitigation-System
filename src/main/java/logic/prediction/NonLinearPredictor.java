package logic.prediction;

import java.util.LinkedList;
import java.util.List;

/**
 * 🧠 NonLinearPredictor - Predicts future target path using curved trajectory logic.
 * Applies quadratic Bezier modeling using recent velocity vectors.
 */
public class NonLinearPredictor {

    private static final int MAX_HISTORY = 10;
    private final LinkedList<double[]> positionHistory = new LinkedList<>();

    /**
     * Adds a new position to the trajectory history.
     */
    public void addPosition(double[] pos) {
        if (pos == null || pos.length != 2) return;
        positionHistory.add(pos.clone());
        if (positionHistory.size() > MAX_HISTORY) {
            positionHistory.removeFirst();
        }
    }

    /**
     * Predicts future positions (x, y) with nonlinear path logic.
     */
    public List<double[]> predictFuturePath(int steps) {
        List<double[]> future = new LinkedList<>();
        if (positionHistory.size() < 3) return future;

        double[] p0 = positionHistory.get(positionHistory.size() - 3);
        double[] p1 = positionHistory.get(positionHistory.size() - 2);
        double[] p2 = positionHistory.get(positionHistory.size() - 1);

        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            double[] pred = bezierQuadratic(p0, p1, p2, t);
            future.add(pred);
        }

        return future;
    }

    /**
     * Predicts next immediate position for rapid fusion/logic.
     */
    public double[] predictNext() {
        List<double[]> preds = predictFuturePath(1);
        return preds.isEmpty() ? null : preds.get(0);
    }

    /**
     * Quadratic Bezier function.
     */
    private double[] bezierQuadratic(double[] p0, double[] p1, double[] p2, double t) {
        double x = Math.pow(1 - t, 2) * p0[0] +
                   2 * (1 - t) * t * p1[0] +
                   Math.pow(t, 2) * p2[0];
        double y = Math.pow(1 - t, 2) * p0[1] +
                   2 * (1 - t) * t * p1[1] +
                   Math.pow(t, 2) * p2[1];
        return new double[]{x, y};
    }

    /**
     * Clears the history buffer.
     */
    public void reset() {
        positionHistory.clear();
    }
}
