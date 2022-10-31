/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;

/**
 * A base class that all parts extend.
 */
public abstract class PartModel extends EntityModel<LivingEntity> {

	protected PartConfiguration config;

	public PartModel() {
		super(RenderType::entityCutoutNoCull);
	}

	public static final float SCALE = 0.0625F;

	/**
	 * Renders the part model.
	 * @param ctx All the fun rendering objects.
	 */
	public abstract void render(RenderContext ctx);

	public List<PartConfiguration> getParts(ClientPartInfo info) {
		return config == null ? List.of() : List.of(config);
	}

	@Override
	@Deprecated
	public final void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

	@Override
	@Deprecated
	public final void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float netHeadYaw, float headPitch) {}

	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, Part.SubType subType, float headPitch) {}

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
		setRotationRadians(model, rad(x), rad(y), rad(z));
	}

	protected double rad(double degrees) {
		return Math.toRadians(degrees);
	}

	protected float radf(double degrees) {
		return (float) Math.toRadians(degrees);
	}

	public static float getAnimationTime(double cycleTime, Entity entity) {
		// Returns between 0-360 in radians depending on far in the "cycle" we are.
		return (float) ((entity.hashCode() + System.currentTimeMillis()) % cycleTime / cycleTime * 2 * Math.PI);
	}

	protected double[] getMotionAngles(Player player, float partialTick) {
		// TODO: When falling a large distance, tails tend move wildly up and down.
		// This seems to be a problem with yo and yCloakO. Test with capes?
		final double yCloakO = player.yCloakO;
		final double yCloak = player.yCloak;
		final double yo = player.yo;
		final double y = player.getY();

		final double xMotion = player.xCloakO + (player.xCloak - player.xCloakO) * partialTick - (player.xo + (player.getX() - player.xo) * partialTick);
		final double yMotion = yCloakO + (yCloak - yCloakO) * partialTick
				- (yo + (y - yo) * partialTick); // Positive when falling, negative when climbing
		final double zMotion = player.zCloakO + (player.zCloak - player.zCloakO) * partialTick - (player.zo + (player.getZ() - player.zo) * partialTick);

		final float bodyYaw = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTick;
		// Pretty sure renderYawOffset is actually the way the body is "pointing"
		// In degrees, not bound 0-360, be warned!
		final float bodyYawRads = radf(bodyYaw);
		final double bodyYawSin = Mth.sin(bodyYawRads);
		final double bodyYawCos = -Mth.cos(bodyYawRads);

		final float xOffset = Mth.clamp((float) yMotion * 10F, -6F, 32F);
		float f1 = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
		final float f2 = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;

		if (f1 < 0F) f1 = 0F;

		return new double[] {rad(f1 / 2.5 + (xOffset + getTailBob(player, partialTick))), rad(-f2 / 20), rad(f2 / 2)};
	}

	protected float getTailBob(Player player, float partialTick) {
		final float cameraYaw = player.oBob + (player.bob - player.oBob) * partialTick;
		return Mth.sin((player.walkDistO + (player.walkDist - player.walkDistO) * partialTick) * 6) * 12 * cameraYaw;
	}
}
