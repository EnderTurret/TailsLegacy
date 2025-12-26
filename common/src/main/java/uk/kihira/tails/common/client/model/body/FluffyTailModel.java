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
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.render.RenderContext;

/**
 * The fluffy tail part model.
 */
final class FluffyTailModel extends PartModel {

	public void setRotationAngles(int subtype, float timestep, float yOffset, float xOffset, double xAngle, double yAngle, float partialTick, TailsEntity entity,
			@Nullable TailsModelPart tailBase, @Nullable TailsModelPart tail1, @Nullable TailsModelPart tail2, @Nullable TailsModelPart tail3,
			@Nullable TailsModelPart tail4, @Nullable TailsModelPart tail5) {
		double xAngleOffset = 0;
		double yAngleOffset = 0;
		double zAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running
		if (entity.t$isPassenger()) {
			switch (subtype) {
				// Fox Tail
				case 0:
					xAngleOffset = PartModelHelper.rad(22);
					yAngleMultiplier = 0.5F;
					break;
				// Twin Tails
				case 1:
					xAngleOffset = PartModelHelper.rad(20);
					yAngleMultiplier = 0.5F;
					break;
				// Nine Tails
				case 2:
					xAngleOffset = PartModelHelper.rad(15);
					yAngleMultiplier = 0.75F;
					break;
			}
		} else {
			final double[] angles = PartModelHelper.getMotionAngles(entity, partialTick);
			xAngleOffset = angles[0];
			yAngleOffset = angles[1];
			zAngleOffset = angles[2];

			switch (subtype) {
				// Fox Tail; Twin Tails
				case 0:
				case 1:
					xAngleOffset = TailsMath.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
					zAngleOffset = TailsMath.clamp(zAngleOffset, -0.5D, 0.5D);
					break;
				// Nine tails
				case 2:
					zAngleOffset = TailsMath.clamp(zAngleOffset * 0.5D, -1D, 0.5D);
					xAngleOffset = TailsMath.clamp(xAngleOffset * 0.25D, -1D, 0.2D);
					xAngleOffset += TailsMath.cos(timestep + xOffset) / 30F;
					break;
			}
			yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running
		}

		if (tailBase == null) return;

		if (tail1 == null) tail1 = tailBase.t$getChild("tail1");
		if (tail2 == null) tail2 = tail1.t$getChild("tail2");
		if (tail3 == null) tail3 = tail2.t$getChild("tail3");
		if (tail4 == null) tail4 = tail3.t$getChild("tail4");
		if (tail5 == null) tail5 = tail4.t$getChild("tail5");

		tailBase.t$setRotationRadians(xAngle + xAngleOffset,                      (yAngle + -zAngleOffset / 2F + TailsMath.cos(timestep + yOffset) / 8F)        * yAngleMultiplier + yAngleOffset, zAngleOffset / -8F);
		tail1.t$setOffsetRotationRadians(      xAngleOffset + Math.abs(zAngleOffset / 2F), (-zAngleOffset / 2F + TailsMath.cos(timestep - 1 + yOffset) / 8F)    * yAngleMultiplier,                zAngleOffset / -8F);
		tail2.t$setOffsetRotationRadians(      xAngleOffset / 2F,                          (-zAngleOffset / 2F + TailsMath.cos(timestep - 1.5F + yOffset) / 8F) * yAngleMultiplier,                zAngleOffset / -8F);
		tail3.t$setOffsetRotationRadians(      xAngleOffset / 2F,                          (-zAngleOffset / 2F + TailsMath.cos(timestep - 2 + yOffset) / 20F)   * yAngleMultiplier,                zAngleOffset / -20F);
		tail4.t$setOffsetRotationRadians(      xAngleOffset / -2F,                         (-zAngleOffset / 2F + TailsMath.cos(timestep - 3 + yOffset) / 8F)    * yAngleMultiplier,                0F);
		tail5.t$setOffsetRotationRadians(      xAngleOffset / -2.5F,                       (-zAngleOffset / 2F + TailsMath.cos(timestep - 4 + yOffset) / 8F)    * yAngleMultiplier,                0F);
	}

