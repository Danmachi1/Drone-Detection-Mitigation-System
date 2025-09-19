package utils.logging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Simple in-memory log buffer the UI viewer can read. */
public final class LogStorageManager {

    private static final List<String> lines = new ArrayList<>();

    private LogStorageManager() {}

    public static void append(String level, String msg) {
        lines.add(LocalDateTime.now() + " " + level + " " + msg);
    }

    public static List<String> snapshot() {
        return Collections.unmodifiableList(lines);
    }
}
