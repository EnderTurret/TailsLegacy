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

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;

/**
 * <p>The shark tail part model.</p>
 * <p>Model created by access_denied.</p>
 */
final class SharkTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart finBase;

	public SharkTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition finBaseDef = rootDef
			.addOrReplaceChild("tailBase", CubeListBuilder.create()
					.texOffs(0, 24).addBox(-2, -2, 0, 4, 4, 4), PartPose.offsetAndRotation(0, 0.5F, -0.6F, -0.6522295414702809F, 0.02949606435870417F, 0))
			.addOrReplaceChild("tail1", CubeListBuilder.create()
					.texOffs(0, 16).addBox(-1.5F, -1.5F, 0, 3, 3, 5), PartPose.offsetAndRotation(0, 0, 3.5F, 0.0013962634015954637F, 0, 0))
			.addOrReplaceChild("tail2", CubeListBuilder.create()
					.texOffs(0, 9).addBox(-1, -1, -0.2F, 2, 2, 5), PartPose.offsetAndRotation(0, 0, 4.5F, 0.278554548618295F, 0, 0))
			.addOrReplaceChild("tail3", CubeListBuilder.create()
					.texOffs(0, 3).addBox(-1, -1, 0, 2, 2, 4), PartPose.offsetAndRotation(0, 0, 4.4F, 0.22759093446006054F, 0, 0))
			.addOrReplaceChild("finBase", CubeListBuilder.create()
					.texOffs(16, 21).addBox(-0.5F, -0.4F, -4, 1, 7, 4), PartPose.offsetAndRotation(0, 0, 3, 2.5953045977155678F, 0, 0));

		finBaseDef
			.addOrReplaceChild("finTop1", CubeListBuilder.create()
					.texOffs(16, 10).addBox(-0.5F, 0, -2.9F, 1, 2, 3), PartPose.offsetAndRotation(0, 6.5F, -0.1F, -0.091106186954104F, 0, 0))
			.addOrReplaceChild("finTop2", CubeListBuilder.create()
					.texOffs(16, 4).addBox(0, 0, -2, 1, 4, 2), PartPose.offsetAndRotation(-0.5F, 2, 0.1F, -0.136659280431156F, 0, 0))
			.addOrReplaceChild("finTop3", CubeListBuilder.create()
					.texOffs(16, 1).addBox(0, 0, -1, 1, 2, 1), PartPose.offsetAndRotation(0, 4, 0, -0.136659280431156F, 0, 0));

		finBaseDef
			.addOrReplaceChild("finBot1", CubeListBuilder.create()
					.texOffs(26, 27).addBox(0, 0, -2, 1, 3, 2), PartPose.offsetAndRotation(-0.5F, -0.4F, -4, 0.091106186954104F, 0, 0))
			.addOrReplaceChild("finBot2", CubeListBuilder.create()
					.texOffs(26, 21).addBox(0, 0, -3, 1, 3, 3), PartPose.offsetAndRotation(0, 0, -2, 0.136659280431156F, 0, 0))
			.addOrReplaceChild("finBot3", CubeListBuilder.create()
					.texOffs(26, 17).addBox(0, 0, -2, 1, 2, 2), PartPose.offsetAndRotation(0, 0, -3, 0.1980948701013564F, 0, 0));

		root = rootDef.bake(64, 32);
		tailBase = root.getChild("tailBase");
		tail1 = tailBase.getChild("tail1");
		tail2 = tail1.getChild("tail2");
		tail3 = tail2.getChild("tail3");
		finBase = tail3.getChild("finBase");
		final ModelPart finTop1 = finBase.getChild("finTop1");
		final ModelPart finTop2 = finTop1.getChild("finTop2");
		final ModelPart finTop3 = finTop2.getChild("finTop3");
		final ModelPart finBot1 = finBase.getChild("finBot1");
		final ModelPart finBot2 = finBot1.getChild("finBot2");
		final ModelPart finBot3 = finBot2.getChild("finBot3");

		config = new PartConfiguration(List.of(tailBase, tail1, tail2, tail3, finBase, finTop1, finTop2, finTop3, finBot1, finBot2, finBot3))
				.setParents(finBot1, tailBase, tail1, tail2, tail3, finBase)
				.setParents(finBot2, tailBase, tail1, tail2, tail3, finBase, finBot1)
				.setParents(finBot3, tailBase, tail1, tail2, tail3, finBase, finBot1, finBot2);
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, Part.SubType subType, float headPitch) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);

				xAngleOffset = Mth.clamp(angles[0] / 5, -1, 0.45);
				yAngleMultiplier = 1 - xAngleOffset * 2; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = rad(12);
			yAngleMultiplier = 0.25;
		}

		final float timestep = getAnimationTime(3000D, entity);
		setRotationRadians(tailBase, -rad(37.37) + xAngleOffset * 4, Mth.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail1, rad(0.08) + xAngleOffset * 1, Mth.cos(timestep - 2) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail2, rad(15.96) - xAngleOffset * 2, Mth.cos(timestep - 3) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail3, rad(13.04) - xAngleOffset, Mth.cos(timestep - 4) / 5 * yAngleMultiplier, 0);
		setRotationRadians(finBase, rad(148.7), Mth.cos(timestep - 10) / 5 * yAngleMultiplier, 0);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(tailBase);
	}
}
