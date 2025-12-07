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

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.render.layer.BasePartLayer;

/**
 * A {@link RenderLayer} for Tails parts/accessories.
 * @param <T> The entity type.
 * @param <M> The model type.
 */
public class PartLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> implements BasePartLayer {

	/**
	 * @param renderer The renderer.
	 */
	public PartLayer(LivingEntityRenderer<T, M> renderer) {
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

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		renderParts(
				(TailsEntity) entity,
				(TailsPoseStack) poseStack,
				(TailsBufferSource) buffer,
				partialTick,
				packedLight,
				LivingEntityRenderer.getOverlayCoords(entity, 0F)
				);
	}
}