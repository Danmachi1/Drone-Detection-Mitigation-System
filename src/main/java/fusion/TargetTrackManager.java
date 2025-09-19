	package fusion;
	
	import sensors.SensorDataRecord;
	
	import java.util.*;
	import java.util.concurrent.ConcurrentHashMap;
	
	/**
	 * 🎯 TargetTrackManager - Tracks active target states across sensors and fusion outputs.
	 * Handles lock-on logic, timeout decay, and track reassignment.
	 */
	public class TargetTrackManager {
	
	    private static final long TIMEOUT_MS = 5000;
	
	    public static class Track {
	        public final String id;
	        public double[] lastPosition;
	        public long lastSeen;
	        public boolean locked;
	
	        public Track(String id, double[] position) {
	            this.id = id;
	            this.lastPosition = position;
	            this.lastSeen = System.currentTimeMillis();
	            this.locked = false;
	        }
	
	        public boolean isStale() {
	            return System.currentTimeMillis() - lastSeen > TIMEOUT_MS;
	        }
	    }
	
	    private final Map<String, Track> tracks = new ConcurrentHashMap<>();
	
	    /**
	     * Updates or creates a track based on a new fused estimate.
	     */
	    public void updateTrack(String id, double[] newPosition) {
	        Track t = tracks.get(id);
	        if (t == null) {
	            t = new Track(id, newPosition);
	            tracks.put(id, t);
	        } else {
	            t.lastPosition = newPosition;
	            t.lastSeen = System.currentTimeMillis();
	
	            // Simple lock-on logic: seen consistently within short window
	            if (!t.locked && System.currentTimeMillis() - t.lastSeen < 2000) {
	                t.locked = true;
	            }
	        }
	    }
	
	    /**
	     * Gets list of active tracks (not timed out).
	     */
	    public List<Track> getActiveTracks() {
	        cleanup();
	        return new ArrayList<>(tracks.values());
	    }
	
	    /**
	     * Checks if a track is currently locked.
	     */
	    public boolean isLocked(String id) {
	        Track t = tracks.get(id);
	        return t != null && t.locked && !t.isStale();
	    }
	
	    /**
	     * Deletes old tracks that timed out.
	     */
	    private void cleanup() {
	        tracks.entrySet().removeIf(entry -> entry.getValue().isStale());
	    }
	
	    /**
	     * Manually reset all track data.
	     */
	    public void reset() {
	        tracks.clear();
	    }
	
	    /**
	     * Returns the current track for a given ID.
	     */
	    public Track getTrack(String id) {
	        return tracks.get(id);
	    }
	
	    /**
	     * Returns all current track IDs.
	     */
	    public Set<String> getTrackIds() {
	        cleanup();
	        return tracks.keySet();
	    }
	}
