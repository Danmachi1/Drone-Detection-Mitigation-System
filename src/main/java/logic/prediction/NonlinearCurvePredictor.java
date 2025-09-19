package logic.prediction;

import java.util.LinkedList;
import java.util.List;

/**
 * 🌀 NonlinearCurvePredictor - Alternate nonlinear path predictor using smoothing curve logic.
 * Works when Bezier or LSTM fallback is unavailable.
 */
public class NonlinearCurvePredictor {

    private static final int MAX_HISTORY = 10;
    private final LinkedList<double[]> positionHistory = new LinkedList<>();

    /**
     * Feeds in a new position point.
     */
    public void addPosition(double[] pos) {
        if (pos == null || pos.length != 2) return;
        positionHistory.add(pos.clone());
        if (positionHistory.size() > MAX_HISTORY) {
            positionHistory.removeFirst();
        }
    }

    /**
     * Predicts the next point using average curvature direction.
     */
    public double[] predictNext() {
        int size = positionHistory.size();
        if (size < 3) return null;

        double[] last = positionHistory.get(size - 1);
        double[] prev = positionHistory.get(size - 2);
        double[] prePrev = positionHistory.get(size - 3);

        double dx1 = prev[0] - prePrev[0];
        double dy1 = prev[1] - prePrev[1];
        double dx2 = last[0] - prev[0];
        double dy2 = last[1] - prev[1];

        double avgDx = (dx1 + dx2) / 2;
        double avgDy = (dy1 + dy2) / 2;

        return new double[]{last[0] + avgDx, last[1] + avgDy};
    }

    /**
     * Clears the stored history.
     */
    public void reset() {
        positionHistory.clear();
    }
}
