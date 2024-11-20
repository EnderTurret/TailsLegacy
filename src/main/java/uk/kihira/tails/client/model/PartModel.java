/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
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
import uk.kihira.tails.client.render.layer.TailsArrowLayer;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * A base class that all parts extend.
 */
public abstract class PartModel extends EntityModel<LivingEntity> {

	protected PartModel() {
		super(RenderType::entityCutoutNoCull);
	}

	public static final float SCALE = 0.0625F;

	/**
	 * Renders the part model.
	 * @param ctx All the fun rendering objects.
	 */
	public void render(RenderContext ctx) {
		final Part part = ctx.info().getPart();
		final boolean transformed = !part.getRenderTransforms().isEmpty();
		if (transformed) {
			ctx.poseStack().pushPose();
			part.getRenderTransforms().apply(ctx.poseStack());
		}

		ctx.render(part.getModel());

		if (transformed)
			ctx.poseStack().popPose();
	}

	/**
	 * <p>Returns a list of "configurations" representing logical groupings of parts in this model.
	 * For example, the nine fluffy tail model returns a list consisting of each separate tail.
	 * Each configuration allows setting up a {@link PoseStack} with the same state as would be in {@link #render(RenderContext)},
	 * meaning one doesn't have to simply guess or hard-code the location and rotation of each part.</p>
	 * <p>This is mainly useful if you want to select a part or cube and render things on it,
	 * such as what the {@link TailsArrowLayer} does.</p>
	 * @param info The part info.
	 * @return A list of part configurations.
	 */
	public List<PartConfiguration> collectParts(ClientPartInfo info) {
		return info.getPart().allowArrows() ? List.of(PartConfiguration.derive(info.getPart().getModel())) : List.of();
	}

	/**
	 * @deprecated Use {@link #render(RenderContext)} instead.
	 */
	@Override
	@Deprecated
	public final void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

	/**
	 * @deprecated Use {@link #setupAnim(LivingEntity, float, float, float, float, uk.kihira.tails.client.part.Part.SubType, ModelPart)} instead.
	 */
	@Override
	@Deprecated
	public final void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float netHeadYaw, float headPitch) {}

	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, Part.SubType subType, ModelPart model) {}

	/**
	 * Allows modifying the rendering of this part model in the part preview pane.
	 * In particular, allows for translating or rotating the part, so it doesn't clip with other parts or GUI components.
	 * @param ctx The rendering context.
	 * @param renderer The renderer wrapping this part model.
	 */
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.info().getPart().getRenderTransforms().apply(ctx.poseStack());
		ctx.info().getPart().getPreviewTransforms().apply(ctx.poseStack());
	}

	/**
	 * Sets the rotation on a model where the provided params are in radians
	 * @param model The model
	 * @param x The x angle
	 * @param y The y angle
	 * @param z The z angle
	 */
	protected static void setRotationRadians(ModelPart model, double x, double y, double z) {
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
	protected static void setRotationDegrees(ModelPart model, float x, float y, float z) {
		setRotationRadians(model, rad(x), rad(y), rad(z));
	}

	protected static double rad(double degrees) {
		return Math.toRadians(degrees);
	}

	protected static float radf(double degrees) {
		return (float) Math.toRadians(degrees);
	}

	public static float getAnimationTime(double cycleTime, Entity entity) {
		// Returns between 0-360 in radians depending on far in the "cycle" we are.
		return (float) ((entity.hashCode() + System.currentTimeMillis()) % cycleTime / cycleTime * 2 * Math.PI);
	}

	protected static double[] getMotionAngles(Player player, float partialTick) {
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

	protected static float getTailBob(Player player, float partialTick) {
		final float cameraYaw = player.oBob + (player.bob - player.oBob) * partialTick;
		return Mth.sin((player.walkDistO + (player.walkDist - player.walkDistO) * partialTick) * 6) * 12 * cameraYaw;
	}
}
