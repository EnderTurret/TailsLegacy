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

package net.enderturret.tailslegacy.fabric.client;

import java.util.Objects;
import java.util.function.IntConsumer;

import org.joml.Matrix3x2f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import net.enderturret.tailslegacy.common.JavaColor;
import net.enderturret.tailslegacy.common.TailsMath;

/**
 * Various rendering-related utilities.
 */
public final class RenderHelper {

	// Blits a texture 'scaled' to fit a larger/smaller area.
	public static void blitScaled(GuiGraphicsExtractor gui, Identifier texture, int x, int y, int blitOffset, int u, int v, int uWidth, int vHeight, int width, int height, int color) {
		final AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);

		gui.submitGuiElementRenderState(new BlitRenderState(
				RenderPipelines.GUI_TEXTURED,
				TextureSetup.singleTexture(tex.getTextureView(), tex.getSampler()),
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
	 * Renders the given entity like in the {@linkplain InventoryScreen inventory screen}.
	 * @param gui The {@link GuiGraphicsExtractor}.
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
	public static void drawEntity(GuiGraphicsExtractor gui, int x1, int y1, int x2, int y2, int scale, float yaw, float pitch, float partialTick, LivingEntity entity) {
		final Quaternionf pose = new Quaternionf().rotateZ(TailsMath.PI);
		final Quaternionf cameraOrientation = new Quaternionf().rotateX(pitch * 20F * TailsMath.DEG_TO_RAD);
		pose.mul(cameraOrientation);

		pose.mul(new Quaternionf().rotateZ(TailsMath.PI));
		pose.mul(new Quaternionf().rotateY(yaw * TailsMath.DEG_TO_RAD));

		final EntityRenderState renderState = extractRenderState(entity);

		if (renderState instanceof LivingEntityRenderState living) {
			living.xRot = living.yRot = 0;
			living.bodyRot = 0;
		}

		if (renderState instanceof HumanoidRenderState humanoid)
			humanoid.isCrouching = false;

		gui.entity(renderState, scale, new Vector3f(0, entity.getBbHeight() / 2F, 0), pose, cameraOrientation, x1, y1, x2, y2);
	}

	private static EntityRenderState extractRenderState(LivingEntity entity) {
		final EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		final EntityRenderer<? super LivingEntity, ?> renderer = dispatcher.getRenderer(entity);
		final EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
		renderState.lightCoords = 15728880;
		renderState.shadowPieces.clear();
		renderState.outlineColor = 0;
		return renderState;
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
			action.accept(JavaColor.fromABGR(pixel, false));
		}, 0, (int) x, (int) y, 1, 1);
	}
}