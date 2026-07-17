package org.eneryleen.damage_indicator.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.eneryleen.damage_indicator.event.DamageEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    // Fabric's AFTER_DAMAGE does not fire on a killing blow, so the final hit showed
    // no number. actuallyHurt is called only with damage that is really applied
    // (full amount, or the delta beyond i-frames), including the lethal hit — same
    // injection point the spotmobs damage tracker uses. The amount here is
    // post-shield / pre-armor, matching what AFTER_DAMAGE used to report.
    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    private void damage_indicator$onDamage(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        DamageEventHandler.onDamage((LivingEntity) (Object) this, source, amount);
    }
}
