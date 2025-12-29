/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.render.layer;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsRandomSource;
import uk.kihira.tails.common.client.model.PartConfiguration;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.render.layer.BaseArrowLayer;

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

	public TailsArrowLayer(EntityRenderDispatcher dispatcher, LivingEntityRenderer<T, M> renderer) {
		this(new EntityRendererProvider.Context(dispatcher, null, null, null, null), renderer);
	}

	@Override
	protected int numStuck(T entity) {
		return super.numStuck(entity);
	}

	@Override
	public @Nullable TailsModelPart attachmentPart(String attachmentRoot) {
		return switch (attachmentRoot) {
			case "head" -> (TailsModelPart) (Object) getParentModel().head;
			case "body" -> (TailsModelPart) (Object) getParentModel().body;
			default -> null;
		};
	}

	@Override
	public PartConfiguration makeRootConfig(ClientPartsData data, TailsEntity entity) {
		return new Player(getParentModel());
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		final int stuck = numStuck(entity);
		if (stuck <= 0) return;

		final Random rand = new Random(entity.getId());

		renderArrows(
				(TailsEntity) entity,
				(TailsPoseStack) poseStack,
				(TailsBufferSource) buffer,
				new TailsRandomSource.Java(rand),
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
			return (TailsModelPart) (Object) model.getRandomModelPart((Random) rand.t$unwrap());
		}
	}
}