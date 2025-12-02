/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.render.layer;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common2.client.duck.TailsBufferSource;
import uk.kihira.tails.common2.client.duck.TailsEntity;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.duck.TailsPoseStack;
import uk.kihira.tails.common2.client.duck.TailsRandomSource;
import uk.kihira.tails.common2.client.model.PartConfiguration;
import uk.kihira.tails.common2.client.part.ClientPartsData;
import uk.kihira.tails.common2.client.render.layer.BaseArrowLayer;
import uk.kihira.tails.neoforge.common.TailsRandomSourceImpl;

/**
 * A specialized {@link ArrowLayer} for rendering arrows on Tails parts/accessories in addition to normal body parts.
 *
 * @author EnderTurret
 *
 * @param <T>
 * @param <M>
 */
@Internal
public final class TailsArrowLayer<T extends LivingEntity, M extends PlayerModel<T>> extends ArrowLayer<T, M> implements BaseArrowLayer {

	@Internal
	public TailsArrowLayer(EntityRendererProvider.Context context, LivingEntityRenderer<T, M> renderer) {
		super(context, renderer);
	}

	@Override
	protected int numStuck(T entity) {
		return super.numStuck(entity);
	}

	@Override
	public PartConfiguration makeRootConfig(ClientPartsData data, TailsEntity entity) {
		return new Player(getParentModel());
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		final int stuck = numStuck(entity);
		if (stuck <= 0) return;

		final RandomSource rand = RandomSource.create(entity.getId());

		renderArrows(
				(TailsEntity) entity,
				(TailsPoseStack) poseStack,
				(TailsBufferSource) buffer,
				new TailsRandomSourceImpl(rand),
				stuck, partialTick, packedLight, OverlayTexture.NO_OVERLAY);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void renderStuckItem(TailsPoseStack poseStack, TailsBufferSource bufferSource, int packedLight, TailsEntity entity, float x, float y, float z, float partialTick) {
		renderStuckItem((PoseStack) poseStack, (MultiBufferSource) bufferSource, packedLight, (T) entity, x, y, z, partialTick);
	}

	/**
	 * Represents a part configuration for a whole player.
	 * @author EnderTurret
	 */
	private static class Player extends PartConfiguration {

		private final PlayerModel<?> model;

		/**
		 * @param model The model of the player.
		 */
		public Player(PlayerModel<?> model) {
			super(List.of());
			this.model = model;
		}

		@Override
		public TailsModelPart randomPart(TailsRandomSource rand) {
			return (TailsModelPart) (Object) model.getRandomModelPart((RandomSource) rand.t$unwrap());
		}
	}
}