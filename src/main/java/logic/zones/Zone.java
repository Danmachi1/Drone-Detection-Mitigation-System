package logic.zones;

import java.util.List;

import utils.MathUtils;

/**
 * 🗺️ Zone – Immutable circle zone used for no-fly, asset, and threat areas.
 * Coordinates are metres in the same ENU grid used by the rest of the project.
 */
public final class Zone {
    private final List<double[]> polygon;
    private final String id;
    private final double cx, cy;   // centre
    private final double radius;   // metres

    public Zone(String id, double cx, double cy, double radius, List<double[]> polygon) {
        this.id = id;
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.polygon = polygon;
    }

    /* ------------------------------------------------------------------ */

    public String getId()         { return id; }
    public double getCenterX()    { return cx; }
    public double getCenterY()    { return cy; }
    public double getRadius()     { return radius; }

    /* ------------------------------------------------------------------ */

    /** Euclidean distance from point (x,y) to zone centre. */
    public double distanceTo(double x, double y) {
        double dx = x - cx, dy = y - cy;
        return Math.hypot(dx, dy);
    }

    /** True if (x,y) lies *inside* the circle (inclusive). */
    public boolean contains(double x, double y) {
        return distanceTo(x, y) <= radius;
    }

    /** True if point is within `padding` metres of the boundary. */
    public boolean isNear(double x, double y, double padding) {
        double d = distanceTo(x, y);
        return d >= radius && d <= radius + padding;
    }

    @Override public String toString() {
        return "Zone[" + id + " r=" + radius +
               " at (" + cx + "," + cy + ")]";
    }
    /**
     * Returns true if the point is strictly inside the zone (not on the edge).
     */
    public boolean contains(double[] pos) {
        return MathUtils.distance(getCenterX(), getCenterY(), pos[0], pos[1]) < getRadius();
    }

    /**
     * Returns true if the point is near the zone within a given threshold.
     */
    public boolean isNear(double[] pos, double threshold) {
        return MathUtils.distance(getCenterX(), getCenterY(), pos[0], pos[1]) < (getRadius() + threshold);
    }

    /**
     * Returns true if the object is heading toward this zone based on velocity vector.
     */
    public boolean isHeadingToward(double[] pos, double[] vel) {
        double[] zoneVec = {getCenterX() - pos[0], getCenterY() - pos[1]};
        double dot = MathUtils.dotProduct(MathUtils.normalize(vel), MathUtils.normalize(zoneVec));
        return dot > 0.85; // ~30 degree heading margin
    }




    public List<double[]> getPolygon() {
        return polygon;
    }
    

}
