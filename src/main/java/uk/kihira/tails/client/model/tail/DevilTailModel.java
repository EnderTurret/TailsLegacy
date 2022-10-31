/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.List;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;

/**
 * The model for devil tails.
 */
public final class DevilTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tail5;
	private final ModelPart tailTip;

	private final PartConfiguration config0;

	public DevilTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef
			.addOrReplaceChild("tailBase", CubeListBuilder.create()
					.texOffs(0, 0).addBox(-1, -1, 0, 2, 2, 2), PartPose.rotation(radf(-30), 0, 0))
			.addOrReplaceChild("tail1", CubeListBuilder.create()
					.texOffs(0, 4).addBox(-0.5F, -0.5F, 0, 1, 1, 4), PartPose.offsetAndRotation(0, 0, 1.8F, radf(-30), 0, 0))
			.addOrReplaceChild("tail2", CubeListBuilder.create()
					.texOffs(0, 9).addBox(-0.5F, -0.5F, 0, 1, 1, 5), PartPose.offsetAndRotation(0, 0, 3.8F, radf(-30), 0, 0))
			.addOrReplaceChild("tail3", CubeListBuilder.create()
					.texOffs(0, 15).addBox(-0.5F, -0.5F, 0, 1, 1, 3), PartPose.offsetAndRotation(0, 0, 4.8F, radf(20), 0, 0))
			.addOrReplaceChild("tail4", CubeListBuilder.create()
					.texOffs(0, 19).addBox(-0.5F, -0.5F, 0, 1, 1, 2), PartPose.offsetAndRotation(0, 0, 2.6F, radf(50), 0, 0))
			.addOrReplaceChild("tail5", CubeListBuilder.create()
					.texOffs(0, 22).addBox(-0.5F, -0.5F, 0, 1, 1, 2), PartPose.offsetAndRotation(0, 0, 1.7F, radf(50), 0, 0))
			.addOrReplaceChild("tailTip", CubeListBuilder.create()
					.texOffs(12, 0).addBox(-2.5F, 0, 0, 5, 5, 0), PartPose.offsetAndRotation(0, 0, 1.8F, radf(120), 0, 0));

		root = rootDef.bake(32, 32);

		tailBase = root.getChild("tailBase");
		tail1 = tailBase.getChild("tail1");
		tail2 = tail1.getChild("tail2");
		tail3 = tail2.getChild("tail3");
		tail4 = tail3.getChild("tail4");
		tail5 = tail4.getChild("tail5");
		tailTip = tail5.getChild("tailTip");

		config = new PartConfiguration(tailBase, List.of(tailBase, tail1, tail2, tail3, tail4, tail5, tailTip));
		config0 = new PartConfiguration(tailBase, List.of(tailBase, tail1, tail2, tail3, tail4, tail5));
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, Part.SubType subType, float headPitch) {
		final float seed = getAnimationTime(6000, entity);
		final float xseed = getAnimationTime(12000, entity);
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);

				xAngleOffset = Mth.clamp(angles[0] / 3.5, -1, 0.275);
				yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = rad(13);
			yAngleMultiplier = 0.25;
		}

		setRotationRadians(tailBase, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 1) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail1, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 2) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail2, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 3) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail3, rad(20) - xAngleOffset * 2 + Mth.cos(xseed - 4) / 6 * yAngleMultiplier, Mth.cos(seed - 4) / 8 * yAngleMultiplier, Mth.cos(xseed - 4) / 8 * yAngleMultiplier);
		setRotationRadians(tail4, rad(50) - xAngleOffset * 3 + Mth.cos(xseed - 5) / 8 * yAngleMultiplier, Mth.cos(seed - 5) / 8 * yAngleMultiplier, Mth.cos(xseed - 5) / 8 * yAngleMultiplier);
		setRotationRadians(tail5, rad(50) - xAngleOffset * 4 + Mth.cos(xseed - 6) / 4  * yAngleMultiplier, Mth.cos(seed - 6) / 8 * yAngleMultiplier, Mth.cos(xseed - 6) / 8 * yAngleMultiplier);
		setRotationRadians(tailTip, rad(120) - xAngleOffset, 0, 0);
	}

	@Override
	public void render(RenderContext ctx) {
		tailTip.visible = !ctx.info().getSubType().id().equals("no_tip");

		ctx.render(tailBase);

		tailTip.visible = true;
	}

	@Override
	public List<PartConfiguration> getParts(ClientPartInfo info) {
		if (info.getSubType().id().equals("no_tip"))
			return List.of(config0);

		return super.getParts(info);
	}
}