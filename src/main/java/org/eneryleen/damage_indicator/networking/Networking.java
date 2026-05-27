package org.eneryleen.damage_indicator.networking;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.eneryleen.damage_indicator.client.DamageIndicatorManager;
import org.eneryleen.damage_indicator.networking.payload.SpawnIndicatorPayload;

public class Networking {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                SpawnIndicatorPayload.TYPE,
                SpawnIndicatorPayload.STREAM_CODEC,
                Networking::handleSpawnIndicator
        );
    }

    private static void handleSpawnIndicator(SpawnIndicatorPayload payload, IPayloadContext context) {
        // Guard against a hostile/buggy server: NaN/Infinity in coordinates
        // corrupts the render matrices, and a non-positive/non-finite damage value is meaningless.
        if (!Double.isFinite(payload.x()) || !Double.isFinite(payload.y()) || !Double.isFinite(payload.z())
                || !Float.isFinite(payload.damage()) || payload.damage() <= 0) {
            return;
        }
        // Payloads arrive on the network thread; touching render state
        // (DamageIndicatorManager / Minecraft instance) must happen on the main thread.
        context.enqueueWork(() -> DamageIndicatorManager.addIndicator(
                payload.x(), payload.y(), payload.z(), payload.damage(), payload.isCritical()));
    }
}
