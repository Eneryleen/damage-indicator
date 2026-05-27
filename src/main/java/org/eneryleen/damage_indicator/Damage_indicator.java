package org.eneryleen.damage_indicator;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.eneryleen.damage_indicator.config.DamageIndicatorConfig;
import org.eneryleen.damage_indicator.networking.Networking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Damage_indicator.MOD_ID)
public class Damage_indicator {
    public static final String MOD_ID = "damage_indicator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public Damage_indicator(IEventBus modBus, ModContainer container) {
        DamageIndicatorConfig.getInstance();
        modBus.addListener(Networking::registerPayloads);
        LOGGER.info("Damage Indicator initialized!");
    }
}
