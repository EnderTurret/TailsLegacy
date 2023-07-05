/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 *
 * Some code provided by iChun under LGPLv3
 */

package uk.kihira.tails.client;

import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Various rendering-related utilities.
 */
public final class RenderHelper {

	/**
	 * Begins a {@linkplain GL11#glScissor(int, int, int, int) gl scissor} using the given <em>GUI</em> coordinates.
	 * These coordinates are converted automatically to <em>screen</em> coordinates for the scissor.
	 * @param x The coordinate of the left side of the scissor.
	 * @param y The coordinate of the top side of the scissor.
	 * @param width The width of the scissor.
	 * @param height The height of the scissor.
	 */
	public static void startGlScissor(int x, int y, int width, int height) {
		final Window mc = Minecraft.getInstance().getWindow();

		final double scaleW = (double)mc.getScreenWidth() / mc.getGuiScaledWidth();
		final double scaleH = (double)mc.getScreenHeight() / mc.getGuiScaledHeight();

		RenderSystem.enableScissor((int)Math.floor(x * scaleW),
				(int) Math.floor(mc.getScreenHeight() - (y + height) * scaleH),
				(int) Math.floor((x + width) * scaleW) - (int) Math.floor(x * scaleW),
				(int) Math.floor(mc.getScreenHeight() - y * scaleH) - (int) Math.floor(mc.getScreenHeight() - (y + height) * scaleH)); // Starts from lower left corner (minecraft starts from upper left)
	}

	/**
	 * Ends a {@linkplain GL11#glScissor(int, int, int, int) gl scissor}.
	 */
	public static void endGlScissor() {
		RenderSystem.disableScissor();
	}

	/**
	 * Draws a string that respects new lines.
	 * @param gui The {@link GuiGraphics}.
	 * @param font The {@link Font} to use for drawing the text.
	 * @param text The text to draw.
	 * @param x The x position of the text.
	 * @param y The y position of the text.
	 * @param color The color of the text.
	 */
	public static void drawStringMultiLine(GuiGraphics gui, Font font, String text, int x, int y, int color) {
		final String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i];
			gui.drawString(font, line, x, y + font.lineHeight * i, color);
		}
	}

	public static void blitScaled(GuiGraphics gui, int x, int y, int blitOffset, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
		final float f = 0.00390625F;
		final float f1 = 0.00390625F;
		final PoseStack.Pose e = gui.pose().last();
		final Tesselator tess = Tesselator.getInstance();
		final BufferBuilder renderer = tess.getBuilder();
		renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		renderer.vertex(e.pose(), x + 0,		y + tarHeight,	blitOffset).uv((u + 0) * f,			(v + srcHeight) * f1).endVertex();
		renderer.vertex(e.pose(), x + tarWidth,	y + tarHeight,	blitOffset).uv((u + srcWidth) * f,	(v + srcHeight) * f1).endVertex();
		renderer.vertex(e.pose(), x + tarWidth,	y + 0,			blitOffset).uv((u + srcWidth) * f,	(v + 0) * f1).endVertex();
		renderer.vertex(e.pose(), x + 0,		y + 0,			blitOffset).uv((u + 0) * f,			(v + 0) * f1).endVertex();
		tess.end();
	}

	/**
	 * Renders the given entity like in the {@linkplain InventoryScreen#renderEntityInInventory(int, int, int, float, float, LivingEntity) inventory screen}.
	 * @param x The x coordinate of the entity.
	 * @param y The y coordinate of the entity.
	 * @param scale The scale to render the entity at.
	 * @param yaw The yaw of the entity.
	 * @param pitch The pitch of the entity.
	 * @param partialTick The partial tick.
	 * @param entity The entity to render.
	 */
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

		final Quaternionf quaternion = new Quaternionf().rotateZ(Mth.PI);
		final Quaternionf quaternion1 = new Quaternionf().rotateX(pitch * 20F * Mth.DEG_TO_RAD);
		quaternion.mul(quaternion1);

		pose2.mulPose(quaternion);
		pose2.mulPose(new Quaternionf().rotateZ(Mth.PI));
		pose2.mulPose(new Quaternionf().rotateY(yaw * Mth.DEG_TO_RAD));

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

		quaternion1.conjugate();

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
}