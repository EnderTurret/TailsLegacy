/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.common.part.PartInfo;

/**
 * The model for the floofy tail everyone loves.
 */
public class FluffyTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tail5;

	public FluffyTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef
			.addOrReplaceChild("tailBase", CubeListBuilder.create()
					.texOffs(0, 0).addBox(-1, -1, 0, 2, 2, 3), PartPose.rotation(radf(-15), 0, 0))
			.addOrReplaceChild("tail1", CubeListBuilder.create()
					.texOffs(10, 0).addBox(-1.5F, -1.5F, 0, 3, 3, 2), PartPose.offsetAndRotation(0, 0, 1.5F, radf(-15), 0, 0))
			.addOrReplaceChild("tail2", CubeListBuilder.create()
					.texOffs(0, 5).addBox(-2, -2, 0, 4, 4, 4), PartPose.offsetAndRotation(0, 0, 1.5F, radf(-15), 0, 0))
			.addOrReplaceChild("tail3", CubeListBuilder.create()
					.texOffs(0, 13).addBox(-2.5F, -2.5F, 0, 5, 5, 8), PartPose.offsetAndRotation(0, 0, 3F, radf(-25), 0, 0))
			.addOrReplaceChild("tail4", CubeListBuilder.create()
					.texOffs(0, 26).addBox(-2, -2, 0, 4, 4, 2), PartPose.offsetAndRotation(0, 0, 7.4F, radf(15), 0, 0))
			.addOrReplaceChild("tail5", CubeListBuilder.create()
					.texOffs(12, 26).addBox(-1.5F, -1.5F, 0, 3, 3, 2), PartPose.offsetAndRotation(0, 0, 1.4F, radf(15), 0, 0));

		root = rootDef.bake(64, 32);
		tailBase = root.getChild("tailBase");
		tail1 = tailBase.getChild("tail1");
		tail2 = tail1.getChild("tail2");
		tail3 = tail2.getChild("tail3");
		tail4 = tail3.getChild("tail4");
		tail5 = tail4.getChild("tail5");

		final List<ModelPart> parts = List.of(tailBase, tail1, tail2, tail3, tail4, tail5);

		single = List.of(new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			setRotationAngles(0, getAnimationTime(4000F, entity), 1F, 1F, 0, 0, partialTick, entity);
			poseStack.mulPose(Vector3f.XP.rotationDegrees(-20F));
		}));

		twin = List.of(new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			setRotationAngles(1, getAnimationTime(4000F, entity), 1F, 1F, 0F, rad(40), partialTick, entity);
			poseStack.mulPose(Vector3f.XP.rotationDegrees(-20F));
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			poseStack.mulPose(Vector3f.XP.rotationDegrees(-20F));
			setRotationAngles(1, getAnimationTime(4000F, entity), 1.4F, 0F, 0F, rad(-40), partialTick, entity);
		}));

		nine = List.of(new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			setRotationAngles(2, getAnimationTime(6500F, entity), -1.5F, 2.5F, 0, 0, partialTick, entity);
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			setRotationAngles(2, getAnimationTime(6500F, entity), -1.3F, 1.6F, 0, rad(30), partialTick, entity);
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			setRotationAngles(2, getAnimationTime(6500F, entity), -1.1F, 0.7F, 0, rad(-30), partialTick, entity);
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			setRotationAngles(2, getAnimationTime(6500F, entity), -1.2F, 2.6F, rad(20), rad(-15), partialTick, entity);
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> { // 4
			setRotationAngles(2, getAnimationTime(6500F, entity), -0.9F, 1.1F, rad(20), rad(15), partialTick, entity);
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> { // 5
			setRotationAngles(2, getAnimationTime(6500F, entity), -0.8F, 2F, rad(20), rad(45), partialTick, entity);
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			setRotationAngles(2, getAnimationTime(6500F, entity), -1.25F, 0.6F, rad(20), rad(-45), partialTick, entity);
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> { // 7
			setRotationAngles(2, getAnimationTime(6500F, entity), -1.4F, 0.9F, rad(45), rad(15), partialTick, entity);
		}), new PartConfiguration(tailBase, parts, (info, poseStack, partialTick, entity) -> {
			setRotationAngles(2, getAnimationTime(6500F, entity), -1.1F, 1.6F, rad(45), rad(-15), partialTick, entity);
		}));
	}

	public void setRotationAngles(int subtype, float timestep, float yOffset, float xOffset, double xAngle, double yAngle, float partialTick, Entity entity) {
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
				// Fox Tail
				case 0 -> {
					xAngleOffset = Mth.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
					zAngleOffset = Mth.clamp(zAngleOffset, -0.5D, 0.5D);
				}
				// Twin Tails
				case 1 -> {
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

		setRotationRadians(tailBase, xAngle + xAngleOffset, (-zAngleOffset / 2F + yAngle + Mth.cos(timestep + yOffset) / 8F) * yAngleMultiplier + yAngleOffset, -zAngleOffset / 8F);
		setRotationRadians(tail1, -0.2617993877991494 + xAngleOffset + Math.abs(zAngleOffset / 2F), (-zAngleOffset / 2F + Mth.cos(timestep - 1 + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);
		setRotationRadians(tail2, -0.2617993877991494 + xAngleOffset / 2F, (-zAngleOffset / 2F + Mth.cos(timestep - 1.5F + yOffset) / 8F) * yAngleMultiplier, -zAngleOffset / 8F);
		setRotationRadians(tail3, -0.4363323129985824 + xAngleOffset / 2F, (-zAngleOffset / 2F + Mth.cos(timestep - 2 + yOffset) / 20F) * yAngleMultiplier, -zAngleOffset / 20F);
		setRotationRadians(tail4, 0.2617993877991494 - xAngleOffset / 2F, (-zAngleOffset / 2F + Mth.cos(timestep - 3 + yOffset) / 8F) * yAngleMultiplier, 0F);
		setRotationRadians(tail5, 0.2617993877991494 - xAngleOffset / 2.5F, (-zAngleOffset / 2F + Mth.cos(timestep - 4 + yOffset) / 8F) * yAngleMultiplier, 0F);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, int subtype, float partialTick) {
		float timestep = getAnimationTime(4000F, entity);

		if (subtype == 0) {
			setRotationAngles(0, timestep, 1F, 1F, 0, 0, partialTick, entity);
			poseStack.pushPose();
			poseStack.mulPose(Vector3f.XP.rotationDegrees(-20F));
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
		}
		else if (subtype == 1) {
			setRotationAngles(1, timestep, 1F, 1F, 0F, rad(40), partialTick, entity);
			poseStack.pushPose();
			poseStack.mulPose(Vector3f.XP.rotationDegrees(-20F));
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(1, timestep, 1.4F, 0F, 0F, rad(-40), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
		}
		else if (subtype == 2) {
			timestep = getAnimationTime(6500F, entity);

			setRotationAngles(2, timestep, -1.5F, 2.5F, 0, 0, partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(2, timestep, -1.3F, 1.6F, 0, rad(30), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(2, timestep, -1.1F, 0.7F, 0, rad(-30), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(2, timestep, -1.2F, 2.6F, rad(20), rad(-15), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(2, timestep, -0.9F, 1.1F, rad(20), rad(15), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(2, timestep, -0.8F, 2F, rad(20), rad(45), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(2, timestep, -1.25F, 0.6F, rad(20), rad(-45), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(2, timestep, -1.4F, 0.9F, rad(45), rad(15), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			setRotationAngles(2, timestep, -1.1F, 1.6F, rad(45), rad(-15), partialTick, entity);
			tailBase.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	private final List<PartConfiguration> single;

	private final List<PartConfiguration> twin;

	private final List<PartConfiguration> nine;

	@Override
	public List<PartConfiguration> getParts(PartInfo info) {
		final int subtype = info.getSubType();

		if (subtype == 0)
			return single;
		if (subtype == 1)
			return twin;
		if (subtype == 2)
			return nine;

		return List.of();
	}
}