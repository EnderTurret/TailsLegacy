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

import java.util.Objects;
import java.util.function.IntConsumer;

import org.joml.Matrix3x2f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common.TailsMath;

/**
 * Various rendering-related utilities.
 */
public final class RenderHelper {

	/**
	 * Begins a {@linkplain GL11#glScissor(int, int, int, int) gl scissor} using the given <em>GUI</em> coordinates.
	 * These coordinates are converted automatically to <em>screen</em> coordinates for the scissor.
	 * @param gui The {@code GuiGraphics}.
	 * @param x The coordinate of the left side of the scissor.
	 * @param y The coordinate of the top side of the scissor.
	 * @param width The width of the scissor.
	 * @param height The height of the scissor.
	 */
	public static void startGlScissor(GuiGraphics gui, int x, int y, int width, int height) {
		final Window mc = Minecraft.getInstance().getWindow();

		final double scaleW = (double)mc.getScreenWidth() / mc.getGuiScaledWidth();
		final double scaleH = (double)mc.getScreenHeight() / mc.getGuiScaledHeight();

		gui.enableScissor((int)Math.floor(x * scaleW),
				(int) Math.floor(mc.getScreenHeight() - (y + height) * scaleH),
				(int) Math.floor((x + width) * scaleW) - (int) Math.floor(x * scaleW),
				(int) Math.floor(mc.getScreenHeight() - y * scaleH) - (int) Math.floor(mc.getScreenHeight() - (y + height) * scaleH)); // Starts from lower left corner (minecraft starts from upper left)
	}

	/**
	 * Ends a {@linkplain GL11#glScissor(int, int, int, int) gl scissor}.
	 * @param gui The {@code GuiGraphics}.
	 */
	public static void endGlScissor(GuiGraphics gui) {
		gui.disableScissor();
	}

	// Blits a texture 'scaled' to fit a larger/smaller area.
	public static void blitScaled(GuiGraphics gui, ResourceLocation texture, int x, int y, int blitOffset, int u, int v, int uWidth, int vHeight, int width, int height, int color) {
		final GpuTextureView tex = Minecraft.getInstance().getTextureManager().getTexture(texture).getTextureView();

		gui.submitGuiElementRenderState(new BlitRenderState(
				RenderPipelines.GUI_TEXTURED,
				TextureSetup.singleTexture(tex),
				new Matrix3x2f(gui.pose()),
				x,
				y,
				x + width,
				y + height,
				u / 256F,
				(u + uWidth) / 256F,
				v / 256F,
				(v + vHeight) / 256F,
				color,
				gui.peekScissorStack()
				));
	}

	/**
	 * Renders the given entity like in the {@linkplain InventoryScreen#renderEntityInInventory(GuiGraphics, int, int, int, int, float, Vector3f, Quaternionf, Quaternionf, LivingEntity) inventory screen}.
	 * @param gui The {@link GuiGraphics}.
	 * @param x1 The x coordinate of the entity.
	 * @param y1 The y coordinate of the entity.
	 * @param x2 The x coordinate of the entity.
	 * @param y2 The y coordinate of the entity.
	 * @param scale The scale to render the entity at.
	 * @param yaw The yaw of the entity.
	 * @param pitch The pitch of the entity.
	 * @param partialTick The partial tick.
	 * @param entity The entity to render.
	 */
	public static void drawEntity(GuiGraphics gui, int x1, int y1, int x2, int y2, int scale, float yaw, float pitch, float partialTick, LivingEntity entity) {
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

		InventoryScreen.renderEntityInInventory(gui, x1, y1, x2, y2, scale,
				new Vector3f(0, entity.getBbHeight() / 2F, 0), pose, cameraOrientation, entity);

		entity.yBodyRot = oldYBodyRot;
		entity.setYRot(oldYRot);
		entity.setXRot(oldXRot);
		entity.yHeadRot = oldYHeadRot;
		entity.yHeadRotO = oldYHeadRotO;
	}

	public static void getColourAtPoint(double x, double y, IntConsumer action) {
		final Minecraft mc = Minecraft.getInstance();
		final RenderTarget renderTarget = mc.getMainRenderTarget();

		// We have to resolve these mouse coordinates back to window coordinates.
		final double scale = mc.getWindow().getGuiScale();
		x *= scale;
		y *= scale;

		// We also have to flip the y coordinate because OpenGL's
		// coordinate system is upside-down compared to ours.
		y = mc.getWindow().getHeight() - y;

		final GpuTexture colorTexture = renderTarget.getColorTexture();
		Objects.requireNonNull(colorTexture);

		final GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Tails pixel buffer", 9, colorTexture.getFormat().pixelSize());
		final CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
		RenderSystem.getDevice()
		.createCommandEncoder()
		.copyTextureToBuffer(colorTexture, buffer, 0, () -> {
			int pixel = 0;
			try (GpuBuffer.MappedView view = encoder.mapBuffer(buffer, true, false)) {
				pixel = view.data().getInt();
			}

			buffer.close();
			action.accept(pixel);
		}, 0, (int) x, (int) y, 1, 1);
	}
}