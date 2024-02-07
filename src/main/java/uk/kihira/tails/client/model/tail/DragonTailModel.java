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
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;

/**
 * <p>The dragon tail part model.</p>
 * <p>Model created by TTFTCUTS.</p>
 */
final class DragonTailModel extends PartModel {

	private final ModelPart root;

	private final ModelPart tailBase;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;

	private final ModelPart tailSubBase;
	private final ModelPart tailSub1;
	private final ModelPart tailSub2;
	private final ModelPart tailSub3;

	private final PartConfiguration config0;

	public DragonTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef
			.addOrReplaceChild("tailBase", CubeListBuilder.create()
					.texOffs(22, 0).addBox(-2.5F, -2.5F, -2, 5, 5, 8), PartPose.rotation(radf(-40), 0, 0))
			.addOrReplaceChild("tail1", CubeListBuilder.create()
					.texOffs(0, 0).addBox(-2, -2, 0, 4, 4, 7), PartPose.offsetAndRotation(0, 0.3F, 5, radf(-8), 0, 0))
			.addOrReplaceChild("tail2", CubeListBuilder.create()
					.texOffs(0, 11).addBox(-1.5F, -1.5F, 0, 3, 3, 8), PartPose.offsetAndRotation(0, 0.2F, 5.5F, radf(10), 0, 0))
			.addOrReplaceChild("tail3", CubeListBuilder.create()
					.texOffs(0, 22).addBox(-1, -1, 0, 2, 2, 7), PartPose.offsetAndRotation(0, 0.4F, 7.5F, radf(20), 0, 0));

		rootDef
			.addOrReplaceChild("tailSubBase", CubeListBuilder.create()
					.texOffs(22, 5).addBox(0, -7.25F, -2, 0, 5, 8), PartPose.rotation(radf(-40), 0, 0))
			.addOrReplaceChild("tailSub1", CubeListBuilder.create()
					.texOffs(22, 11).addBox(0, -6.75F, 1, 0, 5, 7), PartPose.offsetAndRotation(0, 0.3F, 5, radf(-8), 0, 0))
			.addOrReplaceChild("tailSub2", CubeListBuilder.create()
					.texOffs(22, 15).addBox(0, -6.25F, 1, 0, 5, 8), PartPose.offsetAndRotation(0, 0.2F, 5.5F, radf(10), 0, 0))
			.addOrReplaceChild("tailSub3", CubeListBuilder.create()
					.texOffs(29, 6).addBox(0, -5.75F, 1, 0, 5, 7), PartPose.offsetAndRotation(0, 0.4F, 7.5F, radf(20), 0, 0));

		root = rootDef.bake(64, 32);
		tailBase = root.getChild("tailBase");
		tail1 = tailBase.getChild("tail1");
		tail2 = tail1.getChild("tail2");
		tail3 = tail2.getChild("tail3");
		tailSubBase = root.getChild("tailSubBase");
		tailSub1 = tailSubBase.getChild("tailSub1");
		tailSub2 = tailSub1.getChild("tailSub2");
		tailSub3 = tailSub2.getChild("tailSub3");

		config = new PartConfiguration(List.of(tailBase, tail1, tail2, tail3, tailSubBase, tailSub1, tailSub2, tailSub3));
		config0 = new PartConfiguration(List.of(tailBase, tail1, tail2, tail3));
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

		final float timestep = getAnimationTime(4000, entity);
		setRotationRadians(tailBase, rad(-40) + xAngleOffset * 2, Mth.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail1, rad(-8) + xAngleOffset * 2, Mth.cos(timestep - 2) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail2, rad(10) - xAngleOffset / 4, Mth.cos(timestep - 3) / 5 * yAngleMultiplier, 0);
		setRotationRadians(tail3, rad(20) - xAngleOffset, Mth.cos(timestep - 4) / 5 * yAngleMultiplier, 0);

		if ("dragon_tail".equals(subType.id())) {
			setRotationRadians(tailSubBase, rad(-40) + xAngleOffset * 2, Mth.cos(timestep - 1) / 5 * yAngleMultiplier, 0);
			setRotationRadians(tailSub1, rad(-8) + xAngleOffset * 2, Mth.cos(timestep - 2) / 5 * yAngleMultiplier, 0);
			setRotationRadians(tailSub2, rad(10) - xAngleOffset / 4, Mth.cos(timestep - 3) / 5 * yAngleMultiplier, 0);
			setRotationRadians(tailSub3, rad(20) - xAngleOffset, Mth.cos(timestep - 4) / 5 * yAngleMultiplier, 0);
		}
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(tailBase);

		if ("dragon_tail".equals(ctx.info().getSubType().id()))
			ctx.render(tailSubBase);
	}

	@Override
	public List<PartConfiguration> getParts(ClientPartInfo info) {
		if (!"dragon_tail".equals(info.getSubType().id()))
			return List.of(config0);

		return super.getParts(info);
	}
}