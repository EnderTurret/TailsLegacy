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
import uk.kihira.tails.common.client.model.animation.ModelAnimator;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ModelPredicate;
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

	private final Map<TailsModelPart, Boolean> partVisibilities = new ConcurrentHashMap<>();

	/**
	 * Renders the part model.
	 * @param ctx All the fun rendering objects.
	 */
	public void render(RenderContext ctx) {
		final Part part = ctx.info().getPart();
		final SubType subType = ctx.info().getSubType();
		final ModelAnimator animation = part.getAnimation();

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
		return info.getPart().allowArrows() != ModelPredicate.FALSE
				? Collections.singletonList(PartConfiguration.derive(info.getPart().getModel(), info.getPart().allowArrows()))
				: Collections.emptyList();
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
}