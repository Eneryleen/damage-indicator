package org.eneryleen.damage_indicator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.eneryleen.damage_indicator.Damage_indicator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.IllegalFormatException;

public class DamageIndicatorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("damage_indicator.json").toFile();
    public static final String DEFAULT_DAMAGE_FORMAT = "%.1f";

    // Display settings
    public boolean enabled = true;
    public double maxRenderDistance = 32.0;
    public float baseScale = 0.025f;

    // Text settings
    public String damageFormat = DEFAULT_DAMAGE_FORMAT;
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

    // volatile is required for a correct double-checked-locking singleton:
    // without it the JIT/CPU may publish a reference to a partially-constructed object.
    private static volatile DamageIndicatorConfig INSTANCE = null;

    public static DamageIndicatorConfig getInstance() {
        DamageIndicatorConfig local = INSTANCE;
        if (local == null) {
            synchronized (DamageIndicatorConfig.class) {
                local = INSTANCE;
                if (local == null) {
                    local = load();
                    INSTANCE = local;
                }
            }
        }
        return local;
    }

    public static DamageIndicatorConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                DamageIndicatorConfig loaded = GSON.fromJson(reader, DamageIndicatorConfig.class);
                if (loaded != null) {
                    return loaded;
                }
                Damage_indicator.LOGGER.warn("Config file {} is empty, using defaults", CONFIG_FILE);
            } catch (Exception e) {
                // Catch both IOException and JsonSyntaxException (a RuntimeException) —
                // a broken JSON file must not bring the mod down; defaulting is safer.
                Damage_indicator.LOGGER.warn("Failed to load config {}, falling back to defaults: {}",
                        CONFIG_FILE, e.toString());
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
            Damage_indicator.LOGGER.warn("Failed to save config {}: {}", CONFIG_FILE, e.toString());
        }
    }

    public String formatDamage(float damage) {
        if (!showDecimals) {
            return String.valueOf((int) Math.ceil(damage));
        }
        try {
            return String.format(damageFormat, damage).replace(',', '.');
        } catch (IllegalFormatException e) {
            // A broken damageFormat (e.g. "%d" or "%s") would otherwise crash the client on every hit.
            return String.format(DEFAULT_DAMAGE_FORMAT, damage).replace(',', '.');
        }
    }

    public int getColor(boolean isCritical) {
        return isCritical ? criticalColor : normalColor;
    }
}
