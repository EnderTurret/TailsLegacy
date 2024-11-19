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

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.ModelSerializer;
import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;

/**
 * The cat tail part model. (Not to be confused with cattails.)
 */
final class CatTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tail5;

	public CatTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef
			.addOrReplaceChild("tailBase", CubeListBuilder.create()
					.texOffs(0, 0).addBox(-0.5F, -0.5F, 0, 1, 1, 2), PartPose.ZERO)
			.addOrReplaceChild("tail1", CubeListBuilder.create()
					.texOffs(0, 3).addBox(-0.5F, -0.5F, 0, 1, 1, 3), PartPose.offset(0, 0, 1.75F))
			.addOrReplaceChild("tail2", CubeListBuilder.create()
					.texOffs(0, 7).addBox(-0.5F, -0.5F, 0, 1, 1, 6), PartPose.offset(0, 0, 2.75F))
			.addOrReplaceChild("tail3", CubeListBuilder.create()
					.texOffs(0, 14).addBox(-0.5F, -0.5F, 0, 1, 1, 3), PartPose.offset(0, 0, 5.75F))
			.addOrReplaceChild("tail4", CubeListBuilder.create()
					.texOffs(0, 18).addBox(-0.5F, -0.5F, 0, 1, 1, 2), PartPose.offset(0, 0, 2.75F))
			.addOrReplaceChild("tail5", CubeListBuilder.create()
					.texOffs(0, 21).addBox(-0.5F, -0.5F, 0, 1, 1, 2), PartPose.offset(0, 0, 1.75F));

		root = ModelSerializer.bake(rootDef, 32, 32, "tail/cat_tail");

		tailBase = root.getChild("tailBase");
		tail1 = tailBase.getChild("tail1");
		tail2 = tail1.getChild("tail2");
		tail3 = tail2.getChild("tail3");
		tail4 = tail3.getChild("tail4");
		tail5 = tail4.getChild("tail5");

		config = new PartConfiguration(List.of(tailBase, tail1, tail2, tail3, tail4, tail5));
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

				xAngleOffset = Mth.clamp(angles[0] / 3.5, -1F, 0.33);
				yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
			}
		}
		else {
			xAngleOffset = rad(13);
			yAngleMultiplier = 0.25;
		}

		setRotationRadians(tailBase, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 1) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail1, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 2) / 8 * yAngleMultiplier, 0);
		setRotationRadians(tail2, rad(-30) + xAngleOffset * 2, Mth.cos(seed - 3) / 8 * yAngleMultiplier, Mth.cos(xseed - 3) / 16);
		setRotationRadians(tail3, rad(20) - xAngleOffset * 2 + Mth.cos(xseed - 4) / 8, Mth.cos(seed - 4) / 8 * yAngleMultiplier, Mth.cos(xseed - 4) / 8);
		setRotationRadians(tail4, rad(50) - xAngleOffset * 2.5 + Mth.cos(xseed - 5) / 10, Mth.cos(seed - 5) / 8 * yAngleMultiplier, Mth.cos(xseed - 5) / 8);
		setRotationRadians(tail5, rad(50) - xAngleOffset * 3 + Mth.cos(xseed - 6) / 10, Mth.cos(seed - 6) / 8 * yAngleMultiplier, Mth.cos(xseed - 6) / 8);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(tailBase);
	}
}
