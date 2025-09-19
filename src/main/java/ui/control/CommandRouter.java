package ui.control;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 🎮 CommandRouter – Central hub that UI widgets publish high-level commands to.
 * Subsystems (swarm, sensors, storage) can register handlers by key.
 *
 * Example:
 *    CommandRouter router = new CommandRouter();
 *    router.on("shutdown", v -> System.exit(0));
 *    router.route("shutdown");
 */
public class CommandRouter {

    private final Map<String, Consumer<String>> handlers = new HashMap<>();

    /** Register a handler for the given command key. */
    public void on(String key, Consumer<String> cb) {
        handlers.put(key.toLowerCase(), cb);
    }

    /** Dispatch a command (key + optional payload). */
    public void route(String command, String payload) {
        Consumer<String> cb = handlers.get(command.toLowerCase());
        if (cb != null) {
            cb.accept(payload);
        } else {
            System.err.println("⚠️  CommandRouter: unrecognised command " + command);
        }
    }

    /** Convenience overload for commands without payload. */
    public void route(String command) { route(command, ""); }
}
