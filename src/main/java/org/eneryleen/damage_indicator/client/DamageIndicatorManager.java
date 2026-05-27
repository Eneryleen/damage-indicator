package org.eneryleen.damage_indicator.client;

import java.util.ArrayDeque;
import java.util.Deque;

public class DamageIndicatorManager {
    // Хард-кэп против флуда: без лимита 10k пакетов/с моментально съедают кучу.
    private static final int MAX_INDICATORS = 512;

    // ArrayDeque + synchronized: после enqueueWork всё идёт с main thread,
    // но render и tick тоже на main thread — sync дёшев, removeFirst() O(1)
    // (в отличие от CopyOnWriteArrayList.remove(0) — там O(n) на каждое переполнение).
    private static final Deque<DamageIndicator> indicators = new ArrayDeque<>();

    public static synchronized void addIndicator(double x, double y, double z, float damage, boolean isCritical) {
        if (indicators.size() >= MAX_INDICATORS) {
            indicators.pollFirst();
        }
        indicators.addLast(new DamageIndicator(x, y, z, damage, isCritical));
    }

    public static synchronized void tick() {
        long currentTime = System.currentTimeMillis();
        indicators.removeIf(indicator -> indicator.isExpired(currentTime));
    }

    /**
     * Возвращает снапшот списка индикаторов; рендер итерируется по снапшоту, чтобы
     * не держать монитор в горячем цикле и не падать на concurrent modification.
     */
    public static synchronized DamageIndicator[] snapshot() {
        return indicators.toArray(new DamageIndicator[0]);
    }
}
