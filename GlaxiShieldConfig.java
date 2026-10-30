package dev.glaxiblade.glaxishield;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GlaxiShieldConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("glaxishield.json");
    public static String ign = "Glaxiblade";
    public static String hex = "#00D9FF";

    private GlaxiShieldConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        try (Reader reader = Files.newBufferedReader(FILE)) {
            Data data = GSON.fromJson(reader, Data.class);
            if (data != null) {
                if (data.ign != null && !data.ign.isBlank()) ign = data.ign;
                if (validHex(data.hex)) hex = data.hex.toUpperCase();
            }
        } catch (Exception ignored) { }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(new Data(ign, hex), writer);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Could not save GlaxiShield config", exception);
        }
    }

    public static boolean validHex(String value) {
        return value != null && value.matches("#[0-9a-fA-F]{6}");
    }

    private record Data(String ign, String hex) {}
}
