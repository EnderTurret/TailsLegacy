/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 *
 * Some code provided by iChun under LGPLv3
 */

package net.enderturret.tailslegacy.forge.client;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import net.enderturret.tailslegacy.common.TailsMath;

/**
 * Various rendering-related utilities.
 */
public final class RenderHelper {

	/**
	 * Begins a {@linkplain GL11#glScissor(int, int, int, int) gl scissor} using the given <em>GUI</em> coordinates.
	 * These coordinates are converted automatically to <em>screen</em> coordinates for the scissor.
	 * @param x0 The coordinate of the left side of the scissor.
	 * @param y0 The coordinate of the top side of the scissor.
	 * @param x1 The coordinate of the right side of the scissor.
	 * @param y1 The coordinate of the bottom side of the scissor.
	 */
	public static void startGlScissor(int x0, int y0, int x1, int y1) {
		final Window mc = Minecraft.getInstance().getWindow();

		final double scaleW = mc.getGuiScale();
		final double scaleH = mc.getGuiScale();
		final int screenHeight = mc.getHeight();

		RenderSystem.enableScissor(
				(int) (x0 * scaleW),
				(int) (screenHeight - y1 * scaleH),
				(int) Math.max(0, (x1 - x0) * scaleW),
				(int) Math.max(0, (y1 - y0) * scaleH)); // Starts from lower left corner (minecraft starts from upper left)
	}

	/**
	 * Ends a {@linkplain GL11#glScissor(int, int, int, int) gl scissor}.
	 */
	public static void endGlScissor() {
		RenderSystem.disableScissor();
	}

	// Blits a texture 'scaled' to fit a larger/smaller area.
	public static void blitScaled(PoseStack poseStack, int x, int y, int blitOffset, int u, int v, int uWidth, int vHeight, int width, int height) {
		final Matrix4f pose = poseStack.last().pose();
		final Tesselator tess = Tesselator.getInstance();
		final BufferBuilder renderer = tess.getBuilder();

		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		renderer.vertex(pose, x + 0,		y + height,	blitOffset).uv((u + 0) / 256F,		(v + vHeight) / 256F).endVertex();
		renderer.vertex(pose, x + width,	y + height,	blitOffset).uv((u + uWidth) / 256F,	(v + vHeight) / 256F).endVertex();
		renderer.vertex(pose, x + width,	y + 0,		blitOffset).uv((u + uWidth) / 256F,	(v + 0) / 256F).endVertex();
		renderer.vertex(pose, x + 0,		y + 0,		blitOffset).uv((u + 0) / 256F,		(v + 0) / 256F).endVertex();

		BufferUploader.drawWithShader(renderer.end());
	}

	/**
	 * Renders the given entity like in the {@linkplain InventoryScreen#renderEntityInInventoryRaw(int, int, int, float, float, LivingEntity) inventory screen}.
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

		poseStack.pushPose();
		poseStack.translate(x, y, 1050);
		poseStack.scale(1, 1, -1);

		RenderSystem.applyModelViewMatrix();

		final PoseStack entityPose = new PoseStack();
		entityPose.translate(0, 0, 1000);
		entityPose.scale(scale, scale, scale);

		final Quaternion pose = Vector3f.ZP.rotation(TailsMath.PI);
		final Quaternion cameraOrientation = Vector3f.XP.rotationDegrees(pitch * 20F);
		pose.mul(cameraOrientation);

		entityPose.mulPose(pose);
		entityPose.mulPose(Vector3f.ZP.rotation(Mth.PI));
		entityPose.mulPose(Vector3f.YP.rotationDegrees(yaw));

		Lighting.setupForEntityInInventory();

		final EntityRenderDispatcher rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
		final MultiBufferSource.BufferSource impl = Minecraft.getInstance().renderBuffers().bufferSource();

		cameraOrientation.conj();

		rendererManager.overrideCameraOrientation(cameraOrientation);
		rendererManager.setRenderShadow(false);

		RenderSystem.runAsFancy(() -> {
			rendererManager.render(entity, 0, 0, 0, 0F, 1F, entityPose, impl, LightTexture.FULL_BRIGHT);
		});

		impl.endBatch();

		rendererManager.setRenderShadow(true);

		poseStack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();

		entity.yBodyRot = oldYBodyRot;
		entity.setYRot(oldYRot);
		entity.setXRot(oldXRot);
		entity.yHeadRot = oldYHeadRot;
		entity.yHeadRotO = oldYHeadRotO;
	}

	public static int getColourAtPoint(double x, double y) {
		final Minecraft mc = Minecraft.getInstance();

		// We have to resolve these mouse coordinates back to window coordinates.
		final double scale = mc.getWindow().getGuiScale();
		x *= scale;
		y *= scale;

		// We also have to flip the y coordinate because OpenGL's
		// coordinate system is upside-down compared to ours.
		y = mc.getWindow().getHeight() - y;

		mc.getMainRenderTarget().bindRead();
		GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mc.getMainRenderTarget().frameBufferId);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);

		RenderSystem.pixelStore(GL11.GL_PACK_ALIGNMENT, 1);
		RenderSystem.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 1);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			final ByteBuffer pixelBuffer = stack.calloc(3);

			RenderSystem.readPixels((int) x, (int) y, 1, 1,
					GL11.GL_RGB,
					GL11.GL_UNSIGNED_BYTE,
					pixelBuffer);

			pixelBuffer.rewind();

			final int r = pixelBuffer.get() & 0xFF;
			final int g = pixelBuffer.get() & 0xFF;
			final int b = pixelBuffer.get() & 0xFF;

			return (r << 16) | (g << 8) | b;
		}
	}

	public static void drawScrollingString(PoseStack poseStack, Font font, Component text, int minX, int maxX, int y, int color) {
		final int maxWidth = maxX - minX;
		final int textWidth = font.width(text.getVisualOrderText());
		if (textWidth <= maxWidth)
			GuiComponent.drawString(poseStack, font, text, minX, y, color);
		else
			drawCenteredScrollingString(poseStack, font, text, (minX + maxX) / 2, minX, y, maxX, y + font.lineHeight, color);
	}

	public static void drawCenteredScrollingString(PoseStack poseStack, Font font, Component text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
		final int textWidth = font.width(text);
		final int y = (minY + maxY - 9) / 2 + 1;
		final int width = maxX - minX;

		if (textWidth > width) {
			final int delta = textWidth - width;
			final double time = Util.getMillis() / 1000.0;
			final double d1 = Math.max(delta * 0.5, 3.0);
			final double scrollProgress = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * time / d1)) / 2.0 + 0.5;
			final double scroll = Mth.lerp(scrollProgress, 0, delta);

			startGlScissor(minX, minY, maxX, maxY);
			GuiComponent.drawString(poseStack, font, text, minX - (int)scroll, y, color);
			endGlScissor();

			return;
		}

		final int x = Mth.clamp(centerX, minX + textWidth / 2, maxX - textWidth / 2);
		GuiComponent.drawCenteredString(poseStack, font, text, x, y, color);
	}
}