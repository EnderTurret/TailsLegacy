/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.render.layer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.render.layer.BasePartLayer;
import net.enderturret.tailslegacy.fabric.client.render.RenderStates;

/**
 * A {@link RenderLayer} for Tails parts/accessories.
 * @param <S> The render state type.
 * @param <M> The model type.
 */
public class PartLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> implements BasePartLayer {

	/**
	 * @param renderer The renderer.
	 */
	public PartLayer(LivingEntityRenderer<?, S, M> renderer) {
		super(renderer);
	}

	@Override
	@Nullable
	public TailsModelPart attachmentPart(String attachmentRoot) {
		return switch (attachmentRoot) {
			case "head" -> (TailsModelPart) (Object) getParentModel().head;
			case "body" -> (TailsModelPart) (Object) getParentModel().body;
			default -> null;
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClientPartsData getPartsData(TailsEntity entity) {
		return ((S) entity).getRenderDataOrThrow(RenderStates.RENDER_DATA).partsData;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S renderState, float yRot, float xRot) {
		renderParts(
				(TailsEntity) renderState,
				(TailsPoseStack) poseStack,
				(TailsBufferSource) nodeCollector,
				renderState.partialTick,
				packedLight,
				LivingEntityRenderer.getOverlayCoords(renderState, 0F)
				);
	}
}