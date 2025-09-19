package logic.mission;

import java.io.*;
import java.nio.file.*;

import logic.mission.MissionProfile;

/**
 * 💾 MissionManager – Saves and loads MissionProfile objects to disk.
 * Uses serialized `.mission` files in ./missions directory.
 */
public class MissionManager {

    private static final Path MISSION_DIR = Paths.get("missions");

    static {
        try {
            Files.createDirectories(MISSION_DIR);
        } catch (IOException e) {
            System.err.println("❌ Failed to create mission directory: " + e.getMessage());
        }
    }

    public static boolean saveMission(MissionProfile mission) {
        Path path = MISSION_DIR.resolve(mission.getId() + ".mission");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            out.writeObject(mission);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Failed to save mission: " + e.getMessage());
            return false;
        }
    }

    public static MissionProfile loadMission(String id) {
        Path path = MISSION_DIR.resolve(id + ".mission");
        if (!Files.exists(path)) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (MissionProfile) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Failed to load mission: " + e.getMessage());
            return null;
        }
    }
}
