package logic.mission;

import java.io.Serializable;

/**
 * 📄 MissionProfile – Represents a configured mission for the swarm.
 * Includes zone info, number of drones, and vision mode.
 */
public class MissionProfile implements Serializable {
	   private static final long serialVersionUID = 1L;

	    /* global default – single-drone visual recon in no-zone context */
	    public static final MissionProfile DEFAULT =
	            new MissionProfile("default", "GLOBAL", 1, "VISUAL");
    private final String id;
    private final String zone;
    private final int droneCount;
    private final String visionMode;

    public MissionProfile(String id, String zone, int droneCount, String visionMode) {
        this.id = id;
        this.zone = zone;
        this.droneCount = droneCount;
        this.visionMode = visionMode;
    }
    

    public String getId()         { return id; }
    public String getZone()       { return zone; }
    public int getDroneCount()    { return droneCount; }
    public String getVisionMode() { return visionMode; }
}
