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

import uk.kihira.tails.common.TailsMath;
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

		final TailsModelPart tailBase1 = model.t$getChild("tailBase1");
		final TailsModelPart tailBase2 = model.t$getChild("tailBase2");
		final TailsModelPart tailBase3 = model.t$getChild("tailBase3");

		for (TailsModelPart part : model.t$getChildren().values())
			part.t$setVisible(false);

		tailBase1.t$setVisible(true);

		float timestep = PartModelHelper.getAnimationTime(4000, ctx.entity());

		switch (ctx.info().getSubType().id()) {
			case "one_tail":
				anim.setupAnim(ctx.entity(), 1, 0, ctx.partialTick(), timestep, 1, 1, 0, 0);
				ctx.poseStack().t$push();
				ctx.poseStack().t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				ctx.render(tailBase1);
				ctx.poseStack().t$pop();
				break;
			case "two_tails":
				anim.setupAnim(ctx.entity(), 1, 1, ctx.partialTick(), timestep, 1, 1, 0, rad(40));
				ctx.poseStack().t$push();
				ctx.poseStack().t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				ctx.render(tailBase1);

				anim.setupAnim(ctx.entity(), 2, 1, ctx.partialTick(), timestep, 1.4F, 0, 0, rad(-40));
				tailBase2.t$setVisible(true);
				ctx.render(tailBase2);
				ctx.poseStack().t$pop();
				break;
			case "three_tails":
				anim.setupAnim(ctx.entity(), 1, 0, ctx.partialTick(), timestep, -1.5F, 2.5F, 0, 0);
				ctx.render(tailBase1);

				anim.setupAnim(ctx.entity(), 2, 0, ctx.partialTick(), timestep, -1.3F, 1.6F, 0, rad(45));
				tailBase2.t$setVisible(true);
				ctx.render(tailBase2);

				anim.setupAnim(ctx.entity(), 3, 0, ctx.partialTick(), timestep, -1.1F, 0.7F, 0, rad(-45));
				tailBase3.t$setVisible(true);
				ctx.render(tailBase3);
				break;
			case "nine_tails":
				final TailsModelPart tailBase4 = model.t$getChild("tailBase4");
				final TailsModelPart tailBase5 = model.t$getChild("tailBase5");
				final TailsModelPart tailBase6 = model.t$getChild("tailBase6");
				final TailsModelPart tailBase7 = model.t$getChild("tailBase7");
				final TailsModelPart tailBase8 = model.t$getChild("tailBase8");
				final TailsModelPart tailBase9 = model.t$getChild("tailBase9");

				timestep = PartModelHelper.getAnimationTime(6500, ctx.entity());

				anim.setupAnim(ctx.entity(), 1, 2, ctx.partialTick(), timestep, -1.5F, 2.5F, 0, 0);
				ctx.render(tailBase1);

				anim.setupAnim(ctx.entity(), 2, 2, ctx.partialTick(), timestep, -1.3F, 1.6F, 0, rad(30));
				tailBase2.t$setVisible(true);
				ctx.render(tailBase2);

				anim.setupAnim(ctx.entity(), 3, 2, ctx.partialTick(), timestep, -1.1F, 0.7F, 0, rad(-30));
				tailBase3.t$setVisible(true);
				ctx.render(tailBase3);

				anim.setupAnim(ctx.entity(), 4, 2, ctx.partialTick(), timestep, -1.2F, 2.6F, rad(20), rad(-15));
				tailBase4.t$setVisible(true);
				ctx.render(tailBase4);

				anim.setupAnim(ctx.entity(), 5, 2, ctx.partialTick(), timestep, -0.9F, 1.1F, rad(20), rad(15));
				tailBase5.t$setVisible(true);
				ctx.render(tailBase5);

				anim.setupAnim(ctx.entity(), 6, 2, ctx.partialTick(), timestep, -0.8F, 2F, rad(20), rad(45));
				tailBase6.t$setVisible(true);
				ctx.render(tailBase6);

				anim.setupAnim(ctx.entity(), 7, 2, ctx.partialTick(), timestep, -1.25F, 0.6F, rad(20), rad(-45));
				tailBase7.t$setVisible(true);
				ctx.render(tailBase7);

				anim.setupAnim(ctx.entity(), 8, 2, ctx.partialTick(), timestep, -1.4F, 0.9F, rad(45), rad(15));
				tailBase8.t$setVisible(true);
				ctx.render(tailBase8);

				anim.setupAnim(ctx.entity(), 9, 2, ctx.partialTick(), timestep, -1.1F, 1.6F, rad(45), rad(-15));
				tailBase9.t$setVisible(true);
				ctx.render(tailBase9);
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