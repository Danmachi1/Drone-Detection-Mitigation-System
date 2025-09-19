	package sensors.plugins;
	
	import main.config.WeatherProvider;
	import sensors.SensorPlugin;
	import sensors.SensorDataRecord;
	
	import java.util.Collections;
	import java.util.List;
	import java.util.Map;
	import java.util.HashMap;
	
	/**
	 * 🌤 WeatherSensorPlugin – Wraps {@link WeatherProvider} so the fusion engine
	 * can consume wind / temperature as pseudo-sensor data.
	 *
	 * • Always simulation-safe (just reads WeatherProvider); no hardware path.
	 */
	public class WeatherSensorPlugin implements SensorPlugin {
	
	    private final WeatherProvider wx = new WeatherProvider();
	    private long lastUpdate = 0;
	    private SensorDataRecord lastRecord = null;
	
	    @Override
	    public List<SensorDataRecord> poll() {
	        lastUpdate = System.currentTimeMillis();
	
	        double windDir = wx.getWindDirection();      // degrees
	        double windSpd = wx.getWindSpeed();          // m/s
	        double tempC   = wx.getTemperature();        // °C
	
	        double vx = windSpd * Math.cos(Math.toRadians(windDir));
	        double vy = windSpd * Math.sin(Math.toRadians(windDir));
	
	        lastRecord = new SensorDataRecord(
	            lastUpdate,
	            0, 0,
	            vx, vy,
	            tempC, // use altitude field for temp
	            0,
	            "Weather"
	        );
	
	        return Collections.singletonList(lastRecord);
	    }
	
	    @Override
	    public boolean isSimulationMode() {
	        return true;
	    }
	
	    @Override
	    public void setSimulationMode(boolean sim) {
	        // Ignored: always sim
	    }
	
	    @Override
	    public long getLastUpdateTime() {
	        return lastUpdate;
	    }
	
	    @Override
	    public List<double[]> getThreatPositions() {
	        return Collections.emptyList();  // Weather is not a threat source
	    }
	
	    @Override
	    public Map<String, double[]> getDronePositions() {
	        return Collections.emptyMap();   // Weather doesn’t track drones
	    }
	
	    @Override
	    public List<double[]> getPredictedPaths() {
	        return Collections.emptyList();  // Weather has no object paths
	    }
	    private boolean active = true;

	    @Override
	    public void activate() {
	        active = true;
	    }

	    @Override
	    public void deactivate() {
	        active = false;
	    }

	    @Override
	    public boolean isActive() {
	        return active;
	    }

	    @Override
	    public String getPluginName() {
	        return "WeatherSensorPlugin";
	    }

	}
