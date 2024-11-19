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
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;

/**
 * <p>The dragon tail part model.</p>
 * <p>Model created by TTFTCUTS.</p>
 */
final class DragonTailModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, Part.SubType subType, ModelPart model) {
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

		final float timestep = getAnimationTime(4000, entity);

		final ModelPart tailBase = model.getChild("tailBase");
		final ModelPart tail1 = tailBase.getChild("tail1");
		final ModelPart tail2 = tail1.getChild("tail2");
		final ModelPart tail3 = tail2.getChild("tail3");
		setRotationRadians(tailBase, rad(-40) + xAngleOffset * 2, Mth.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail1, rad(-8) + xAngleOffset * 2, Mth.cos(timestep - 2) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail2, rad(10) - xAngleOffset / 4, Mth.cos(timestep - 3) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail3, rad(20) - xAngleOffset, Mth.cos(timestep - 4) / 5 * yAngleMultiplier, 0);

		final ModelPart tailSubBase = model.getChild("tailSubBase");
		final ModelPart tailSub1 = tailSubBase.getChild("tailSub1");
		final ModelPart tailSub2 = tailSub1.getChild("tailSub2");
		final ModelPart tailSub3 = tailSub2.getChild("tailSub3");
		if ("dragon_tail".equals(subType.id())) {
			tailSubBase.visible = true;
			setRotationRadians(tailSubBase, rad(-40) + xAngleOffset * 2, Mth.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
			setRotationRadians(tailSub1, rad(-8) + xAngleOffset * 2, Mth.cos(timestep - 2) / 5 * yAngleMultiplier, 0);
			setRotationRadians(tailSub2, rad(10) - xAngleOffset / 4, Mth.cos(timestep - 3) / 5 * yAngleMultiplier, 0);
			setRotationRadians(tailSub3, rad(20) - xAngleOffset, Mth.cos(timestep - 4) / 5 * yAngleMultiplier, 0);
		} else
			tailSubBase.visible = false;
	}
}