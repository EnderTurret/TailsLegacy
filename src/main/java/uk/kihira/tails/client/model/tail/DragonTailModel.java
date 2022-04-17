/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

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
import uk.kihira.tails.common.part.PartInfo;

/**
 * The model for dragon tails.
 */
public class DragonTailModel extends PartModel {

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
					.texOffs(22, 0).addBox(-2.5F, -2.5F, -2, 5, 5, 8), PartPose.rotation(rad(-40), 0, 0))
			.addOrReplaceChild("tail1", CubeListBuilder.create()
					.texOffs(0, 0).addBox(-2, -2, 0, 4, 4, 7), PartPose.offsetAndRotation(0, 0.3F, 5, rad(-8), 0, 0))
			.addOrReplaceChild("tail2", CubeListBuilder.create()
					.texOffs(0, 11).addBox(-1.5F, -1.5F, 0, 3, 3, 8), PartPose.offsetAndRotation(0, 0.2F, 5.5F, rad(10), 0, 0))
			.addOrReplaceChild("tail3", CubeListBuilder.create()
					.texOffs(0, 22).addBox(-1, -1, 0, 2, 2, 7), PartPose.offsetAndRotation(0, 0.4F, 7.5F, rad(20), 0, 0));

		rootDef
			.addOrReplaceChild("tailSubBase", CubeListBuilder.create()
					.texOffs(22, 5).addBox(0, -7.25F, -2, 0, 5, 8), PartPose.rotation(rad(-40), 0, 0))
			.addOrReplaceChild("tailSub1", CubeListBuilder.create()
					.texOffs(22, 11).addBox(0, -6.75F, 1, 0, 5, 7), PartPose.offsetAndRotation(0, 0.3F, 5, rad(-8), 0, 0))
			.addOrReplaceChild("tailSub2", CubeListBuilder.create()
					.texOffs(22, 15).addBox(0, -6.25F, 1, 0, 5, 8), PartPose.offsetAndRotation(0, 0.2F, 5.5F, rad(10), 0, 0))
			.addOrReplaceChild("tailSub3", CubeListBuilder.create()
					.texOffs(29, 6).addBox(0, -5.75F, 1, 0, 5, 7), PartPose.offsetAndRotation(0, 0.4F, 7.5F, rad(20), 0, 0));

		root = rootDef.bake(64, 32);
		tailBase = root.getChild("tailBase");
		tail1 = tailBase.getChild("tail1");
		tail2 = tail1.getChild("tail2");
		tail3 = tail2.getChild("tail3");
		tailSubBase = root.getChild("tailSubBase");
		tailSub1 = tailSubBase.getChild("tailSub1");
		tailSub2 = tailSub1.getChild("tailSub2");
		tailSub3 = tailSub2.getChild("tailSub3");

		config = new PartConfiguration(tailBase, List.of(tailBase, tail1, tail2, tail3, tailSubBase, tailSub1, tailSub2, tailSub3));
		config0 = new PartConfiguration(tailBase, List.of(tailBase, tail1, tail2, tail3));
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float subtype, float headPitch) {
		double xAngleOffset = 0;
		double yAngleMultiplier = 1; // Used to suppress sway when running.
		if (entity.getVehicle() == null) {
			if (entity instanceof Player) {
				final double[] angles = getMotionAngles((Player) entity, partialTicks);

				xAngleOffset = Mth.clamp(angles[0] / 5F, -1D, 0.45D);
				yAngleMultiplier = 1 - xAngleOffset * 2F; // Used to suppress sway when running.
			}
		}
		// Mounted
		else {
			xAngleOffset = Math.toRadians(12F);
			yAngleMultiplier = 0.25F;
		}

		final float timestep = getAnimationTime(4000D, entity);
		setRotationRadians(tailBase, Math.toRadians(-40F) + xAngleOffset * 2F, Mth.cos(timestep - 1) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail1, Math.toRadians(-8F) + xAngleOffset * 2F, Mth.cos(timestep - 2) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail2, Math.toRadians(10F) - xAngleOffset / 4F, Mth.cos(timestep - 3) / 5F * yAngleMultiplier, 0F);
		setRotationRadians(tail3, Math.toRadians(20F) - xAngleOffset, Mth.cos(timestep - 4) / 5F * yAngleMultiplier, 0F);

		if (subtype == 1) {
			setRotationRadians(tailSubBase, Math.toRadians(-40F) + xAngleOffset * 2F, Mth.cos(timestep - 1) / 5F * yAngleMultiplier, 0F);
			setRotationRadians(tailSub1, Math.toRadians(-8F) + xAngleOffset * 2F, Mth.cos(timestep - 2) / 5F * yAngleMultiplier, 0F);
			setRotationRadians(tailSub2, Math.toRadians(10F) - xAngleOffset / 4F, Mth.cos(timestep - 3) / 5F * yAngleMultiplier, 0F);
			setRotationRadians(tailSub3, Math.toRadians(20F) - xAngleOffset, Mth.cos(timestep - 4) / 5F * yAngleMultiplier, 0F);
		}
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (subtype == 1)
			tailSubBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public List<PartConfiguration> getParts(PartInfo info) {
		final int subtype = info.getSubType();

		if (subtype != 1)
			return List.of(config0);

		return super.getParts(info);
	}
}