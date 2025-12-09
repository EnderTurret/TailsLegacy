/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.render.layer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.render.layer.BasePartLayer;
import uk.kihira.tails.forge.client.platform.TailsPoseStackImpl;

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
		renderParts(
				(TailsEntity) entity,
				TailsPoseStackImpl.INSTANCE,
				(TailsBufferSource) buffer,
				partialTick,
				1,
				1
				);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}