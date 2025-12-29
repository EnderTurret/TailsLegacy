/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.model.body;

import static uk.kihira.tails.common.client.model.PartModelHelper.rad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.PartConfiguration;
import uk.kihira.tails.common.client.model.PartModel;
import uk.kihira.tails.common.client.model.PartModelHelper;
import uk.kihira.tails.common.client.model.animation.impl.FluffyTailAnimator;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.render.RenderContext;

/**
 * The fluffy tail part model.
 */
final class FluffyTailModel extends PartModel {

	@Override
	public void render(RenderContext ctx) {
		final TailsModelPart model = ctx.getModel();
		final FluffyTailAnimator anim = (FluffyTailAnimator) ctx.getAnimation();

		float timestep = PartModelHelper.getAnimationTime(4000, ctx.entity());

		switch (ctx.info().getSubType().id()) {
			case "one_tail":
				anim.setupAnim(ctx.entity(), 0, ctx.partialTick(), timestep, 1, 1, 0, 0);
				ctx.poseStack().t$push();
				ctx.poseStack().t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				ctx.render(model);
				ctx.poseStack().t$pop();
				break;
			case "two_tails":
				anim.setupAnim(ctx.entity(), 1, ctx.partialTick(), timestep, 1, 1, 0, rad(40));
				ctx.poseStack().t$push();
				ctx.poseStack().t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 1, ctx.partialTick(), timestep, 1.4F, 0, 0, rad(-40));
				ctx.render(model);
				ctx.poseStack().t$pop();
				break;
			case "three_tails":
				anim.setupAnim(ctx.entity(), 0, ctx.partialTick(), timestep, -1.5F, 2.5F, 0, 0);
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 0, ctx.partialTick(), timestep, -1.3F, 1.6F, 0, rad(45));
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 0, ctx.partialTick(), timestep, -1.1F, 0.7F, 0, rad(-45));
				ctx.render(model);
				break;
			case "nine_tails":
				timestep = PartModelHelper.getAnimationTime(6500, ctx.entity());

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -1.5F, 2.5F, 0, 0);
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -1.3F, 1.6F, 0, rad(30));
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -1.1F, 0.7F, 0, rad(-30));
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -1.2F, 2.6F, rad(20), rad(-15));
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -0.9F, 1.1F, rad(20), rad(15));
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -0.8F, 2F, rad(20), rad(45));
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -1.25F, 0.6F, rad(20), rad(-45));
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -1.4F, 0.9F, rad(45), rad(15));
				ctx.render(model);

				anim.setupAnim(ctx.entity(), 2, ctx.partialTick(), timestep, -1.1F, 1.6F, rad(45), rad(-15));
				ctx.render(model);
				break;
		}
	}

	@Override
	public List<PartConfiguration> collectParts(ClientPartInfo _info) {
		final PartConfiguration base = PartConfiguration.derive(_info.getPart().getModel(), _info.getPart().allowArrows());

		switch (_info.getSubTypeId()) {
			case "one_tail":
				return of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 0, partialTick, 1F, 1F, 0, 0);
					poseStack.t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				}));
			case "two_tails":
				return of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 1, partialTick, 1F, 1F, 0F, rad(40));
					poseStack.t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					poseStack.t$rotateX(-20F * TailsMath.DEG_TO_RAD);
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 1, partialTick, 1.4F, 0F, 0F, rad(-40));
				}));
			case "three_tails":
				return of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 0, partialTick, -1.5F, 2.5F, 0, 0);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 0, partialTick, -1.3F, 1.6F, 0, rad(45));
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 0, partialTick, -1.1F, 0.7F, 0, rad(-45));
				}));
			case "nine_tails":
				return of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -1.5F, 2.5F, 0, 0);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -1.3F, 1.6F, 0, rad(30));
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -1.1F, 0.7F, 0, rad(-30));
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -1.2F, 2.6F, rad(20), rad(-15));
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 4
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -0.9F, 1.1F, rad(20), rad(15));
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 5
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -0.8F, 2F, rad(20), rad(45));
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -1.25F, 0.6F, rad(20), rad(-45));
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 7
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -1.4F, 0.9F, rad(45), rad(15));
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					((FluffyTailAnimator) info.getPart().getAnimation()).setupAnim(entity, 2, partialTick, -1.1F, 1.6F, rad(45), rad(-15));
				}));
		}

		return Collections.emptyList();
	}

	@SafeVarargs
	private static <T> List<T> of(T... elements) {
		final List<T> ret = new ArrayList<>(elements.length);
		Collections.addAll(ret, elements);
		return ret;
	}
}