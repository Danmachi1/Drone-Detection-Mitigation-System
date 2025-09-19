package main;

import main.config.EnvironmentDetector;
import main.config.SimulationClock;

/**
 * 🏁 SkyShieldLauncher – Thin command-line wrapper that chooses between
 * simulation and live-hardware modes, then delegates to {@link MainApp}.
 *
 * Usage examples:
 *   java -jar skyshield.jar                # auto detect
 *   java -jar skyshield.jar --simulate     # force sim
 *   java -jar skyshield.jar --realtime     # force real-time (multiplier =1)
 *   java -jar skyshield.jar --speed 4.0    # 4× fast-forward
 */
public final class SkyShieldLauncher {

    public static void main(String[] args) {

        boolean simMode = EnvironmentDetector.isSimulation();
        double  speed   = 1.0;

        /* ── Parse CLI flags ─────────────────────────────────── */
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--simulate" -> simMode = true;
                case "--realtime" -> simMode = false;
                case "--speed"    -> {
                    if (i + 1 < args.length)
                        speed = Double.parseDouble(args[++i]);
                }
            }
        }

        /* ── Configure clocks & launch ───────────────────────── */
        SimulationClock.setMultiplier(speed);

        System.out.println("SkyShield starting (" +
                (simMode ? "SIM" : "LIVE") + ", speed=" + speed + "×)");

        MainApp app = new MainApp();
        app.init(simMode);

        /* Basic run-loop; production builds should use an Executor. */
        while (true) {
            app.update();
            try { Thread.sleep(33); } catch (InterruptedException ignored) {}
        }
    }
}
