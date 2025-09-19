		package logic.engage;
	
	import java.util.*;
	
	import logic.engage.InterceptionPlanner.InterceptorRole;
	import logic.threat.ThreatAlertEscalator;
	import logic.threat.ThreatAlertEscalator.ThreatLevel;
	
	/**
	 * ⚔️ EngagementManager - Handles active threat engagements by assigning drones,
	 * resolving conflicts, and managing mission lifecycles.
	 */
	public class EngagementManager {
	
	    private final DefenderDroneController droneController;
	    private final InterceptionPlanner planner;
	    private final ThreatAlertEscalator threatEscalator;
	    private final Set<String> engagedDrones = new HashSet<>();
	
	    public EngagementManager(DefenderDroneController droneController,
	                             InterceptionPlanner planner,
	                             ThreatAlertEscalator threatEscalator) {
	        this.droneController = droneController;
	        this.planner = planner;
	        this.threatEscalator = threatEscalator;
	    }
	
	    public void engageThreats(List<Object[]> threats, Map<String, double[]> droneData) {
	        for (Object[] threat : threats) {
	            String threatId = (String) threat[0];
	            double[] threatPos = new double[]{(double) threat[1], (double) threat[2], (double) threat[3]};
	
	            List<double[]> dronePosList = new ArrayList<>();
	            List<String> droneIds = new ArrayList<>();
	
	            for (Map.Entry<String, double[]> entry : droneData.entrySet()) {
	                if (!engagedDrones.contains(entry.getKey())) {
	                    dronePosList.add(entry.getValue());
	                    droneIds.add(entry.getKey());
	                }
	            }
	
	            if (dronePosList.isEmpty()) {
	                System.out.println("⚠️ No available drones for threat " + threatId);
	                continue;
	            }
	
	            int bestIndex = planner.selectInterceptor(droneIds, dronePosList, threatPos, InterceptorRole.KAMIKAZE);
	
	            if (bestIndex != -1) {
	                String selectedDroneId = droneIds.get(bestIndex);
	                droneController.assignMission(selectedDroneId, threatId, "M-" + threatId, InterceptionPlanner.InterceptorRole.KAMIKAZE);
	                engagedDrones.add(selectedDroneId);
	                System.out.println("✅ Assigned " + selectedDroneId + " to threat " + threatId);
	            } else {
	                System.out.println("❌ No suitable drone found for threat " + threatId);
	            }
	        }
	    }
	
	    public void engageTarget(String targetId, double[] position, Map<String, double[]> droneData) {
	        List<double[]> dronePosList = new ArrayList<>();
	        List<String> droneIds = new ArrayList<>();
	
	        for (Map.Entry<String, double[]> entry : droneData.entrySet()) {
	            if (!engagedDrones.contains(entry.getKey())) {
	                dronePosList.add(entry.getValue());
	                droneIds.add(entry.getKey());
	            }
	        }
	
	        if (dronePosList.isEmpty()) {
	            System.out.println("⚠️ No available drones for target " + targetId);
	            return;
	        }
	
	        int bestIndex = planner.selectInterceptor(droneIds, dronePosList, position, InterceptorRole.KAMIKAZE);
	
	        if (bestIndex != -1) {
	            String selectedDrone = droneIds.get(bestIndex);
	            droneController.assignMission(selectedDrone, targetId, "M-" + targetId, InterceptionPlanner.InterceptorRole.KAMIKAZE);
	            engagedDrones.add(selectedDrone);
	            System.out.println("✅ Target " + targetId + " engaged by drone: " + selectedDrone);
	        } else {
	            System.out.println("❌ No interceptor selected for target " + targetId);
	        }
	    }
	
	   public void onThreatEscalated(String id, ThreatLevel fromLevel, ThreatLevel toLevel) {
	    System.out.printf("📣 EngagementManager: %s escalated %s → %s%n", id, fromLevel, toLevel);
	
	    threatKillChains.putIfAbsent(id, new ArrayList<>());
	    List<String> chain = threatKillChains.get(id);
	    if (!chain.contains("DETECT")) chain.add("DETECT");
	    if (!chain.contains("CLASSIFY")) chain.add("CLASSIFY");
	
	    if (toLevel == ThreatLevel.CRITICAL) {
	        if (!chain.contains("TRACK")) chain.add("TRACK");
	
	        double[] position = threatEscalator.getThreatPosition(id);
	        Map<String, double[]> droneData = droneController.getAllDrones();
	
	        if (position != null && droneData != null) {
	            engageTarget(id, position, droneData);
	            if (!chain.contains("DECIDE")) chain.add("DECIDE");
	            if (!chain.contains("ENGAGE")) chain.add("ENGAGE");
	        } else {
	            System.out.println("⚠️ Missing data to engage target: " + id);
	        }
	    }
	}
	
	    public Set<String> getEngagedDrones() {
	        return new HashSet<>(engagedDrones);
	    }
	
	    public void releaseDrone(String droneId) {
	        droneController.resetDrone(droneId);
	        engagedDrones.remove(droneId);
	    }
	
	    public void abortAll() {
	        for (String droneId : engagedDrones) {
	            droneController.cancelMission(droneId);
	        }
	        engagedDrones.clear();
	    }
	
	    public boolean isDroneEngaged(String droneId) {
	        return engagedDrones.contains(droneId);
	    }
	    public static boolean manualOverride(String threatId, String action) {
	        try {
	            System.out.println("⚠️ Manual override issued: " + action + " → " + threatId);
	
	            // Example logic (can be extended with real mission rerouting):
	            switch (action.toLowerCase()) {
	                case "track" -> {
	                    // Simulate tagging the threat
	                    System.out.println("📡 Tracking threat " + threatId);
	                    return true;
	                }
	                case "neutralize" -> {
	                    // Simulate ordering attack
	                    System.out.println("💥 Neutralizing threat " + threatId);
	                    return true;
	                }
	                case "ignore" -> {
	                    // Mark it as ignored (could modify its threat level)
	                    System.out.println("🚫 Ignoring threat " + threatId);
	                    return true;
	                }
	                default -> {
	                    System.err.println("⚠️ Unknown override action: " + action);
	                    return false;
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("❌ Manual override failed: " + e.getMessage());
	            return false;
	        }
	    }
	    /**
	     * Returns the current kill-chain status for each engaged threat.
	     * Each threat maps to a sequence of kill chain stages.
	     */
	    private final Map<String, List<String>> threatKillChains = new HashMap<>();
	
	    public Map<String, String[]> getKillChainStatus() {
	        Map<String, String[]> status = new HashMap<>();
	        for (Map.Entry<String, List<String>> entry : threatKillChains.entrySet()) {
	            status.put(entry.getKey(), entry.getValue().toArray(new String[0]));
	        }
	        return status;
	    }
	    public void confirmEngagement(String threatId) {
	        List<String> chain = threatKillChains.computeIfAbsent(threatId, k -> new ArrayList<>());
	        if (!chain.contains("CONFIRM")) chain.add("CONFIRM");
	        System.out.println("✅ Threat " + threatId + " confirmed neutralized.");
	    }
	
	    public DefenderDroneController getDroneController() {
	        return this.droneController;
	    }
	    public static boolean overrideThreatDecision(String threatId, String action) {
	        return manualOverride(threatId, action);
	    }

	    /**
	     * Returns current threat statuses in simple form: [ThreatID → StatusString].
	     * Status can be inferred from kill-chain or engagement state.
	     */
	    public Map<String, String> getActiveThreatStatuses() {
	        Map<String, String> map = new HashMap<>();
	        for (Map.Entry<String, List<String>> entry : threatKillChains.entrySet()) {
	            String id = entry.getKey();
	            List<String> stages = entry.getValue();
	            String status = stages.isEmpty() ? "UNKNOWN" : stages.get(stages.size() - 1);
	            map.put(id, status);
	        }
	        return map;
	    }


	
	}
