/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.render.layer;

import java.util.Collections;
import java.util.Random;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsRandomSource;
import uk.kihira.tails.common.client.model.PartConfiguration;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.render.layer.BaseArrowLayer;
import uk.kihira.tails.forge.client.platform.TailsPoseStackImpl;
import uk.kihira.tails.forge.client.platform.TailsTessellatorWrapper;

/**
 * A specialized {@code LayerArrow} for rendering arrows on Tails parts/accessories in addition to normal body parts.
 *
 * @author EnderTurret
 */
@Internal
public final class TailsArrowLayer implements BaseArrowLayer {

	private final RendererLivingEntity renderer;

	private Entity arrowEntity;

	public TailsArrowLayer(RendererLivingEntity renderer) {
		this.renderer = renderer;
	}

	@Override
	public PartConfiguration makeRootConfig(ClientPartsData data, TailsEntity entity) {
		return new Player(((RenderPlayer) renderer).modelBipedMain);
	}

	public void doRenderLayer(EntityLivingBase entity, float partialTick) {
		final int stuck = entity.getArrowCountInEntity();
		if (stuck <= 0) return;

		final Random rand = new Random(entity.getEntityId());

		RenderHelper.disableStandardItemLighting();

		arrowEntity = new EntityArrow(entity.worldObj, entity.posX, entity.posY, entity.posZ);

		renderArrows(
				(TailsEntity) entity,
				TailsPoseStackImpl.INSTANCE,
				TailsTessellatorWrapper.get(),
				new TailsRandomSource.Java(rand),
				stuck, partialTick, 1, 1);

		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public void renderStuckItem(TailsPoseStack poseStack, TailsBufferSource bufferSource, int packedLight, TailsEntity entity, float x, float y, float z, float partialTick) {
		float f6 = MathHelper.sqrt_float(x * x + z * z);
		arrowEntity.prevRotationYaw = arrowEntity.rotationYaw = (float) (Math.atan2(x, z) * TailsMath.RAD_TO_DEG);
		arrowEntity.prevRotationPitch = arrowEntity.rotationPitch = (float) (Math.atan2(y, f6) * TailsMath.RAD_TO_DEG);
		RenderManager.instance.renderEntityWithPosYaw(arrowEntity, 0, 0, 0, 0F, partialTick);
	}

	/**
	 * Represents a part configuration for a whole player.
	 * @author EnderTurret
	 */
	private static class Player extends PartConfiguration {

		private final ModelBiped model;

		/**
		 * @param model The model of the player.
		 */
		public Player(ModelBiped model) {
			super(Collections.emptyList());
			this.model = model;
		}

		@Override
		public TailsModelPart randomPart(TailsRandomSource rand) {
			return (TailsModelPart) model.getRandomModelBox((Random) rand.t$unwrap());
		}
	}
}