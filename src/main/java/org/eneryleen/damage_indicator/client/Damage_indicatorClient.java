package org.eneryleen.damage_indicator.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.eneryleen.damage_indicator.Damage_indicator;
import org.eneryleen.damage_indicator.config.ConfigScreenFactory;

@Mod(value = Damage_indicator.MOD_ID, dist = Dist.CLIENT)
public class Damage_indicatorClient {

    public Damage_indicatorClient(IEventBus modBus, ModContainer container) {
        // Explicit class-token (as NeoForge itself does): reliable subtype resolution for RenderLevelStageEvent.
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, Damage_indicatorClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(RenderLevelStageEvent.AfterOpaqueFeatures.class, DamageIndicatorRenderer::render);

        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (modContainer, parentScreen) -> ConfigScreenFactory.create(parentScreen)
        );
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        DamageIndicatorManager.tick();
    }
}
