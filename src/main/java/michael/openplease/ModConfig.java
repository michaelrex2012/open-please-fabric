package michael.openplease;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    public static boolean DoorAutoOpen = true;
    public static boolean TrapdoorAutoOpen = true;
    public static boolean GateAutoOpen = true;
    public static boolean ToggleSound = true

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/openplease.json");

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfigData data = GSON.fromJson(reader, ModConfigData.class);
                DoorAutoOpen = data.enableDoorAutoOpen;
                TrapdoorAutoOpen = data.enableTrapdoorAutoOpen;
                GateAutoOpen = data.enableGateAutoOpen;
                ToggleSound = data.enableSound;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save(); // save default config if file doesn't exist
        }
    }

    public static void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                ModConfigData data = new ModConfigData();
                data.enableDoorAutoOpen = DoorAutoOpen;
                data.enableTrapdoorAutoOpen = TrapdoorAutoOpen;
                data.enableGateAutoOpen = GateAutoOpen;
                data.enableSound = ToggleSound;
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner static class to store actual config data for serialization
    private static class ModConfigData {
        public boolean enableDoorAutoOpen = true;
        public boolean enableTrapdoorAutoOpen = true;
        public boolean enableGateAutoOpen = true;
        public boolean enableSound = true;
    }
}