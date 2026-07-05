package org.eneryleen.damage_indicator.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // cloth-config is optional (suggests, not depends): without it the config
        // button must be absent instead of crashing with NoClassDefFoundError.
        // The method reference below is resolved lazily, so the cloth-backed class
        // is never touched when cloth-config is missing.
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return screen -> null;
        }
        // Fully qualified: our ConfigScreenFactory shares its simple name with ModMenu's interface.
        return org.eneryleen.damage_indicator.config.ConfigScreenFactory::create;
    }
}
