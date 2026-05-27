package org.eneryleen.damage_indicator.client;

import net.minecraft.world.phys.Vec3;
import org.eneryleen.damage_indicator.config.DamageIndicatorConfig;

import java.util.Random;

public class DamageIndicator {
    public final String text;
    public final int color;
    public final boolean isCritical;
    public final long creationTime;
    public final double initialX;
    public final double initialY;
    public final double initialZ;

    private static final Random RANDOM = new Random();

    public DamageIndicator(double x, double y, double z, float damage, boolean isCritical) {
        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();

        this.text = config.formatDamage(damage);
        this.isCritical = isCritical;
        this.color = config.getColor(isCritical);
        this.creationTime = System.currentTimeMillis();

        double offsetX = (RANDOM.nextDouble() - 0.5) * config.horizontalSpread;
        double offsetZ = (RANDOM.nextDouble() - 0.5) * config.horizontalSpread;

        this.initialX = x + offsetX;
        this.initialY = y;
        this.initialZ = z + offsetZ;
    }

    private long getAge(long currentTime) {
        return currentTime - this.creationTime;
    }

    public boolean isExpired(long currentTime) {
        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();
        return getAge(currentTime) > config.lifetime;
    }

    public float getAlpha(long currentTime) {
        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();
        long age = getAge(currentTime);
        if (age > config.fadeStartTime) {
            float fadeProgress = (age - config.fadeStartTime) / (float) (config.lifetime - config.fadeStartTime);
            return Math.max(0, 1.0f - fadeProgress);
        }
        return 1.0f;
    }

    public Vec3 getPosition(long currentTime) {
        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();
        long age = getAge(currentTime);
        double progress = Math.min(age / (double) config.lifetime, 1.0);

        double easing = progress * (2 - progress);
        double verticalOffset = easing * config.verticalSpeed;

        return new Vec3(initialX, initialY + verticalOffset, initialZ);
    }

    public float getScale(long currentTime) {
        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();
        long age = getAge(currentTime);
        float baseScale = 1.0f;

        if (age < config.popDuration) {
            float popProgress = age / (float) config.popDuration;
            float popEffect = 1.0f + (float) Math.sin(popProgress * Math.PI) *
                    (isCritical ? config.criticalPopEffect : config.normalPopEffect);
            return baseScale * popEffect;
        }

        return baseScale;
    }
}
