/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.render.layer;

import java.util.Collections;
import java.util.Random;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.math.MathHelper;

import net.enderturret.tailslegacy.common.TailsMath;
import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.duck.TailsRandomSource;
import net.enderturret.tailslegacy.common.client.model.PartConfiguration;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.render.layer.BaseArrowLayer;
import net.enderturret.tailslegacy.forge.client.platform.TailsPoseStackImpl;
import net.enderturret.tailslegacy.forge.client.platform.TailsTessellatorWrapper;

/**
 * A specialized {@link LayerArrow} for rendering arrows on Tails parts/accessories in addition to normal body parts.
 *
 * @author EnderTurret
 */
@Internal
public final class TailsArrowLayer extends LayerArrow implements BaseArrowLayer {

	private final RenderLivingBase<?> renderer;

	private Entity arrowEntity;

	public TailsArrowLayer(RenderLivingBase<?> renderer) {
		super(renderer);
		this.renderer = renderer;
	}

	@Override
	public @Nullable TailsModelPart attachmentPart(String attachmentRoot) {
		switch (attachmentRoot) {
			case "body": return (TailsModelPart) ((ModelBiped) renderer.getMainModel()).bipedBody;
			case "head": return (TailsModelPart) ((ModelBiped) renderer.getMainModel()).bipedHead;
			default: return null;
		}
	}

	@Override
	public PartConfiguration makeRootConfig(ClientPartsData data, TailsEntity entity) {
		return new Player((ModelBiped) renderer.getMainModel());
	}

	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		final int stuck = entity.getArrowCountInEntity();
		if (stuck <= 0) return;

		final Random rand = new Random(entity.getEntityId());

		if (arrowEntity == null)
			arrowEntity = new EntityTippedArrow(entity.world, entity.posX, entity.posY, entity.posZ);

		arrowEntity.world = entity.world;
		arrowEntity.prevPosX = arrowEntity.posX = entity.posX;
		arrowEntity.prevPosY = arrowEntity.posY = entity.posY;
		arrowEntity.prevPosZ = arrowEntity.posZ = entity.posZ;

		final boolean isLightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
		if (isLightingEnabled) GL11.glDisable(GL11.GL_LIGHTING);

		renderArrows(
				(TailsEntity) entity,
				TailsPoseStackImpl.INSTANCE,
				TailsTessellatorWrapper.get(),
				new TailsRandomSource.Java(rand),
				stuck, partialTick, 1, 1);

		if (isLightingEnabled) GL11.glEnable(GL11.GL_LIGHTING);

		arrowEntity.world = null;
	}

	@Override
	public void renderStuckItem(TailsPoseStack poseStack, TailsBufferSource bufferSource, int packedLight, TailsEntity entity, float x, float y, float z, float partialTick) {
		float f6 = MathHelper.sqrt(x * x + z * z);
		arrowEntity.prevRotationYaw = arrowEntity.rotationYaw = (float) (Math.atan2(x, z) * TailsMath.RAD_TO_DEG);
		arrowEntity.prevRotationPitch = arrowEntity.rotationPitch = (float) (Math.atan2(y, f6) * TailsMath.RAD_TO_DEG);
		renderer.getRenderManager().renderEntity(arrowEntity, 0, 0, 0, 0F, partialTick, false);
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