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

package uk.kihira.tails.neoforge.client;

import java.nio.ByteBuffer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common.TailsMath;

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

	// Blits a texture 'scaled' to fit a larger/smaller area.
	public static void blitScaled(GuiGraphics gui, int x, int y, int blitOffset, int u, int v, int uWidth, int vHeight, int width, int height) {
		final Matrix4f pose = gui.pose().last().pose();
		final Tesselator tess = Tesselator.getInstance();
		final BufferBuilder renderer = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		renderer.addVertex(pose, x + 0,		y + height,	blitOffset).setUv((u + 0) / 256F,		(v + vHeight) / 256F);
		renderer.addVertex(pose, x + width,	y + height,	blitOffset).setUv((u + uWidth) / 256F,	(v + vHeight) / 256F);
		renderer.addVertex(pose, x + width,	y + 0,		blitOffset).setUv((u + uWidth) / 256F,	(v + 0) / 256F);
		renderer.addVertex(pose, x + 0,		y + 0,		blitOffset).setUv((u + 0) / 256F,		(v + 0) / 256F);

		BufferUploader.drawWithShader(renderer.buildOrThrow());
	}

	/**
	 * Renders the given entity like in the {@linkplain InventoryScreen#renderEntityInInventory(GuiGraphics, float, float, float, Vector3f, Quaternionf, Quaternionf, LivingEntity) inventory screen}.
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

		InventoryScreen.renderEntityInInventory(gui, x, y, scale, new Vector3f(), pose, cameraOrientation, entity);

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
}