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

package uk.kihira.tails.forge.client;

import java.nio.ByteBuffer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common.TailsMath;

/**
 * Various rendering-related utilities.
 */
public final class RenderHelper {

	// Blits a texture 'scaled' to fit a larger/smaller area.
	public static void blitScaled(GuiGraphics gui, int x, int y, int blitOffset, int u, int v, int uWidth, int vHeight, int width, int height) {
		final Matrix4f pose = gui.pose().last().pose();
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
	 * Renders the given entity like in the {@linkplain InventoryScreen#renderEntityInInventory(GuiGraphics, int, int, int, Quaternionf, Quaternionf, LivingEntity) inventory screen}.
	 * @param gui The {@link GuiGraphics}.
	 * @param x The x coordinate of the entity.
	 * @param y The y coordinate of the entity.
	 * @param scale The scale to render the entity at.
	 * @param yaw The yaw of the entity.
	 * @param pitch The pitch of the entity.
	 * @param partialTick The partial tick.
	 * @param entity The entity to render.
	 */
	public static void drawEntity(GuiGraphics gui, int x, int y, int scale, float yaw, float pitch, float partialTick, LivingEntity entity) {
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

		final Quaternionf pose = new Quaternionf().rotateZ(TailsMath.PI);
		final Quaternionf cameraOrientation = new Quaternionf().rotateX(pitch * 20F * TailsMath.DEG_TO_RAD);
		pose.mul(cameraOrientation);

		pose.mul(new Quaternionf().rotateZ(TailsMath.PI));
		pose.mul(new Quaternionf().rotateY(yaw * TailsMath.DEG_TO_RAD));

		InventoryScreen.renderEntityInInventory(gui, x, y, scale, pose, cameraOrientation, entity);

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

	public static int drawScrollingString(GuiGraphics gui, Font font, Component text, int minX, int maxX, int y, int color) {
		final int maxWidth = maxX - minX;
		final int textWidth = font.width(text.getVisualOrderText());
		if (textWidth <= maxWidth)
			return gui.drawString(font, text, minX, y, color);
		else {
			drawCenteredScrollingString(gui, font, text, (minX + maxX) / 2, minX, y, maxX, y + font.lineHeight, color);
			return maxWidth;
		}
	}

	public static void drawCenteredScrollingString(GuiGraphics gui, Font font, Component text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
		final int textWidth = font.width(text);
		final int y = (minY + maxY - 9) / 2 + 1;
		final int width = maxX - minX;

		if (textWidth > width) {
			final int delta = textWidth - width;
			final double time = Util.getMillis() / 1000.0;
			final double d1 = Math.max(delta * 0.5, 3.0);
			final double scrollProgress = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * time / d1)) / 2.0 + 0.5;
			final double scroll = Mth.lerp(scrollProgress, 0, delta);

			gui.enableScissor(minX, minY, maxX, maxY);
			gui.drawString(font, text, minX - (int)scroll, y, color);
			gui.disableScissor();

			return;
		}

		final int x = Mth.clamp(centerX, minX + textWidth / 2, maxX - textWidth / 2);
		gui.drawCenteredString(font, text, x, y, color);
	}
}