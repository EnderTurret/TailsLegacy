/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.platform;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import net.enderturret.tailslegacy.common.JavaColor;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.duck.TailsBuffer;
import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.duck.TailsVertexConsumer;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack.Entry;

public final class TailsTessellatorWrapper implements TailsBufferSource, TailsBuffer, TailsVertexConsumer {

	private static TailsTessellatorWrapper instance;

	private final Tessellator tessellator = Tessellator.getInstance();
	private final BufferBuilder buffer = tessellator.getBuffer();

	private boolean renderingTransparent;

	public static TailsTessellatorWrapper get() {
		if (instance == null) instance = new TailsTessellatorWrapper();
		return instance;
	}

	@Override
	public TailsBuffer t$getEntityBuffer(TailsEntity entity, TResourceLocation texture) {
		final ResourceLocation tex = (ResourceLocation) texture;
		boolean visible = true, visibleToPlayer = false;

		if (!entity.t$isPreview() && entity.t$unwrap() instanceof EntityLivingBase) {
			final EntityLivingBase living = (EntityLivingBase) entity.t$unwrap();
			visible = !living.isInvisible();
			renderingTransparent = !visible && !living.isInvisibleToPlayer(Minecraft.getMinecraft().player);
		}

		if (!visible && !visibleToPlayer) return null;

		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);

		return this;
	}

	@Override
	public void t$submitCustomGeometry(TailsPoseStack poseStack, BiConsumer<Entry, TailsVertexConsumer> renderer) {
		if (renderingTransparent)
			GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

		renderer.accept(poseStack.t$lastEntry(), this);

		tessellator.draw();
		if (renderingTransparent)
			GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
	}

	@Override
	public void t$submitModelPart(TailsModelPart part, TailsPoseStack poseStack, int packedLight, int packedOverlay, int color) {
		if (renderingTransparent)
			GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

		GlStateManager.color(JavaColor.red(color), JavaColor.green(color), JavaColor.blue(color), JavaColor.alpha(color));

		((ModelRenderer) part).render(0.0625F);

		GlStateManager.color(1, 1, 1, 1);

		if (renderingTransparent)
			GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
	}

	@Override
	public TailsVertexConsumer t$beginVertex(Entry pose, float x, float y, float z) {
		buffer.pos(x, y, z);
		return this;
	}

	private int lastColor;

	@Override
	public TailsVertexConsumer t$color(int color) {
		// Older versions of Minecraft seem to be sensitive to the order of vertex data.
		// We emit color before UV normally, but 1.12 expects UV then color.
		// To fix this, we record the color and emit it after the UV below.
		lastColor = color;
		return this;
	}

	@Override
	public TailsVertexConsumer t$uv(float u, float v) {
		buffer.tex(u, v);

		buffer.color(JavaColor.red(lastColor), JavaColor.green(lastColor), JavaColor.blue(lastColor), JavaColor.alpha(lastColor));

		return this;
	}

	@Override
	public TailsVertexConsumer t$overlay(int overlay) {
		return this;
	}

	@Override
	public TailsVertexConsumer t$light(int light) {
		//buffer.lightmap(light >> 16 & 65535, light & 65535);
		return this;
	}

	@Override
	public TailsVertexConsumer t$normal(float x, float y, float z) {
		buffer.normal(x, y, z);
		return this;
	}

	@Override
	public TailsVertexConsumer t$normal(Entry pose, float x, float y, float z) {
		buffer.normal(x, y, z);
		return this;
	}

	@Override
	public TailsVertexConsumer t$endVertex() {
		buffer.endVertex();
		return this;
	}
}