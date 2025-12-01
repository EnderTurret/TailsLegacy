/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.model;

import java.util.List;

import net.minecraft.util.Mth;

import uk.kihira.tails.client.render.layer.TailsArrowLayer;
import uk.kihira.tails.common2.client.duck.TailsEntity;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.duck.TailsPoseStack;
import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.common2.client.part.Part;
import uk.kihira.tails.common2.client.part.Part.SubType;
import uk.kihira.tails.common2.client.part.PartPath;
import uk.kihira.tails.common2.client.render.RenderContext;
import uk.kihira.tails.common2.client.render.part.PartRenderer;

/**
 * A base class that all parts extend.
 */
public abstract class PartModel {

	protected PartModel() {
		super();
	}

	public static final float SCALE = 0.0625F;

	/**
	 * Renders the part model.
	 * @param ctx All the fun rendering objects.
	 */
	public void render(RenderContext ctx) {
		final Part part = ctx.info().getPart();
		final SubType subType = ctx.info().getSubType();

		final int len = subType.hideParts().size() + subType.showParts().size();
		final TailsModelPart[] changedParts = len == 0 ? null : new TailsModelPart[len];
		final boolean[] partStates = len == 0 ? null : new boolean[changedParts.length];

		if (len != 0) {
			int pos = 0;
			for (PartPath path : subType.hideParts()) {
				changedParts[pos] = path.traverse(ctx.getModel());
				partStates[pos] = changedParts[pos].t$isVisible();
				changedParts[pos].t$setVisible(false);
				pos++;
			}
			for (PartPath path : subType.showParts()) {
				changedParts[pos] = path.traverse(ctx.getModel());
				partStates[pos] = changedParts[pos].t$isVisible();
				changedParts[pos].t$setVisible(true);
				pos++;
			}
		}

		final boolean transformed = !part.getRenderTransforms().isEmpty() || !subType.renderTransforms().isEmpty();
		if (transformed) {
			ctx.poseStack().t$push();

			if (!part.getRenderTransforms().isEmpty())
				part.getRenderTransforms().apply(ctx.poseStack());

			if (!subType.renderTransforms().isEmpty())
				subType.renderTransforms().apply(ctx.poseStack());
		}

		ctx.render(part.getModel());

		if (transformed)
			ctx.poseStack().t$pop();

		for (int i = 0; i < len; i++)
			changedParts[i].t$setVisible(partStates[i]);
	}

	/**
	 * <p>Returns a list of "configurations" representing logical groupings of parts in this model.
	 * For example, the nine fluffy tail model returns a list consisting of each separate tail.
	 * Each configuration allows setting up a {@link TailsPoseStack} with the same state as would be in {@link #render(RenderContext)},
	 * meaning one doesn't have to simply guess or hard-code the location and rotation of each part.</p>
	 * <p>This is mainly useful if you want to select a part or cube and render things on it,
	 * such as what the {@link TailsArrowLayer} does.</p>
	 * @param info The part info.
	 * @return A list of part configurations.
	 */
	public List<PartConfiguration> collectParts(ClientPartInfo info) {
		return info.getPart().allowArrows() ? List.of(PartConfiguration.derive(info.getPart().getModel())) : List.of();
	}

	public void setupAnim(TailsEntity entity, float partialTick, Part.SubType subType, TailsModelPart model) {}

	/**
	 * Allows modifying the rendering of this part model in the part preview pane.
	 * In particular, allows for translating or rotating the part, so it doesn't clip with other parts or GUI components.
	 * @param ctx The rendering context.
	 * @param renderer The renderer wrapping this part model.
	 */
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.info().getPart().getRenderTransforms().apply(ctx.poseStack());
		ctx.info().getSubType().renderTransforms().apply(ctx.poseStack());
		ctx.info().getPart().getPreviewTransforms().apply(ctx.poseStack());
	}

	/**
	 * Sets the rotation on a model where the provided params are in radians
	 * @param model The model
	 * @param x The x angle
	 * @param y The y angle
	 * @param z The z angle
	 */
	protected static void setRotationRadians(TailsModelPart model, double x, double y, double z) {
		model.t$setXRot((float) x);
		model.t$setYRot((float) y);
		model.t$setZRot((float) z);
	}

	/**
	 * Sets the rotation on a model where the provided params are in degrees
	 * @param model The model
	 * @param x The x angle
	 * @param y The y angle
	 * @param z The z angle
	 */
	protected static void setRotationDegrees(TailsModelPart model, float x, float y, float z) {
		setRotationRadians(model, rad(x), rad(y), rad(z));
	}

	protected static double rad(double degrees) {
		return Math.toRadians(degrees);
	}

	protected static float radf(double degrees) {
		return (float) Math.toRadians(degrees);
	}

	public static float getAnimationTime(double cycleTime, TailsEntity entity) {
		// Returns between 0-360 in radians depending on far in the "cycle" we are.
		return (float) ((entity.hashCode() + System.currentTimeMillis()) % cycleTime / cycleTime * 2 * Math.PI);
	}

	protected static double[] getMotionAngles(TailsEntity player, float partialTick) {
		// TODO: When falling a large distance, tails tend to move wildly up and down.
		// This seems to be caused by yCloakO and yCloak being set to Y when the difference between them is greater than 10.
		// See Player.moveCloak() for details.
		final double xMotion = Mth.lerp(partialTick, player.t$xCloakO(), player.t$xCloak()) - Mth.lerp(partialTick, player.t$xO(), player.t$x());
		final double yMotion = Mth.lerp(partialTick, player.t$yCloakO(), player.t$yCloak()) - Mth.lerp(partialTick, player.t$yO(), player.t$y()); // Positive when falling, negative when climbing
		final double zMotion = Mth.lerp(partialTick, player.t$zCloakO(), player.t$zCloak()) - Mth.lerp(partialTick, player.t$zO(), player.t$z());

		final float bodyYaw = Mth.rotLerp(partialTick, player.t$yBodyRotO(), player.t$yBodyRot());
		// Pretty sure renderYawOffset is actually the way the body is "pointing"
		// In degrees, not bound 0-360, be warned!
		final float bodyYawRads = radf(bodyYaw);
		final double bodyYawSin = Mth.sin(bodyYawRads);
		final double bodyYawCos = -Mth.cos(bodyYawRads);

		final float xOffset = Mth.clamp((float) yMotion * 10F, -6F, 32F);
		float forwardMotion = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
		forwardMotion = Mth.clamp(forwardMotion, 0, 150);
		float sideMotion = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;
		sideMotion = Mth.clamp(sideMotion, -20, 20);

		if (forwardMotion < 0F) forwardMotion = 0F;

		return new double[] {
				rad(forwardMotion / 2.5 + xOffset + getTailBob(player, partialTick)),
				rad(-sideMotion / 20),
				rad(sideMotion / 2)
		};
	}

	protected static float getTailBob(TailsEntity player, float partialTick) {
		final float cameraYaw = Mth.lerp(partialTick, player.t$bobO(), player.t$bob());
		return Mth.sin(Mth.lerp(partialTick, player.t$walkDistanceO(), player.t$walkDistance()) * 6) * 12 * cameraYaw;
	}
}
