package org.eneryleen.damage_indicator;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.eneryleen.damage_indicator.config.DamageIndicatorConfig;
import org.eneryleen.damage_indicator.event.DamageEventHandler;
import org.eneryleen.damage_indicator.networking.payload.SpawnIndicatorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Damage_indicator implements ModInitializer {
    public static final String MOD_ID = "damage_indicator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        DamageIndicatorConfig.getInstance();
        PayloadTypeRegistry.playS2C().register(SpawnIndicatorPayload.TYPE, SpawnIndicatorPayload.STREAM_CODEC);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(DamageEventHandler::onAfterDamage);
        LOGGER.info("Damage Indicator initialized!");
    }
}
