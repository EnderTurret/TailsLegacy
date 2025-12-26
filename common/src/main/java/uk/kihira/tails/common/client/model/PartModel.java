/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.common.client.part.Part.SubType;
import uk.kihira.tails.common.client.part.PartPath;
import uk.kihira.tails.common.client.render.RenderContext;
import uk.kihira.tails.common.client.render.part.PartRenderer;

/**
 * A base class that all parts extend.
 */
public abstract class PartModel {

	protected PartModel() {
		super();
	}

	public static final float SCALE = 0.0625F;

	private final Map<TailsModelPart, Boolean> partVisibilities = new ConcurrentHashMap<>();

	/**
	 * Renders the part model.
	 * @param ctx All the fun rendering objects.
	 */
	public void render(RenderContext ctx) {
		final Part part = ctx.info().getPart();
		final SubType subType = ctx.info().getSubType();

		for (Map.Entry<TailsModelPart, Boolean> entry : partVisibilities.entrySet())
			entry.getKey().t$setVisible(entry.getValue());

		for (PartPath path : subType.hideParts())
			setPartVisible(path.traverse(ctx.getModel()), false);
		for (PartPath path : subType.showParts())
			setPartVisible(path.traverse(ctx.getModel()), true);

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
	}

	private void setPartVisible(TailsModelPart part, boolean visible) {
		partVisibilities.putIfAbsent(part, part.t$isVisible());
		part.t$setVisible(visible);
	}

	/**
	 * <p>Returns a list of "configurations" representing logical groupings of parts in this model.
	 * For example, the nine fluffy tail model returns a list consisting of each separate tail.
	 * Each configuration allows setting up a {@link TailsPoseStack} with the same state as would be in {@link #render(RenderContext)},
	 * meaning one doesn't have to simply guess or hard-code the location and rotation of each part.</p>
	 * <p>This is mainly useful if you want to select a part or cube and render things on it,
	 * such as what the {@code TailsArrowLayer} does.</p>
	 * @param info The part info.
	 * @return A list of part configurations.
	 */
	public List<PartConfiguration> collectParts(ClientPartInfo info) {
		return info.getPart().allowArrows() ? Collections.singletonList(PartConfiguration.derive(info.getPart().getModel())) : Collections.emptyList();
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

	protected static void setOffsetRotationRadians(TailsModelPart model, double x, double y, double z) {
		model.t$setXRot(model.t$getInitialXRot() + (float) x);
		model.t$setYRot(model.t$getInitialYRot() + (float) y);
		model.t$setZRot(model.t$getInitialZRot() + (float) z);
	}

	protected static double rad(double degrees) {
		return Math.toRadians(degrees);
	}

	protected static float radf(double degrees) {
		return (float) Math.toRadians(degrees);
	}

	// Returns between 0-360 in radians depending on far in the "cycle" we are.
	public static float getAnimationTime(int cycleTime, TailsEntity entity) {
		final double dCycleTime = cycleTime;
		// Note: it's tempting to refactor this to use floats (it elides the cast), but this breaks the math completely.
		// Maybe modulus only works on ints and doubles, so it chooses the int version for floats?
		return (float) ((entity.t$uuid().hashCode() + System.currentTimeMillis()) % dCycleTime / dCycleTime * 2 * Math.PI);
	}

	protected static double[] getMotionAngles(TailsEntity player, float partialTick) {
		// TODO: When falling a large distance, tails tend to move wildly up and down.
		// This seems to be caused by yCloakO and yCloak being set to Y when the difference between them is greater than 10.
		// See Player.moveCloak() for details.
		final double xMotion = TailsMath.lerp(partialTick, player.t$xCloakO(), player.t$xCloak()) - TailsMath.lerp(partialTick, player.t$xO(), player.t$x());
		final double yMotion = TailsMath.lerp(partialTick, player.t$yCloakO(), player.t$yCloak()) - TailsMath.lerp(partialTick, player.t$yO(), player.t$y()); // Positive when falling, negative when climbing
		final double zMotion = TailsMath.lerp(partialTick, player.t$zCloakO(), player.t$zCloak()) - TailsMath.lerp(partialTick, player.t$zO(), player.t$z());

		final float bodyYaw = TailsMath.rotLerp(partialTick, player.t$yBodyRotO(), player.t$yBodyRot());
		// Pretty sure renderYawOffset is actually the way the body is "pointing"
		// In degrees, not bound 0-360, be warned!
		final float bodyYawRads = radf(bodyYaw);
		final double bodyYawSin = TailsMath.sin(bodyYawRads);
		final double bodyYawCos = -TailsMath.cos(bodyYawRads);

		final float xOffset = TailsMath.clamp((float) yMotion * 10F, -6F, 32F);
		float forwardMotion = (float)(xMotion * bodyYawSin + zMotion * bodyYawCos) * 100F;
		forwardMotion = TailsMath.clamp(forwardMotion, 0, 150);
		float sideMotion = (float)(xMotion * bodyYawCos - zMotion * bodyYawSin) * 100F;
		sideMotion = TailsMath.clamp(sideMotion, -20, 20);

		if (forwardMotion < 0F) forwardMotion = 0F;

		return new double[] {
				rad(forwardMotion / 2.5 + xOffset + getTailBob(player, partialTick)),
				rad(-sideMotion / 20),
				rad(sideMotion / 2)
		};
	}

	protected static float getTailBob(TailsEntity player, float partialTick) {
		final float cameraYaw = TailsMath.lerp(partialTick, player.t$bobO(), player.t$bob());
		return TailsMath.sin(TailsMath.lerp(partialTick, player.t$walkDistanceO(), player.t$walkDistance()) * 6) * 12 * cameraYaw;
	}
}
