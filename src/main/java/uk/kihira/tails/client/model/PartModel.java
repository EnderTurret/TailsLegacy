/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;

/**
 * A base class that all parts extend.
 */
public abstract class PartModel extends EntityModel<LivingEntity> {

	public PartModel() {
		super(RenderType::entityCutoutNoCull);
	}

	public static final float SCALE = 0.0625F;

	/**
	 * Renders the tail with the optional parts list provided
	 * @param matrixStackIn The {@link PoseStack} to use for transformations.
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
	public abstract void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks);

	@Override
	@Deprecated
	public final void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {}

	@Override
	public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float subtype, float headPitch) {}

	/**
	 * Sets the rotation on a model where the provided params are in radians
	 * @param model The model
	 * @param x The x angle
	 * @param y The y angle
	 * @param z The z angle
	 */
	protected void setRotationRadians(ModelPart model, double x, double y, double z) {
		model.xRot = (float) x;
		model.yRot = (float) y;
		model.zRot = (float) z;
	}

	/**
	 * Sets the rotation on a model where the provided params are in degrees
	 * @param model The model
	 * @param x The x angle
	 * @param y The y angle
	 * @param z The z angle
	 */
	protected void setRotationDegrees(ModelPart model, float x, float y, float z) {
		setRotationRadians(model, (float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
	}

	protected float rad(int degrees) {
		return (float) Math.toRadians(degrees);
	}

	public static float getAnimationTime(double cycleTime, Entity entity) {
		// Returns between 0-360 in radians depending on far in the "cycle" we are.
		return (float) ((entity.hashCode() + System.currentTimeMillis()) % cycleTime / cycleTime * 2F * Math.PI);
	}

	protected double[] getMotionAngles(Player player, double partialTicks) {
		final double xMotion = player.xCloakO + (player.xCloak - player.xCloakO) * partialTicks - (player.xo + (player.getX() - player.xo) * partialTicks);
		final double yMotion = player.yCloakO + (player.yCloak - player.yCloakO) * partialTicks - (player.yo + (player.getY() - player.yo) * partialTicks); // Positive when falling, negative when climbing
		final double zMotion = player.zCloakO + (player.zCloak - player.zCloakO) * partialTicks - (player.zo + (player.getZ() - player.zo) * partialTicks);
		final float bodyYaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * (float) partialTicks;
		// Pretty sure renderYawOffset is actually the way the body is "pointing"
		// In degrees, not bound 0-360, be warned!
		final double bodyYawSin = Mth.sin(bodyYaw * (float) Math.PI / 180F);
		final double bodyYawCos = -Mth.cos(bodyYaw * (float) Math.PI / 180F);
		final float xOffset = Mth.clamp((float) yMotion * 10F, -6F, 32F);
		float f1 = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
		final float f2 = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;

		if (f1 < 0F) f1 = 0F;

		return new double[] {Math.toRadians(f1 / 2.5F + (xOffset + getTailBob(player, (float) partialTicks))), Math.toRadians(-f2 / 20F), Math.toRadians(f2 / 2F)};
	}

	protected float getTailBob(Player player, float partialTicks) {
		final float cameraYaw = player.oBob + (player.bob - player.oBob) * partialTicks;
		return Mth.sin((player.walkDistO + (player.walkDist - player.walkDistO) * partialTicks) * 6F) * 12F * cameraYaw;
	}
}
