/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

/**
 * A base class that all parts extend.
 */
public abstract class PartModel extends EntityModel<LivingEntity> {

	public PartModel() {
		super(RenderType::getEntityCutoutNoCull);
	}

	public static final float SCALE = 0.0625F;

	/**
	 * Renders the tail with the optional parts list provided
	 * @param matrixStackIn The {@link MatrixStack} to use for transformations.
	 * @param bufferIn The buffer to draw to.
	 * @param entity The entity the part is attached to.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay.
	 * @param red The red color value.
	 * @param green The green color value.
	 * @param blue The blue color value.
	 * @param alpha The alpha color value.
	 * @param subtype The subtype.
	 * @param partialTicks The partial ticks.
	 */
	public abstract void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks);

	@Override
	@Deprecated
	public final void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {}

	@Override
	public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float subtype, float headPitch) {}

	/**
	 * Sets the rotation on a model where the provided params are in radians
	 * @param model The model
	 * @param x The x angle
	 * @param y The y angle
	 * @param z The z angle
	 */
	protected void setRotationRadians(ModelRenderer model, double x, double y, double z) {
		model.rotateAngleX = (float) x;
		model.rotateAngleY = (float) y;
		model.rotateAngleZ = (float) z;
	}

	/**
	 * Sets the rotation on a model where the provided params are in degrees
	 * @param model The model
	 * @param x The x angle
	 * @param y The y angle
	 * @param z The z angle
	 */
	protected void setRotationDegrees(ModelRenderer model, float x, float y, float z) {
		setRotationRadians(model, (float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
	}

	public static float getAnimationTime(double cycleTime, Entity entity) {
		// Returns between 0-360 in radians depending on far in the "cycle" we are.
		return (float) ((entity.hashCode() + System.currentTimeMillis()) % cycleTime / cycleTime * 2F * Math.PI);
	}

	protected double[] getMotionAngles(PlayerEntity player, double partialTicks) {
		final double xMotion = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * partialTicks - (player.prevPosX + (player.getPosX() - player.prevPosX) * partialTicks);
		final double yMotion = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * partialTicks - (player.prevPosY + (player.getPosY() - player.prevPosY) * partialTicks); // Positive when falling, negative when climbing
		final double zMotion = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * partialTicks - (player.prevPosZ + (player.getPosZ() - player.prevPosZ) * partialTicks);
		final float bodyYaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * (float) partialTicks;
		// Pretty sure renderYawOffset is actually the way the body is "pointing"
		// In degrees, not bound 0-360, be warned!
		final double bodyYawSin = MathHelper.sin(bodyYaw * (float) Math.PI / 180F);
		final double bodyYawCos = -MathHelper.cos(bodyYaw * (float) Math.PI / 180F);
		final float xOffset = MathHelper.clamp((float) yMotion * 10F, -6F, 32F);
		float f1 = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
		final float f2 = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;

		if (f1 < 0F) f1 = 0F;

		return new double[] {Math.toRadians(f1 / 2.5F + (xOffset + getTailBob(player, (float) partialTicks))), Math.toRadians(-f2 / 20F), Math.toRadians(f2 / 2F)};
	}

	protected float getTailBob(PlayerEntity player, float partialTicks) {
		final float cameraYaw = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
		return MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * partialTicks) * 6F) * 12F * cameraYaw;
	}
}
