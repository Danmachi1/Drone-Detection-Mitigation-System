package utils;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

/**
 * 📷 CameraFeedManager – Stores latest frame for each camera by ID.
 * Provides utility to access default feed (e.g. for single-view mode).
 */
public final class CameraFeedManager {

    private static final Map<String, Image> feeds = new HashMap<>();

    private CameraFeedManager() {}

    /** Push a new image frame from a specific camera ID */
    public static void publish(String cameraId, Image frame) {
        feeds.put(cameraId, frame);
    }

    /** Retrieve the latest image from a specific camera ID */
    public static Image latest(String cameraId) {
        return feeds.get(cameraId);
    }

    /** Retrieve the first available feed (default for single-camera systems) */
    public static Image getFrame() {
        return feeds.values().stream().findFirst().orElse(null);
    }
}
