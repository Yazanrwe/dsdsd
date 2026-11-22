package com.example.freelook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple JSON config stored at .minecraft/config/freelook.json
 */
public class FreelookConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("freelook.json");

    /** false = hold the key to look around (like Lunar default), true = press once to enable, again to disable. */
    public boolean toggle = false;

    /** Switch to third person (back) while freelooking, then restore the previous perspective. */
    public boolean forceThirdPerson = true;

    public static FreelookConfig load() {
        FreelookConfig config = new FreelookConfig();
        if (Files.exists(PATH)) {
            try {
                FreelookConfig read = GSON.fromJson(Files.readString(PATH), FreelookConfig.class);
                if (read != null) {
                    config = read;
                }
            } catch (Exception e) {
                System.err.println("[Freelook] Failed to read config, using defaults: " + e);
            }
        }
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("[Freelook] Failed to save config: " + e);
        }
    }
}