	@Override
	public void render(RenderContext ctx) {
		final TailsModelPart model = ctx.getModel();
		final TailsModelPart tailBase = model.t$getChild("tailBase");
		final TailsModelPart tail1 = tailBase.t$getChild("tail1");
		final TailsModelPart tail2 = tail1.t$getChild("tail2");
		final TailsModelPart tail3 = tail2.t$getChild("tail3");
		final TailsModelPart tail4 = tail3.t$getChild("tail4");
		final TailsModelPart tail5 = tail4.t$getChild("tail5");

		float timestep = PartModelHelper.getAnimationTime(4000, ctx.entity());

		switch (ctx.info().getSubType().id()) {
			case "one_tail":
				setRotationAngles(0, timestep, 1, 1, 0, 0, ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.poseStack().t$push();
				ctx.poseStack().t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				ctx.render(model);
				ctx.poseStack().t$pop();
				break;
			case "two_tails":
				setRotationAngles(1, timestep, 1, 1, 0, rad(40), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.poseStack().t$push();
				ctx.poseStack().t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				ctx.render(model);

				setRotationAngles(1, timestep, 1.4F, 0, 0, rad(-40), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);
				ctx.poseStack().t$pop();
				break;
			case "three_tails":
				setRotationAngles(0, timestep, -1.5F, 2.5F, 0, 0, ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(0, timestep, -1.3F, 1.6F, 0, rad(45), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(0, timestep, -1.1F, 0.7F, 0, rad(-45), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);
				break;
			case "nine_tails":
				timestep = PartModelHelper.getAnimationTime(6500, ctx.entity());

				setRotationAngles(2, timestep, -1.5F, 2.5F, 0, 0, ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(2, timestep, -1.3F, 1.6F, 0, rad(30), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(2, timestep, -1.1F, 0.7F, 0, rad(-30), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(2, timestep, -1.2F, 2.6F, rad(20), rad(-15), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(2, timestep, -0.9F, 1.1F, rad(20), rad(15), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(2, timestep, -0.8F, 2F, rad(20), rad(45), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(2, timestep, -1.25F, 0.6F, rad(20), rad(-45), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(2, timestep, -1.4F, 0.9F, rad(45), rad(15), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);

				setRotationAngles(2, timestep, -1.1F, 1.6F, rad(45), rad(-15), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
				ctx.render(model);
				break;
		}
	}

	@Override
	public List<PartConfiguration> collectParts(ClientPartInfo _info) {
		final PartConfiguration base = PartConfiguration.derive(_info.getPart().getModel());

		switch (_info.getSubTypeId()) {
			case "one_tail":
				return of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(0, PartModelHelper.getAnimationTime(4000, entity), 1F, 1F, 0, 0, partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
					poseStack.t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				}));
			case "two_tails":
				return of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(1, PartModelHelper.getAnimationTime(4000, entity), 1F, 1F, 0F, rad(40), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
					poseStack.t$rotateX(-20F * TailsMath.DEG_TO_RAD);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					poseStack.t$rotateX(-20F * TailsMath.DEG_TO_RAD);
					setRotationAngles(1, PartModelHelper.getAnimationTime(4000, entity), 1.4F, 0F, 0F, rad(-40), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}));
			case "three_tails":
				return of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(0, PartModelHelper.getAnimationTime(4000, entity), -1.5F, 2.5F, 0, 0, partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(0, PartModelHelper.getAnimationTime(4000, entity), -1.3F, 1.6F, 0, rad(45), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(0, PartModelHelper.getAnimationTime(4000, entity), -1.1F, 0.7F, 0, rad(-45), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}));
			case "nine_tails":
				return of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -1.5F, 2.5F, 0, 0, partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -1.3F, 1.6F, 0, rad(30), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -1.1F, 0.7F, 0, rad(-30), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -1.2F, 2.6F, rad(20), rad(-15), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 4
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -0.9F, 1.1F, rad(20), rad(15), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 5
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -0.8F, 2F, rad(20), rad(45), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -1.25F, 0.6F, rad(20), rad(-45), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 7
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -1.4F, 0.9F, rad(45), rad(15), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
				}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
					setRotationAngles(2, PartModelHelper.getAnimationTime(6500, entity), -1.1F, 1.6F, rad(45), rad(-15), partialTick, entity, info.getPart().getModel().t$getChild("tailBase"), null, null, null, null, null);
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