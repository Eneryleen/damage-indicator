package org.eneryleen.damage_indicator.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.eneryleen.damage_indicator.Damage_indicator;
import org.eneryleen.damage_indicator.networking.payload.SpawnIndicatorPayload;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Damage_indicator.MOD_ID)
public class DamageEventHandler {

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        // getNewDamage = реально снятое HP (после брони, зачарований, absorption);
        // getOriginalDamage показывал бы исходный урон до защит — путаница для игрока.
        float amount = event.getNewDamage();

        if (amount <= 0 || entity.level().isClientSide()) {
            return;
        }

        double x = entity.getX();
        double y = entity.getY() + entity.getBbHeight() * 0.75;
        double z = entity.getZ();

        boolean isCritical = false;
        if (event.getSource().getEntity() instanceof Player player) {
            isCritical = player.fallDistance > 0.0F &&
                    !player.onGround() &&
                    !player.onClimbable() &&
                    !player.isInWater() &&
                    !player.isPassenger() &&
                    !player.isSprinting();
        }

        SpawnIndicatorPayload payload = new SpawnIndicatorPayload(x, y, z, amount, isCritical);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload);
    }
}
