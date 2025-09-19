package storage;

import logic.strategy.MissionBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 🗂 MissionRecorder – Captures every MissionBuilder deployment request
 * to a timestamped JSON file via JsonStorageManager.  Use it to replay
 * mission schedules in the simulator.
 */
public final class MissionRecorder {

    private static final String PATH = "missions";

    public static void record(MissionBuilder builder, String label) {
        String stamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String name  = label == null ? "mission-" + stamp : label + "-" + stamp;

        JsonStorageManager.save(PATH, name, builder);
    }

    private MissionRecorder() { /* util */ }
}
