package utils.logging;

import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Lightweight façade over Log4j 2 so the rest of SkyShield can call:
 *
 *   LogManager.getLogger(MyClass.class).info("msg");
 *   LogManager.error("something went wrong");
 *   LogManager.error("msg", throwable);
 *
 * ⚠ This class lives in utils.logging to avoid clashing with
 * org.apache.logging.log4j.LogManager.
 */
public final class LogManager {

    private LogManager() {}  // Utility class

    /* ───────────── Logger Getter ───────────── */

    public static Logger getLogger(Class<?> clazz) {
        return org.apache.logging.log4j.LogManager.getLogger(clazz);
    }

    /* ───────────── Logging Methods ───────────── */

    public static void error(String message) {
        ROOT.error(message);
        notifyListeners("ERROR", message);
    }

    public static void error(String message, Throwable t) {
        ROOT.error(message, t);
        notifyListeners("ERROR", message + " " + t.getMessage());
    }

    public static void warn(String message) {
        ROOT.warn(message);
        notifyListeners("WARN", message);
    }

    public static void info(String message) {
        ROOT.info(message);
        notifyListeners("INFO", message);
    }

    /* ───────────── Real-Time Listener Support ───────────── */

    public interface LogListener {
        void onLog(long timestamp, String level, String message);
    }

    private static final List<LogListener> listeners = new CopyOnWriteArrayList<>();

    public static void addLogListener(LogListener listener) {
        listeners.add(listener);
    }

    private static void notifyListeners(String level, String message) {
        long now = System.currentTimeMillis();
        for (LogListener l : listeners) {
            l.onLog(now, level, message);
        }
    }

    /* ───────────── Internal Logger ───────────── */

    private static final Logger ROOT =
            org.apache.logging.log4j.LogManager.getLogger("GLOBAL");
}
