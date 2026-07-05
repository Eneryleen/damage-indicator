package org.eneryleen.damage_indicator.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import org.eneryleen.damage_indicator.networking.payload.SpawnIndicatorPayload;

public class Damage_indicatorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SpawnIndicatorPayload.TYPE, Damage_indicatorClient::handleSpawnIndicator);
        ClientTickEvents.END_CLIENT_TICK.register(client -> DamageIndicatorManager.tick());
        WorldRenderEvents.AFTER_ENTITIES.register(DamageIndicatorRenderer::render);
    }

    private static void handleSpawnIndicator(SpawnIndicatorPayload payload, ClientPlayNetworking.Context context) {
        // Guard against a hostile/buggy server: NaN/Infinity in coordinates
        // corrupts the render matrices, and a non-positive/non-finite damage value is meaningless.
        if (!Double.isFinite(payload.x()) || !Double.isFinite(payload.y()) || !Double.isFinite(payload.z())
                || !Float.isFinite(payload.damage()) || payload.damage() <= 0) {
            return;
        }
        // Fabric play payload handlers already run on the render thread — no re-scheduling needed.
        DamageIndicatorManager.addIndicator(
                payload.x(), payload.y(), payload.z(), payload.damage(), payload.isCritical());
    }
}
