package org.eneryleen.damage_indicator.client;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DamageIndicatorManager {
    // Хард-кэп против флуда с враждебного сервера: CopyOnWriteArrayList.add — O(n),
    // без лимита 10k пакетов/с моментально съедают кучу и замораживают рендер.
    private static final int MAX_INDICATORS = 512;

    private static final List<DamageIndicator> indicators = new CopyOnWriteArrayList<>();

    public static void addIndicator(double x, double y, double z, float damage, boolean isCritical) {
        if (indicators.size() >= MAX_INDICATORS) {
            // Дропаем самый старый, чтобы новые удары всё-таки показывались.
            indicators.remove(0);
        }
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
