/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.render.layer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.render.layer.BasePartLayer;
import net.enderturret.tailslegacy.forge.client.platform.TailsPoseStackImpl;
import net.enderturret.tailslegacy.forge.client.platform.TailsTessellatorWrapper;

/**
 * A {@link LayerRenderer} for Tails parts/accessories.
 * @param <T> The entity type.
 */
public class PartLayer<T extends EntityLivingBase> implements LayerRenderer<T>, BasePartLayer {

	protected final RenderPlayer renderer;

	public PartLayer(RenderPlayer renderer) {
		this.renderer = renderer;
	}

	@Override
	@Nullable
	public TailsModelPart attachmentPart(String attachmentRoot) {
		switch (attachmentRoot) {
			case "head": return (TailsModelPart) renderer.getMainModel().bipedHead;
			case "body": return (TailsModelPart) renderer.getMainModel().bipedBody;
			default: return null;
		}
	}

	@Override
	public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		// Vanilla does a translate() call instead of posing the ModelRenderers here,
		// so we need to copy that here.
		final boolean crouching = entity instanceof EntityPlayer && entity.isSneaking();
		if (crouching) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.2F, 0);
		}

		renderParts(
				(TailsEntity) entity,
				TailsPoseStackImpl.INSTANCE,
				TailsTessellatorWrapper.get(),
				partialTick,
				1,
				1
				);

		if (crouching)
			GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}