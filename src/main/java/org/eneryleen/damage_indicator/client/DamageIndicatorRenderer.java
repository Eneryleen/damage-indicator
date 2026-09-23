package org.eneryleen.damage_indicator.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.eneryleen.damage_indicator.Damage_indicator;
import org.eneryleen.damage_indicator.config.DamageIndicatorConfig;
import org.joml.Quaternionf;

public class DamageIndicatorRenderer {
    private static final float DEGREES_TO_RADIANS = 0.017453292F;
    private static final int FULL_BRIGHT = 15728880;
    private static final float CRIT_LABEL_SCALE = 0.85f;
    private static final int CRIT_OUTLINE_COLOR = 0x4A1800;
    private static final FontDescription CRIT_FONT = new FontDescription.Resource(
            Identifier.fromNamespaceAndPath(Damage_indicator.MOD_ID, "crit"));

    // Hooked via WorldRenderEvents.AFTER_ENTITIES; context.matrices() and
    // context.consumers() are guaranteed non-null in drawing-phase events.
    // The camera is NOT available on the drawing-phase context in 1.21.11,
    // so it is taken from the game renderer directly.
    public static void render(WorldRenderContext context) {
        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();
        if (!config.enabled) return;

        Minecraft client = Minecraft.getInstance();
        Camera camera = client.gameRenderer.getMainCamera();
        if (camera == null) return;

        PoseStack poseStack = context.matrices();
        MultiBufferSource bufferSource = context.consumers();
        Font font = client.font;
        long currentTime = System.currentTimeMillis();
        FormattedCharSequence critLabel = critLabel(config);

        Vec3 cameraPos = camera.position();
        double maxDistanceSquared = config.maxRenderDistance * config.maxRenderDistance;

        for (DamageIndicator indicator : DamageIndicatorManager.snapshot()) {
            Vec3 indicatorPos = indicator.getPosition(currentTime);
            float alpha = indicator.getAlpha(currentTime);
            float scale = indicator.getScale(currentTime);

            if (alpha <= 0) continue;

            double distanceSquared = cameraPos.distanceToSqr(indicatorPos);
            if (distanceSquared > maxDistanceSquared) continue;

            poseStack.pushPose();

            poseStack.translate(indicatorPos.x - cameraPos.x, indicatorPos.y - cameraPos.y, indicatorPos.z - cameraPos.z);

            poseStack.mulPose(new Quaternionf().rotationY(-camera.yRot() * DEGREES_TO_RADIANS));
            poseStack.mulPose(new Quaternionf().rotationX(camera.xRot() * DEGREES_TO_RADIANS));

            float baseScale = -config.baseScale * (indicator.isCritical ? config.criticalScaleMultiplier : 1.0f);
            poseStack.scale(baseScale * scale, baseScale * scale, baseScale);

            int color = indicator.color;
            int alphaComponent = ((int) (alpha * 255.0f)) << 24;
            int finalColor = color | alphaComponent;

            float textWidth = font.width(indicator.text);

            // drawInBatch bakes the current matrix into the vertices, so the buffer
            // does not need to be flushed here — the world renderer flushes it later.
            font.drawInBatch(
                    indicator.text,
                    -textWidth / 2,
                    0f,
                    finalColor,
                    true,
                    poseStack.last().pose(),
                    bufferSource,
                    Font.DisplayMode.SEE_THROUGH,
                    0,
                    FULL_BRIGHT
            );

            if (indicator.isCritical && critLabel != null) {
                drawCritLabel(font, critLabel, poseStack, bufferSource, config.criticalLabelColor | alphaComponent,
                        CRIT_OUTLINE_COLOR | alphaComponent);
            }

            poseStack.popPose();
        }
    }

    /**
     * "Crit!" over the number, in the number's own pose, so it follows its pop and fade.
     * Text comes from lang and is laid out as a FormattedCharSequence: width and glyphs
     * (including unifont fallback for CJK) are resolved by the font, not assumed per language.
     */
    private static void drawCritLabel(Font font, FormattedCharSequence label, PoseStack poseStack,
                                      MultiBufferSource bufferSource, int color, int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(0f, -1f, 0f);
        poseStack.scale(CRIT_LABEL_SCALE, CRIT_LABEL_SCALE, 1f);
        float x = -font.width(label) / 2f;
        float y = -font.lineHeight;
        // Outline by hand: drawInBatch8xOutline is depth-tested, the number is SEE_THROUGH —
        // the label would vanish behind blocks while its number stays visible.
        for (int i = 0; i < 4; i++) {
            float dx = i == 0 ? -1f : i == 1 ? 1f : 0f;
            float dy = i == 2 ? -1f : i == 3 ? 1f : 0f;
            font.drawInBatch(label, x + dx, y + dy, outlineColor, false, poseStack.last().pose(),
                    bufferSource, Font.DisplayMode.SEE_THROUGH, 0, FULL_BRIGHT);
        }
        font.drawInBatch(label, x, y, color, false, poseStack.last().pose(),
                bufferSource, Font.DisplayMode.SEE_THROUGH, 0, FULL_BRIGHT);
        poseStack.popPose();
    }

    private static FormattedCharSequence critLabel(DamageIndicatorConfig config) {
        if (!config.showCriticalLabel) return null;
        // Own font id: a nicer typeface (and extra scripts) can be dropped into
        // assets/damage_indicator/font/crit.json without touching code; unifont stays the fallback.
        return Component.translatable("damage_indicator.critical")
                .withStyle(style -> style.withBold(true).withFont(CRIT_FONT))
                .getVisualOrderText();
    }
}
