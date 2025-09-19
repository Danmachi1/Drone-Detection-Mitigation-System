package logic.acoustic;

import java.util.*;

/**
 * 📡 SoundLocalizationEngine - Estimates the location of a sound source using TDOA-based sensor triangulation.
 * Enables passive detection of drones using acoustic signatures.
 */
public class SoundLocalizationEngine {

    public static class MicData {
        public final double x, y;      // Mic position
        public final long timestampNs; // When sound arrived at this mic

        public MicData(double x, double y, long timeNs) {
            this.x = x;
            this.y = y;
            this.timestampNs = timeNs;
        }
    }

    private final double soundSpeed = 343.0; // m/s in air
    private final double sensorErrorNs = 1000000; // ~1ms error margin

    /**
     * Estimates the sound source location using 3+ microphone inputs.
     */
    public Optional<double[]> localize(List<MicData> data) {
        if (data.size() < 3) return Optional.empty();

        MicData ref = data.get(0);
        List<double[]> equations = new ArrayList<>();

        for (int i = 1; i < data.size(); i++) {
            MicData m = data.get(i);
            double dx = m.x - ref.x;
            double dy = m.y - ref.y;

            long dtNs = m.timestampNs - ref.timestampNs;
            double dtSec = dtNs / 1e9;
            double distanceDiff = dtSec * soundSpeed;

            // Equation: (x - xi)^2 + (y - yi)^2 - (x - x0)^2 - (y - y0)^2 = (di)^2 - (d0)^2
            equations.add(new double[]{dx, dy, distanceDiff});
        }

        // Simplified estimation using center of intersection
        double estX = ref.x;
        double estY = ref.y;

        for (double[] eq : equations) {
            estX += eq[0] * eq[2];
            estY += eq[1] * eq[2];
        }

        estX /= equations.size();
        estY /= equations.size();

        return Optional.of(new double[]{estX, estY});
    }
}
