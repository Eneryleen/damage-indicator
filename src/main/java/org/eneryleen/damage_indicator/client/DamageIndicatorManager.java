package org.eneryleen.damage_indicator.client;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DamageIndicatorManager {
    private static final List<DamageIndicator> indicators = new CopyOnWriteArrayList<>();

    public static void addIndicator(double x, double y, double z, float damage, boolean isCritical) {
        indicators.add(new DamageIndicator(x, y, z, damage, isCritical));
    }

    public static void tick() {
        long currentTime = System.currentTimeMillis();
        indicators.removeIf(indicator -> indicator.isExpired(currentTime));
    }

    public static List<DamageIndicator> getIndicators() {
        return indicators;
    }
}
