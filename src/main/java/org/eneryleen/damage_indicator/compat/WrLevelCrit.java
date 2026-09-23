package org.eneryleen.damage_indicator.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.eneryleen.damage_indicator.Damage_indicator;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Crit status of the hit from wrlevel (Windlands): there a crit is a perk with its own chance, rolled by
 * wrartifacts / skills / weapons and reported through WrLevelApi.reportCrit; jumping is not a crit.
 * Looked up reflectively so the mod still runs without wrlevel (then vanilla jump crit applies).
 */
public final class WrLevelCrit {
    private WrLevelCrit() {}

    private static final MethodHandle IS_CRIT_HIT = find();
    private static boolean failed;

    private static MethodHandle find() {
        if (!FabricLoader.getInstance().isModLoaded("wrlevel")) return null;
        try {
            Class<?> api = Class.forName("org.eneryleen.wrlevel.WrLevelApi");
            return MethodHandles.publicLookup().findStatic(api, "isCritHit",
                    MethodType.methodType(boolean.class, LivingEntity.class, DamageSource.class));
        } catch (ReflectiveOperationException | LinkageError e) {
            Damage_indicator.LOGGER.warn("[damage_indicator] wrlevel without WrLevelApi.isCritHit (needs 0.14.0+), "
                    + "crits are guessed from the jump", e);
            return null;
        }
    }

    /** wrlevel answers for this hit; false — wrlevel is absent or too old. */
    public static boolean available() {
        return IS_CRIT_HIT != null && !failed;
    }

    public static boolean isCritHit(LivingEntity target, DamageSource source) {
        try {
            return (boolean) IS_CRIT_HIT.invokeExact(target, source);
        } catch (Throwable t) {
            failed = true;
            Damage_indicator.LOGGER.error("[damage_indicator] WrLevelApi.isCritHit failed", t);
            return false;
        }
    }
}
