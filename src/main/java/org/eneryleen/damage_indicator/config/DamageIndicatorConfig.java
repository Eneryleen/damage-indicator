package org.eneryleen.damage_indicator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DamageIndicatorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "damage_indicator.json");

    // Display settings
    public boolean enabled = true;
    public double maxRenderDistance = 32.0;
    public float baseScale = 0.025f;

    // Text settings
    public String damageFormat = "%.1f";
    public boolean showDecimals = true;

    // Color settings
    public int normalColor = 0xFFFFFF;
    public int criticalColor = 0xFFFF55;

    // Animation settings
    public long lifetime = 1200L;
    public long popDuration = 200L;
    public long fadeStartTime = 1000L;
    public double verticalSpeed = 1.5;
    public double horizontalSpread = 0.5;

    // Critical settings
    public float criticalScaleMultiplier = 1.25f;
    public float criticalPopEffect = 0.5f;
    public float normalPopEffect = 0.2f;

    private static DamageIndicatorConfig INSTANCE = null;

    public static DamageIndicatorConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static DamageIndicatorConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                return GSON.fromJson(reader, DamageIndicatorConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DamageIndicatorConfig config = new DamageIndicatorConfig();
        config.save();
        return config;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String formatDamage(float damage) {
        if (showDecimals) {
            return String.format(damageFormat, damage).replace(',', '.');
        } else {
            return String.valueOf((int) Math.ceil(damage));
        }
    }

    public int getColor(boolean isCritical) {
        return isCritical ? criticalColor : normalColor;
    }
}
