/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.common2.client.part.ClientPartInfo;

/**
 * The fluffy tail part model.
 */
final class FluffyTailModel extends PartModel {

	public void setRotationAngles(int subtype, float timestep, float yOffset, float xOffset, double xAngle, double yAngle, float partialTick, Entity entity,
			@Nullable ModelPart tailBase, @Nullable ModelPart tail1, @Nullable ModelPart tail2, @Nullable ModelPart tail3, @Nullable ModelPart tail4, @Nullable ModelPart tail5) {
		double xAngleOffset = 0;
		double yAngleOffset = 0;
		double zAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running
		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);
				xAngleOffset = angles[0];
				yAngleOffset = angles[1];
				zAngleOffset = angles[2];

				switch (subtype) {
				// Fox Tail; Twin Tails
				case 0, 1 -> {
					xAngleOffset = Mth.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
					zAngleOffset = Mth.clamp(zAngleOffset, -0.5D, 0.5D);
				}
				// Nine tails
				case 2 -> {
					zAngleOffset = Mth.clamp(zAngleOffset * 0.5D, -1D, 0.5D);
					xAngleOffset = Mth.clamp(xAngleOffset * 0.25D, -1D, 0.2D);
					xAngleOffset += Mth.cos(timestep + xOffset) / 30F;
				}
				}
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running
			}
		}
		// Mounted
		else
			switch (subtype) {
			// Fox Tail
			case 0 -> {
				xAngleOffset = rad(22);
				yAngleMultiplier = 0.5F;
			}
			// Twin Tails
			case 1 -> {
				xAngleOffset = rad(20);
				yAngleMultiplier = 0.5F;
			}
			// Nine Tails
			case 2 -> {
				xAngleOffset = rad(15);
				yAngleMultiplier = 0.75F;
			}
			}

		if (tailBase == null) return;

		setRotationRadians(tailBase, xAngle + xAngleOffset, (-zAngleOffset / 2F + yAngle + Mth.cos(timestep + yOffset) / 8F) * yAngleMultiplier + yAngleOffset, -zAngleOffset / 8F);

		if (tail1 == null) tail1 = tailBase.getChild("tail1");
		setRotationRadians(tail1, -0.2617993877991494 + xAngleOffset + Math.abs(zAngleOffset / 2F), (-zAngleOffset / 2F + Mth.cos(timestep - 1 + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);

		if (tail2 == null) tail2 = tail1.getChild("tail2");
		setRotationRadians(tail2, -0.2617993877991494 + xAngleOffset / 2F, (-zAngleOffset / 2F + Mth.cos(timestep - 1.5F + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);

		if (tail3 == null) tail3 = tail2.getChild("tail3");
		setRotationRadians(tail3, -0.4363323129985824 + xAngleOffset / 2F, (-zAngleOffset / 2F + Mth.cos(timestep - 2 + yOffset) / 20F) * yAngleMultiplier, -zAngleOffset / 20F);

		if (tail4 == null) tail4 = tail3.getChild("tail4");
		setRotationRadians(tail4, 0.2617993877991494 - xAngleOffset / 2F, (-zAngleOffset / 2F + Mth.cos(timestep - 3 + yOffset) / 8F) * yAngleMultiplier, 0F);

		if (tail5 == null) tail5 = tail4.getChild("tail5");
		setRotationRadians(tail5, 0.2617993877991494 - xAngleOffset / 2.5F, (-zAngleOffset / 2F + Mth.cos(timestep - 4 + yOffset) / 8F) * yAngleMultiplier, 0F);
	}

	@Override
	public void render(RenderContext ctx) {
		final ModelPart model = ctx.getModel();
		final ModelPart tailBase = model.getChild("tailBase");
		final ModelPart tail1 = tailBase.getChild("tail1");
		final ModelPart tail2 = tail1.getChild("tail2");
		final ModelPart tail3 = tail2.getChild("tail3");
		final ModelPart tail4 = tail3.getChild("tail4");
		final ModelPart tail5 = tail4.getChild("tail5");

		float timestep = getAnimationTime(4000F, ctx.entity());

		if ("one_tail".equals(ctx.info().getSubType().id())) {
			setRotationAngles(0, timestep, 1, 1, 0, 0, ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
			ctx.poseStack().pushPose();
			ctx.poseStack().mulPose(new Quaternionf().rotateX(-20F * Mth.DEG_TO_RAD));
			ctx.render(model);
			ctx.poseStack().popPose();
		}
		else if ("two_tails".equals(ctx.info().getSubType().id())) {
			setRotationAngles(1, timestep, 1, 1, 0, rad(40), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
			ctx.poseStack().pushPose();
			ctx.poseStack().mulPose(new Quaternionf().rotateX(-20F * Mth.DEG_TO_RAD));
			ctx.render(model);

			setRotationAngles(1, timestep, 1.4F, 0, 0, rad(-40), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
			ctx.render(model);
			ctx.poseStack().popPose();
		}
		else if ("three_tails".equals(ctx.info().getSubType().id())) {
			setRotationAngles(0, timestep, -1.5F, 2.5F, 0, 0, ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
			ctx.render(model);

			setRotationAngles(0, timestep, -1.3F, 1.6F, 0, rad(45), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
			ctx.render(model);

			setRotationAngles(0, timestep, -1.1F, 0.7F, 0, rad(-45), ctx.partialTick(), ctx.entity(), tailBase, tail1, tail2, tail3, tail4, tail5);
			ctx.render(model);
		}
		else if ("nine_tails".equals(ctx.info().getSubType().id())) {
			timestep = getAnimationTime(6500F, ctx.entity());

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
		}
	}

	@Override
	public List<PartConfiguration> collectParts(ClientPartInfo _info) {
		final PartConfiguration base = PartConfiguration.derive(_info.getPart().getModel());

		if ("one_tail".equals(_info.getSubTypeId()))
			return List.of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(0, getAnimationTime(4000F, entity), 1F, 1F, 0, 0, partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
				poseStack.mulPose(new Quaternionf().rotateX(-20F * Mth.DEG_TO_RAD));
			}));

		if ("two_tails".equals(_info.getSubTypeId()))
			return List.of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(1, getAnimationTime(4000F, entity), 1F, 1F, 0F, rad(40), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
				poseStack.mulPose(new Quaternionf().rotateX(-20F * Mth.DEG_TO_RAD));
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				poseStack.mulPose(new Quaternionf().rotateX(-20F * Mth.DEG_TO_RAD));
				setRotationAngles(1, getAnimationTime(4000F, entity), 1.4F, 0F, 0F, rad(-40), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}));

		if ("three_tails".equals(_info.getSubTypeId()))
			return List.of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(0, getAnimationTime(4000F, entity), -1.5F, 2.5F, 0, 0, partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(0, getAnimationTime(4000F, entity), -1.3F, 1.6F, 0, rad(45), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(0, getAnimationTime(4000F, entity), -1.1F, 0.7F, 0, rad(-45), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}));

		if ("nine_tails".equals(_info.getSubTypeId()))
			return List.of(base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(2, getAnimationTime(6500F, entity), -1.5F, 2.5F, 0, 0, partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(2, getAnimationTime(6500F, entity), -1.3F, 1.6F, 0, rad(30), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(2, getAnimationTime(6500F, entity), -1.1F, 0.7F, 0, rad(-30), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(2, getAnimationTime(6500F, entity), -1.2F, 2.6F, rad(20), rad(-15), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 4
				setRotationAngles(2, getAnimationTime(6500F, entity), -0.9F, 1.1F, rad(20), rad(15), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 5
				setRotationAngles(2, getAnimationTime(6500F, entity), -0.8F, 2F, rad(20), rad(45), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(2, getAnimationTime(6500F, entity), -1.25F, 0.6F, rad(20), rad(-45), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> { // 7
				setRotationAngles(2, getAnimationTime(6500F, entity), -1.4F, 0.9F, rad(45), rad(15), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}), base.copy().withTranslator((info, poseStack, partialTick, entity) -> {
				setRotationAngles(2, getAnimationTime(6500F, entity), -1.1F, 1.6F, rad(45), rad(-15), partialTick, entity, info.getPart().getModel().getChild("tailBase"), null, null, null, null, null);
			}));

		return List.of();
	}
}