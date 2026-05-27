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
        DamageIndicatorManager.addIndicator(payload.x(), payload.y(), payload.z(), payload.damage(), payload.isCritical());
    }
}
