package main;

import fusion.FusionCoordinator;
import fusion.SensorFusionEngine;
import logic.swarm.DroneAgent;
import logic.swarm.SwarmManager;
import main.config.EnvironmentDetector;
import main.config.NoFlyZoneManager;
import main.config.TerrainDataLoader;
import sensors.MultiSensorManager;
import sensors.SensorFallbackController;
import sensors.SensorHealthMonitor;

/**
 * 🚀 MainApp – central bootstrap & real-time loop.
 */
public final class MainApp {

    /* ── core subsystems ────────────────────────────────────────── */
    private final MultiSensorManager    sensorMgr   = new MultiSensorManager();
    private final SensorFusionEngine    fusionEngine = new SensorFusionEngine();
    private final SensorHealthMonitor   healthMonitor = new SensorHealthMonitor();
    private final SwarmManager          swarmManager  = new SwarmManager();
    private final FusionCoordinator     coordinator   = new FusionCoordinator();
    private final SensorFallbackController fallback =
            new SensorFallbackController(healthMonitor, fusionEngine);
 // ── internal fusion delta tracking ──
    private double lastX = Double.NaN;
    private double lastY = Double.NaN;


    /* ── singleton handle visible to all UI threads ────────────── */
    private static volatile MainApp INSTANCE;                  // <-- `volatile`
    public  static MainApp get()      { return INSTANCE; }     // read-only view

    /* public read-only accessors for Java-FX panes */
    public MultiSensorManager getSensors() { return sensorMgr; }
    public SwarmManager       getSwarm()   { return swarmManager; }
    public SensorFusionEngine getFusion()  { return fusionEngine; }

    /* ── ctor – publishes the singleton exactly once ───────────── */
    public MainApp() {
        if (INSTANCE != null)
            throw new IllegalStateException("MainApp already instantiated");
        INSTANCE = this;
    }

    /* ── lifecycle ─────────────────────────────────────────────── */
    public void init(boolean simulation) {

        /* static data */
        NoFlyZoneManager.loadFromCsv("nofly_zones.csv");
        new TerrainDataLoader().loadCsv("terrain_grid.csv");

        /* sensors */
        sensorMgr.initialize(simulation);
        sensorMgr.getActivePlugins()
                 .forEach(p -> healthMonitor.register(p.getClass().getSimpleName(), p));

        /* demo drones */
        swarmManager.registerDrone(new DroneAgent("D-A", coordinator));
        swarmManager.registerDrone(new DroneAgent("D-B", coordinator));

        String msg = "🟢 MainApp initialised – simulation=" + simulation;
        System.out.println(msg);
		        
        ui.SystemLogPanel.append(msg);

    }

    /** one control-loop tick (≈ 30 Hz) */
    public void update() {
    	// quick tweak in main/MainApp.update()

    	double[] fused = coordinator.fuse(sensorMgr.pollAll());
    	if (fused != null && !Double.isNaN(fused[0])) {
    	    if (Double.isNaN(lastX) ||                     // first time
    	        Math.hypot(fused[0] - lastX, fused[1] - lastY) > 0.5) {

    	        System.out.printf("Fusion: x=%.1f  y=%.1f  alt=%.0f%n",
    	                          fused[0], fused[1], fused[5]);
    	        lastX = fused[0];
    	        lastY = fused[1];
    	    }
    	}

        fallback.heartbeat();
     // 🔋 Battery drain applied to all drones
        for (DroneAgent d : swarmManager.getAllDrones()) {
            d.updateBattery();
        }

        /* demo: print once per second */
        if (System.currentTimeMillis() % 1000 < 30 && fused != null) {
            System.out.printf("Fusion: x=%.1f  y=%.1f  alt=%.0f%n",
                              fused[0], fused[1], fused[5]);
        }
    }

    /* ── launcher ──────────────────────────────────────────────── */
    public static void main(String[] args) {

        boolean sim = !EnvironmentDetector.isWindows();
        MainApp app = new MainApp();      // ctor sets INSTANCE
        app.init(sim);

        /* hand reference to the Java-FX Application BEFORE it starts */
        ui.MainUI.setBackend(app);

        /* launch Java-FX UI on a dedicated thread */
        new Thread(() -> javafx.application.Application.launch(ui.MainUI.class),
                   "JavaFX-Launcher").start();

        /* real-time loop stays on this (main) thread */
        while (true) {
            app.update();
            try { Thread.sleep(33); } catch (InterruptedException ignored) {}
        }
    }
    public FusionCoordinator getCoordinator() {
        return coordinator;
    }
    	
}
