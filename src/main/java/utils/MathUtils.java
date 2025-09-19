package utils;

/**
 * 📐 MathUtils – Lightweight numeric helper functions used across SkyShield.
 * All methods are static and side-effect free.
 */
public final class MathUtils {

    private MathUtils() { /* util – no instances */ }

    /* ── Basics ───────────────────────────────────────────────── */

    public static double clamp(double v, double min, double max) {
        return v < min ? min : (v > max ? max : v);
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * clamp(t, 0, 1);
    }

    /* ── Vector helpers (2-D) ────────────────────────────────── */

    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    public static double magnitude(double[] v) {
        return Math.hypot(v[0], v[1]);
    }

    /** Returns a *new* normalised vector (length = 1). */
    public static double[] normalize(double[] v) {
        double mag = magnitude(v);
        return mag == 0 ? new double[]{0,0}
                        : new double[]{ v[0]/mag, v[1]/mag };
    }

    public static double dot(double[] a, double[] b) {
        return a[0]*b[0] + a[1]*b[1];
    }

    public static double[] add(double[] a, double[] b) {
        return new double[]{ a[0]+b[0], a[1]+b[1] };
    }

    public static double[] scale(double[] v, double s) {
        return new double[]{ v[0]*s, v[1]*s };
    }

    /* ── Angle helpers ───────────────────────────────────────── */

    public static double degToRad(double deg) { return Math.toRadians(deg); }
    public static double radToDeg(double rad) { return Math.toDegrees(rad); }

    /** Wrap angle to [-π, π]. */
    public static double wrapRad(double rad) {
        while (rad > Math.PI)  rad -= 2*Math.PI;
        while (rad < -Math.PI) rad += 2*Math.PI;
        return rad;
    }

    /** Smallest signed angle difference (rad) between two headings. */
    public static double deltaRad(double a, double b) {
        double d = b - a;
        return wrapRad(d);
    }
    public static double distance(double lat1, double lon1,
            double lat2, double lon2) {
double dx = lat1 - lat2;
double dy = lon1 - lon2;
return Math.sqrt(dx*dx + dy*dy);
}

public static double dotProduct(double[] a, double[] b) {
double sum = 0;
int n = Math.min(a.length, b.length);
for (int i = 0; i < n; i++) sum += a[i]*b[i];
return sum;
}

}
