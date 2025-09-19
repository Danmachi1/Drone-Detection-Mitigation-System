package plugins;

import java.util.ArrayList;
import java.util.List;

/**
 * 📦 PluginLoader – Very small reflection-based loader.  Pass a list of fully
 * qualified class names; we instantiate each one that implements
 * {@link PluginInterface}.  Any constructor with zero args is accepted.
 *
 * Usage:
 *    List<PluginInterface> mods =
 *        PluginLoader.load(List.of("my.pkg.CsvExporter", "other.LogForwarder"));
 */
public final class PluginLoader {

    private PluginLoader() { /* util – no instances */ }

    /**
     * Attempts to load and initialise each class name.
     *
     * @return list of _started_ plugins.  Faulty entries are skipped with a log.
     */
    public static List<PluginInterface> load(List<String> classNames) {
        List<PluginInterface> plugins = new ArrayList<>();

        for (String cn : classNames) {
            try {
                Class<?> clazz = Class.forName(cn);
                if (!PluginInterface.class.isAssignableFrom(clazz)) {
                    System.err.println("⚠️  " + cn + " does not implement PluginInterface – skipped.");
                    continue;
                }
                PluginInterface plugin = (PluginInterface) clazz.getDeclaredConstructor().newInstance();
                plugin.init();
                plugin.start();
                plugins.add(plugin);

                System.out.println("✅ Loaded plugin: " + plugin.getName() +
                                   " v" + plugin.getVersion());
            } catch (Exception ex) {
                System.err.println("❌ Failed to load plugin " + cn + " – " + ex.getMessage());
            }
        }
        return plugins;
    }
}
