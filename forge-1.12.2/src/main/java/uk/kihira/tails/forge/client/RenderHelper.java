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

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

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
		final Minecraft mc = Minecraft.getMinecraft();
		final ScaledResolution res = new ScaledResolution(mc);

		final double scaleW = mc.displayWidth / res.getScaledWidth_double();
		final double scaleH = mc.displayHeight / res.getScaledHeight_double();
		final int screenHeight = mc.displayHeight;

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(
				(int) (x0 * scaleW),
				(int) (screenHeight - y1 * scaleH),
				(int) Math.max(0, (x1 - x0) * scaleW),
				(int) Math.max(0, (y1 - y0) * scaleH)); // Starts from lower left corner (minecraft starts from upper left)
	}

	/**
	 * Ends a {@linkplain GL11#glScissor(int, int, int, int) gl scissor}.
	 */
	public static void endGlScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public static void enableDefaultBlend() {
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	}

	// Blits a texture 'scaled' to fit a larger/smaller area.
	public static void blitScaled(int x, int y, int blitOffset, int u, int v, int uWidth, int vHeight, int width, int height) {
		final Tessellator tess = Tessellator.getInstance();
		final BufferBuilder renderer = tess.getBuffer();

		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		renderer.pos(x + 0,		y + height,	blitOffset).tex((u + 0) / 256F,		(v + vHeight) / 256F).endVertex();
		renderer.pos(x + width,	y + height,	blitOffset).tex((u + uWidth) / 256F,	(v + vHeight) / 256F).endVertex();
		renderer.pos(x + width,	y + 0,		blitOffset).tex((u + uWidth) / 256F,	(v + 0) / 256F).endVertex();
		renderer.pos(x + 0,		y + 0,		blitOffset).tex((u + 0) / 256F,		(v + 0) / 256F).endVertex();

		tess.draw();
	}

	/**
	 * Renders the given entity like in the inventory screen.
	 * @param x The x coordinate of the entity.
	 * @param y The y coordinate of the entity.
	 * @param scale The scale to render the entity at.
	 * @param yaw The yaw of the entity.
	 * @param pitch The pitch of the entity.
	 * @param partialTick The partial tick.
	 * @param entity The entity to render.
	 */
	public static void drawEntity(int x, int y, int scale, float yaw, float pitch, float partialTick, EntityLivingBase entity) {
		final float oldYBodyRot = entity.renderYawOffset;
		final float oldYRot = entity.rotationPitch;
		final float oldXRot = entity.rotationYaw;
		final float oldYHeadRot = entity.rotationYawHead;
		final float oldYHeadRotO = entity.prevRotationYawHead;

		entity.renderYawOffset = 0;
		entity.rotationPitch = 0;
		entity.rotationYaw = 0;
		entity.rotationYawHead = 0;
		entity.prevRotationYawHead = 0;
		entity.setSneaking(false);

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableColorMaterial();
		GlStateManager.enableDepth();
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, 100);
		GlStateManager.scale(-scale, scale, scale);

		GlStateManager.rotate(180F, 0, 0, 1);
		GlStateManager.rotate(pitch * 20F, 1, 0, 0);

		GlStateManager.rotate(180F, 0, 0, 1);
		GlStateManager.rotate(180 + yaw, 0, 1, 0);

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();

		final RenderManager rendererManager = Minecraft.getMinecraft().getRenderManager();

		rendererManager.setPlayerViewY(180F);
		rendererManager.setRenderShadow(false);

		rendererManager.renderEntity(entity, 0, 0, 0, 0F, 1F, false);

		rendererManager.setRenderShadow(true);

		GlStateManager.popMatrix();
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

		entity.renderYawOffset = oldYBodyRot;
		entity.rotationPitch = oldYRot;
		entity.rotationYaw = oldXRot;
		entity.rotationYawHead = oldYHeadRot;
		entity.prevRotationYawHead = oldYHeadRotO;
	}

	private static ByteBuffer pixelBuffer;

	public static int getColourAtPoint(double x, double y) {
		final Minecraft mc = Minecraft.getMinecraft();

		// We have to resolve these mouse coordinates back to window coordinates.
		final double scale = new ScaledResolution(mc).getScaleFactor();
		x *= scale;
		y *= scale;

		// We also have to flip the y coordinate because OpenGL's
		// coordinate system is upside-down compared to ours.
		y = mc.displayHeight - y;

		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		if (pixelBuffer == null) pixelBuffer = BufferUtils.createByteBuffer(3);

		GL11.glReadPixels((int) x, (int) y, 1, 1,
				GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE,
				pixelBuffer);

		pixelBuffer.rewind();

		final int r = pixelBuffer.get() & 0xFF;
		final int g = pixelBuffer.get() & 0xFF;
		final int b = pixelBuffer.get() & 0xFF;

		return (r << 16) | (g << 8) | b;
	}

	public static void drawScrollingString(FontRenderer font, String text, int minX, int maxX, int y, int color) {
		final int maxWidth = maxX - minX;
		final int textWidth = font.getStringWidth(text);
		if (textWidth <= maxWidth)
			font.drawString(text, minX, y, color);
		else
			drawCenteredScrollingString(font, text, (minX + maxX) / 2, minX, y, maxX, y + font.FONT_HEIGHT, color);
	}

	public static void drawCenteredScrollingString(FontRenderer font, String text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
		final int textWidth = font.getStringWidth(text);
		final int y = (minY + maxY - 9) / 2 + 1;
		final int width = maxX - minX;

		if (textWidth > width) {
			final int delta = textWidth - width;
			final double time = System.currentTimeMillis() / 1000.0;
			final double d1 = Math.max(delta * 0.5, 3.0);
			final double scrollProgress = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * time / d1)) / 2.0 + 0.5;
			final double scroll = MathHelper.clampedLerp(scrollProgress, 0, delta);

			startGlScissor(minX, minY, maxX, maxY);
			font.drawString(text, minX - (int)scroll, y, color);
			endGlScissor();

			return;
		}

		final int x = MathHelper.clamp(centerX, minX + textWidth / 2, maxX - textWidth / 2);
		font.drawString(text, x - textWidth / 2, y, color);
	}
}