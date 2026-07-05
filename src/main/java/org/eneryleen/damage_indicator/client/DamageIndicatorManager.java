package org.eneryleen.damage_indicator.client;

import java.util.ArrayDeque;
import java.util.Deque;

public class DamageIndicatorManager {
    // Hard cap against flood: without it, 10k packets/s would exhaust the heap.
    private static final int MAX_INDICATORS = 512;

    // ArrayDeque + synchronized: the payload receiver runs on the render thread and
    // tick on the client tick — the same main thread in practice, so the sync is
    // cheap insurance, and pollFirst() is O(1)
    // (vs CopyOnWriteArrayList.remove(0) — O(n) on every overflow).
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
     * Returns a snapshot of the indicator list; the renderer iterates over the snapshot
     * so it doesn't hold the monitor during the hot loop and doesn't risk concurrent modification.
     */
    public static synchronized DamageIndicator[] snapshot() {
        return indicators.toArray(new DamageIndicator[0]);
    }
}
