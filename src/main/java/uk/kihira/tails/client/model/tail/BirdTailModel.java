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
 * <p>The bird tail part model.</p>
 * <p>Model created by blusunrize.</p>
 */
final class BirdTailModel extends PartModel {

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float headPitch, Part.SubType subType, ModelPart model) {
		final float timestep = getAnimationTime(8000, entity);
		double xAngleOffset = 0;
		double zAngleOffset = 0;

		if (entity.getVehicle() == null) {
			if (entity instanceof Player player) {
				final double[] angles = getMotionAngles(player, partialTick);
				xAngleOffset = angles[0];
				zAngleOffset = angles[2];

				xAngleOffset -= Mth.cos(timestep - 1) / 15F;
				zAngleOffset -= Mth.cos(timestep - 1) / 25F;
				xAngleOffset = Mth.clamp(xAngleOffset * 0.6D, -1D, 0.45D);
				zAngleOffset = Mth.clamp(zAngleOffset * 0.5D, -0.5D, 0.5D);
			}
		}
		// Mounted
		else
			xAngleOffset = rad(60);

		final ModelPart center = model.getChild("center");
		final ModelPart left0 = center.getChild("left0");
		final ModelPart left1 = left0.getChild("left1");
		final ModelPart left2 = left1.getChild("left2");
		final ModelPart right0 = center.getChild("right0");
		final ModelPart right1 = right0.getChild("right1");
		final ModelPart right2 = right1.getChild("right2");
		setRotationRadians(center, rad(50) + xAngleOffset, -zAngleOffset, 0);
		setRotationRadians(left0, rad(-2), rad(-5), rad(11) + xAngleOffset / 10);
		setRotationRadians(left1, rad(-2), rad(-7), xAngleOffset / 10);
		setRotationRadians(left2, rad(-2), rad(-10), rad(10) + xAngleOffset / 10);
		setRotationRadians(right0, rad(-2), rad(5), rad(-11) - xAngleOffset / 10);
		setRotationRadians(right1, rad(-2), rad(7), -xAngleOffset / 10);
		setRotationRadians(right2, rad(-2), rad(10), rad(-10) - xAngleOffset / 10);
	}
}