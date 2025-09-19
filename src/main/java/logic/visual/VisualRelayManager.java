package logic.visual;

import java.util.*;

/**
 * 📺 VisualRelayManager - Manages video streams from drones, including switching
 * between bio-inspired vision modes like FalconVision, Infrared, and MultiView.
 * Integrates with simulation and real-time control interface.
 */
public class VisualRelayManager {

    private static final Set<String> SUPPORTED_MODES = Set.of("Normal", "FalconVision", "Infrared", "MultiView");

    // Holds currently active streams by drone ID
    private static final Map<String, String> activeStreams = new HashMap<>();

    // Mock registry of available drones for demonstration
    private static final List<String> simulatedDrones = Arrays.asList(
            "Drone-A1", "Drone-B2", "Drone-C3", "Drone-Scout-1"
    );

    /**
     * Returns a list of drones available for visual relay.
     */
    public static List<String> getAvailableDrones() {
        return new ArrayList<>(simulatedDrones);
    }

    /**
     * Requests a video stream for a drone in the specified vision mode.
     * 
     * @param droneId the ID of the drone to stream from
     * @param mode the vision mode to activate
     * @return true if stream initialized successfully, false otherwise
     */
    public static boolean requestStream(String droneId, String mode) {
        if (droneId == null || mode == null) return false;
        if (!simulatedDrones.contains(droneId)) return false;
        if (!SUPPORTED_MODES.contains(mode)) return false;

        // Simulate activating video stream (replace with actual streaming logic)
        System.out.println("📡 Activating " + mode + " stream for " + droneId);

        activeStreams.put(droneId, mode);
        return true;
    }

    /**
     * Returns the currently active stream mode for a given drone.
     * 
     * @param droneId drone identifier
     * @return the mode if active, or null if not streaming
     */
    public static String getActiveStreamMode(String droneId) {
        return activeStreams.get(droneId);
    }

    /**
     * Stops an active video stream for a drone.
     * 
     * @param droneId drone identifier
     * @return true if stream was active and stopped, false if no stream
     */
    public static boolean stopStream(String droneId) {
        if (activeStreams.containsKey(droneId)) {
            System.out.println("🛑 Stopping video stream for " + droneId);
            activeStreams.remove(droneId);
            return true;
        }
        return false;
    }

    /**
     * Returns a list of all active video streams (droneId → mode).
     */
    public static Map<String, String> getActiveStreams() {
        return new HashMap<>(activeStreams);
    }
}
