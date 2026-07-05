package org.eneryleen.damage_indicator.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.eneryleen.damage_indicator.config.DamageIndicatorConfig;
import org.joml.Quaternionf;

public class DamageIndicatorRenderer {
    private static final float DEGREES_TO_RADIANS = 0.017453292F;

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
                    15728880
            );

            poseStack.popPose();
        }
    }
}
