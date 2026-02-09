/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.platform;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import uk.kihira.tails.common.JavaColor;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsPoseStack.Entry;
import uk.kihira.tails.common.client.duck.TailsVertexConsumer;
import uk.kihira.tails.forge.client.ClientEventHandler;

public final class TailsTessellatorWrapper implements TailsBufferSource, TailsBuffer, TailsVertexConsumer {

	private static TailsTessellatorWrapper instance;

	private final Tessellator tessellator = Tessellator.instance;

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
			renderingTransparent = !visible && !living.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
		}

		if (!visible && !visibleToPlayer) return null;

		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);

		return this;
	}

	@Override
	public void t$submitCustomGeometry(TailsPoseStack poseStack, BiConsumer<Entry, TailsVertexConsumer> renderer) {
		if (renderingTransparent)
			enableTransparentModel();
		tessellator.startDrawingQuads();

		renderer.accept(poseStack.t$lastEntry(), this);

		tessellator.draw();
		if (renderingTransparent)
			disableTransparentModel();
	}

	@Override
	public void t$submitModelPart(TailsModelPart part, TailsPoseStack poseStack, int packedLight, int packedOverlay, int color) {
		if (renderingTransparent)
			enableTransparentModel();

		float[] oldColors = null;
		if (color != 0xFFFFFFFF) {
			oldColors = ClientEventHandler.captureCurrentColor();
			GL11.glColor4f(JavaColor.red(color) / 255F * oldColors[0], JavaColor.green(color) / 255F * oldColors[1], JavaColor.blue(color) / 255F * oldColors[2], JavaColor.alpha(color) / 255F * oldColors[3]);
		}

		((ModelRenderer) part).render(0.0625F);

		if (oldColors != null) GL11.glColor4f(oldColors[0], oldColors[1], oldColors[2], oldColors[3]);

		if (renderingTransparent)
			disableTransparentModel();
	}

	private static void enableTransparentModel() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
	}

	private static void disableTransparentModel() {
		GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDepthMask(true);
	}

	private float x, y, z;
	private float nx, ny, nz;
	private float u, v;
	private int color;

	@Override
	public TailsVertexConsumer t$beginVertex(Entry pose, float x, float y, float z) {
		// Older versions of Minecraft seem to be sensitive to the order of vertex data.
		// We emit position, color, UV normally, but 1.7 expects position last.
		// To fix this, we record the values and emit it all in endVertex() below.
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public TailsVertexConsumer t$color(int color) {
		this.color = color;
		return this;
	}

	@Override
	public TailsVertexConsumer t$uv(float u, float v) {
		this.u = u;
		this.v = v;
		return this;
	}

	@Override
	public TailsVertexConsumer t$overlay(int overlay) {
		return this;
	}

	@Override
	public TailsVertexConsumer t$light(int light) {
		return this;
	}

	@Override
	public TailsVertexConsumer t$normal(float x, float y, float z) {
		nx = x;
		ny = y;
		nz = z;
		return this;
	}

	@Override
	public TailsVertexConsumer t$normal(Entry pose, float x, float y, float z) {
		nx = x;
		ny = y;
		nz = z;
		return this;
	}

	@Override
	public TailsVertexConsumer t$endVertex() {
		tessellator.setColorRGBA(JavaColor.red(color), JavaColor.green(color), JavaColor.blue(color), JavaColor.alpha(color));
		tessellator.setNormal(nx, ny, nz);
		tessellator.addVertexWithUV(x, y, z, u, v);
		return this;
	}
}