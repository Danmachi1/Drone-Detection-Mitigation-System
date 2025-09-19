package logic.visual;

import java.util.*;

/**
 * 🦅 FalconVisionAnalyzer - Performs high-zoom visual analysis to classify and track drone types.
 * Supports stealth detection, pattern matching, and visual-based threat confirmation.
 */
public class FalconVisionAnalyzer {

    public enum VisualType {
        MICRO_QUAD, MINI_HEX, FIXED_WING, UNKNOWN, BIRD, CIVILIAN
    }

    // Simulated pattern hashes (placeholder for real ML model)
    private final Map<String, VisualType> shapeLibrary = new HashMap<>();

    public FalconVisionAnalyzer() {
        shapeLibrary.put("Q4-S", VisualType.MICRO_QUAD);
        shapeLibrary.put("H6-L", VisualType.MINI_HEX);
        shapeLibrary.put("FW1", VisualType.FIXED_WING);
        shapeLibrary.put("BIRD", VisualType.BIRD);
    }

    /**
     * Classifies the visual pattern hash into a known type.
     */
    public VisualType classifyVisualPattern(String hash) {
        return shapeLibrary.getOrDefault(hash, VisualType.UNKNOWN);
    }

    /**
     * Simulates object silhouette analysis and returns a pattern hash.
     */
    public String analyzeSilhouette(double pixelWidth, double pixelHeight, double motionSmoothness) {
        if (pixelWidth < 15 && pixelHeight < 15 && motionSmoothness < 0.1)
            return "Q4-S"; // Micro quad
        if (pixelWidth < 25 && pixelHeight < 25 && motionSmoothness < 0.2)
            return "H6-L"; // Mini hex
        if (pixelWidth > 40 && motionSmoothness > 0.8)
            return "FW1";  // Fixed wing
        if (motionSmoothness > 0.9)
            return "BIRD";
        return "UNK";
    }

    /**
     * Returns whether the visual type is a threat class.
     */
    public boolean isVisualThreat(VisualType type) {
        return type == VisualType.MICRO_QUAD ||
               type == VisualType.MINI_HEX ||
               type == VisualType.FIXED_WING ||
               type == VisualType.UNKNOWN;
    }

    /**
     * Adds a new visual pattern hash.
     */
    public void registerPattern(String hash, VisualType type) {
        shapeLibrary.put(hash, type);
    }
}
