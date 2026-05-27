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

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();
        if (!config.enabled) return;

        Camera camera = event.getCamera();
        if (camera == null) return;

        PoseStack poseStack = event.getPoseStack();
        Minecraft client = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();
        Font font = client.font;
        long currentTime = System.currentTimeMillis();

        Vec3 cameraPos = camera.getPosition();
        double maxDistanceSquared = config.maxRenderDistance * config.maxRenderDistance;

        for (DamageIndicator indicator : DamageIndicatorManager.getIndicators()) {
            Vec3 indicatorPos = indicator.getPosition(currentTime);
            float alpha = indicator.getAlpha(currentTime);
            float scale = indicator.getScale(currentTime);

            if (alpha <= 0) continue;

            double distanceSquared = cameraPos.distanceToSqr(indicatorPos);
            if (distanceSquared > maxDistanceSquared) continue;

            poseStack.pushPose();

            poseStack.translate(indicatorPos.x - cameraPos.x, indicatorPos.y - cameraPos.y, indicatorPos.z - cameraPos.z);

            poseStack.mulPose(new Quaternionf().rotationY(-camera.getYRot() * DEGREES_TO_RADIANS));
            poseStack.mulPose(new Quaternionf().rotationX(camera.getXRot() * DEGREES_TO_RADIANS));

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
