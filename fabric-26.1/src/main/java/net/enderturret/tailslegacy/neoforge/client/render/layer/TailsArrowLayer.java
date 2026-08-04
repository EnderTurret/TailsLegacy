/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.client.render.layer;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;

import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.duck.TailsRandomSource;
import net.enderturret.tailslegacy.common.client.model.PartConfiguration;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.render.layer.BaseArrowLayer;
import net.enderturret.tailslegacy.neoforge.client.render.RenderStates;
import net.enderturret.tailslegacy.neoforge.common.platform.TailsRandomSourceImpl;
import net.enderturret.tailslegacy.neoforge.mixin.client.StuckInBodyLayerAccess;

/**
 * A specialized {@link ArrowLayer} for rendering arrows on Tails parts/accessories in addition to normal body parts.
 *
 * @author EnderTurret
 *
 * @param <M>
 */
@Internal
public final class TailsArrowLayer<M extends PlayerModel> extends ArrowLayer<M> implements BaseArrowLayer {

	@Internal
	public TailsArrowLayer(LivingEntityRenderer<?, AvatarRenderState, M> renderer, EntityRendererProvider.Context context) {
		super(renderer, context);
	}

	@Override
	protected int numStuck(AvatarRenderState entity) {
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
	@Nullable
	public ClientPartsData getPartData(TailsEntity entity) {
		return ((AvatarRenderState) entity).getRenderDataOrThrow(RenderStates.RENDER_DATA).partsData;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot) {
		final int stuck = numStuck(renderState);
		if (stuck <= 0) return;

		final RandomSource rand = RandomSource.create(renderState.id);

		renderArrows(
				(TailsEntity) renderState,
				(TailsPoseStack) poseStack,
				(TailsBufferSource) nodeCollector,
				new TailsRandomSourceImpl(rand),
				stuck, renderState.partialTick, packedLight, OverlayTexture.NO_OVERLAY);
	}

	@Override
	public void renderStuckItem(TailsPoseStack poseStack, TailsBufferSource bufferSource, int packedLight, TailsEntity entity, float x, float y, float z, float partialTick) {
		final StuckInBodyLayerAccess access = (StuckInBodyLayerAccess) (Object) this;
		access.tails$submitStuckItem((PoseStack) poseStack, (SubmitNodeCollector) bufferSource, packedLight, x, y, z, ((AvatarRenderState) entity).outlineColor);
	}

	/**
	 * Represents a part configuration for a whole player.
	 * @author EnderTurret
	 */
	private static class Player extends PartConfiguration {

		private final PlayerModel model;

		/**
		 * @param model The model of the player.
		 */
		public Player(PlayerModel model) {
			super(List.of());
			this.model = model;
		}

		@Override
		public TailsModelPart randomPart(TailsRandomSource rand) {
			return (TailsModelPart) (Object) model.getRandomBodyPart((RandomSource) rand.t$unwrap());
		}
	}
}