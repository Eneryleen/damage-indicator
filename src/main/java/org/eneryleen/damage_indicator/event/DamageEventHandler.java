package org.eneryleen.damage_indicator.event;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.eneryleen.damage_indicator.networking.payload.SpawnIndicatorPayload;

import java.util.LinkedHashSet;
import java.util.Set;

public class DamageEventHandler {

    // Fabric's AFTER_DAMAGE limits vs the old NeoForge LivingDamageEvent.Post:
    //  - damageTaken includes shield/freezing mitigation but NOT armor/enchantments
    //    (true HP lost is not exposed without a mixin);
    //  - the event does not fire on a killing blow, so no number on the final hit.
    // The event fires only on the server (from hurtServer), so the old
    // isClientSide() guard is no longer needed.
    public static void onAfterDamage(LivingEntity entity, DamageSource source,
                                     float baseDamageTaken, float damageTaken, boolean blocked) {
        float amount = damageTaken;
        if (amount <= 0) {
            return;
        }

        double x = entity.getX();
        double y = entity.getY() + entity.getBbHeight() * 0.75;
        double z = entity.getZ();

        boolean isCritical = false;
        if (source.getEntity() instanceof Player player) {
            isCritical = player.fallDistance > 0.0F &&
                    !player.onGround() &&
                    !player.onClimbable() &&
                    !player.isInWater() &&
                    !player.isPassenger() &&
                    !player.isSprinting();
        }

        SpawnIndicatorPayload payload = new SpawnIndicatorPayload(x, y, z, amount, isCritical);

        // PlayerLookup.tracking() does not guarantee the entity itself is included,
        // so add it explicitly; the Set dedupes in case it was.
        Set<ServerPlayer> targets = new LinkedHashSet<>(PlayerLookup.tracking(entity));
        if (entity instanceof ServerPlayer self) {
            targets.add(self);
        }
        for (ServerPlayer player : targets) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}
