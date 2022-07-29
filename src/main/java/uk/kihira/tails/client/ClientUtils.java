/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.UUID;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.LivingEntity;

public class ClientUtils {

	/**
	 * Draws a string that respects new lines.
	 * @param poseStack The {@link PoseStack} to use for transformation information.
	 * @param fontRenderer The {@link Font} to use for drawing the text.
	 * @param string The text to draw.
	 * @param x The x position of the text.
	 * @param y The y position of the text.
	 * @param color The color of the text.
	 */
	public static void drawStringMultiLine(PoseStack poseStack, Font fontRenderer, String string, int x, int y, int color) {
		final String[] lines = string.split("\n");
		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i];
			fontRenderer.draw(poseStack, line, x, y + fontRenderer.lineHeight * i, color);
		}
	}

	public static void drawCenteredString(PoseStack poseStack, Font fontRenderer, String string, int x, int y, int color) {
		final int width = fontRenderer.width(string);
		fontRenderer.draw(poseStack, string, x - width / 2, y, color);
	}

	public static void blitScaled(PoseStack poseStack, int x, int y, int blitOffset, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
		final float f = 0.00390625F;
		final float f1 = 0.00390625F;
		final PoseStack.Pose e = poseStack.last();
		final Tesselator tess = Tesselator.getInstance();
		final BufferBuilder renderer = tess.getBuilder();
		renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		renderer.vertex(e.pose(), x + 0,		y + tarHeight,	blitOffset).uv((u + 0) * f,			(v + srcHeight) * f1).endVertex();
		renderer.vertex(e.pose(), x + tarWidth,	y + tarHeight,	blitOffset).uv((u + srcWidth) * f,	(v + srcHeight) * f1).endVertex();
		renderer.vertex(e.pose(), x + tarWidth,	y + 0,			blitOffset).uv((u + srcWidth) * f,	(v + 0) * f1).endVertex();
		renderer.vertex(e.pose(), x + 0,		y + 0,			blitOffset).uv((u + 0) * f,			(v + 0) * f1).endVertex();
		tess.end();
	}

	@SuppressWarnings("deprecation")
	public static void drawEntity(int x, int y, int scale, float yaw, float pitch, float partialTick, LivingEntity entity) {
		final PoseStack poseStack = RenderSystem.getModelViewStack();

		poseStack.pushPose();

		poseStack.translate(x, y, 1050);
		poseStack.scale(1, 1, -1);

		RenderSystem.applyModelViewMatrix();

		final PoseStack pose2 = new PoseStack();
		pose2.translate(0, 0, 1000);
		pose2.scale(scale, scale, scale);

		final Quaternion quaternion = Vector3f.ZP.rotationDegrees(180f);
		final Quaternion quaternion1 = Vector3f.XP.rotationDegrees(pitch * 20F);
		quaternion.mul(quaternion1);

		pose2.mulPose(quaternion);
		pose2.mulPose(Vector3f.ZP.rotationDegrees(180));
		pose2.mulPose(Vector3f.YP.rotationDegrees(yaw));

		final float oldYBodyRot = entity.yBodyRot;
		final float oldYRot = entity.getYRot();
		final float oldXRot = entity.getXRot();
		final float oldYHeadRot = entity.yHeadRot;
		final float oldYHeadRotO = entity.yHeadRotO;

		entity.yBodyRot = 0;
		entity.setYRot(0);
		entity.setXRot(0);
		entity.yHeadRot = 0;
		entity.yHeadRotO = 0;
		entity.setShiftKeyDown(false);

		Lighting.setupForEntityInInventory();

		final EntityRenderDispatcher rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();

		quaternion1.conj();

		rendererManager.overrideCameraOrientation(quaternion1);
		rendererManager.setRenderShadow(false);

		final MultiBufferSource.BufferSource impl = Minecraft.getInstance().renderBuffers().bufferSource();

		RenderSystem.runAsFancy(() -> {
			rendererManager.render(entity, 0, 0, 0, 0F, 1F, pose2, impl, 15728880);
		});

		impl.endBatch();

		rendererManager.setRenderShadow(true);

		entity.yBodyRot = oldYBodyRot;
		entity.setYRot(oldYRot);
		entity.setXRot(oldXRot);
		entity.yHeadRot = oldYHeadRot;
		entity.yHeadRotO = oldYHeadRotO;

		poseStack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}

	public static UUID getPlayerUUID() {
		final Minecraft mc = Minecraft.getInstance();
		/*if (mc.player != null && mc.player.getUniqueID() != null)
			return mc.player.getUniqueID();*/
		return UUIDUtil.getOrCreatePlayerUUID(mc.getUser().getGameProfile());
	}
}