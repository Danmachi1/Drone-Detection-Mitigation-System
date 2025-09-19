package utils.alerts;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/** Thread-safe FIFO queue for short textual alerts. */
public final class AlertManager {

    public interface AlertListener {
        void onAlert(String severity, String message);
    }

    private static final Queue<String> queue = new LinkedList<>();
    private static final CopyOnWriteArrayList<AlertListener> listeners = new CopyOnWriteArrayList<>();

    private AlertManager() {}

    public static void push(String severity, String msg) {
        synchronized (queue) {
            queue.add("[" + severity + "] " + msg);
        }
        for (AlertListener listener : listeners) {
            listener.onAlert(severity, msg);
        }
    }

    public static void push(String msg) {
        push("INFO", msg); // default severity
    }

    public static String poll() {
        synchronized (queue) {
            return queue.poll();
        }
    }

    public static int size() {
        synchronized (queue) {
            return queue.size();
        }
    }

    public static void subscribe(AlertListener listener) {
        listeners.add(listener);
    }

    public static void unsubscribe(AlertListener listener) {
        listeners.remove(listener);
    }
}
