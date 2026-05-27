package org.eneryleen.damage_indicator.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.eneryleen.damage_indicator.config.DamageIndicatorConfig;
import org.joml.Quaternionf;

public class DamageIndicatorRenderer {
    private static final float DEGREES_TO_RADIANS = 0.017453292F;

    // In 26.1.x we subscribe to a concrete subclass (AfterOpaqueFeatures ~= old
    // Stage.AFTER_ENTITIES); the Stage enum and event.getCamera() were removed.
    // The live Camera (with pos/yaw/pitch) is taken from EntityRenderDispatcher.camera —
    // the same field vanilla entity rendering uses.
    public static void render(RenderLevelStageEvent.AfterOpaqueFeatures event) {
        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();
        if (!config.enabled) return;

        Minecraft client = Minecraft.getInstance();
        Camera camera = client.getEntityRenderDispatcher().camera;
        if (camera == null) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();
        Font font = client.font;
        long currentTime = System.currentTimeMillis();

        // 26.1.x: Mojang dropped the get-prefix on Camera accessors.
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
        bufferSource.endBatch();
    }
}
