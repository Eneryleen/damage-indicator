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
        // Защита от враждебного/багованного сервера: NaN/Infinity в координатах
        // ломает матрицы рендера, а отрицательный/нечисловой damage не имеет смысла.
        if (!Double.isFinite(payload.x()) || !Double.isFinite(payload.y()) || !Double.isFinite(payload.z())
                || !Float.isFinite(payload.damage()) || payload.damage() <= 0) {
            return;
        }
        // Сетевой пакет приходит в network-треде; работа с состоянием рендера
        // (DamageIndicatorManager / Minecraft instance) обязана идти в main-треде.
        context.enqueueWork(() -> DamageIndicatorManager.addIndicator(
                payload.x(), payload.y(), payload.z(), payload.damage(), payload.isCritical()));
    }
}
