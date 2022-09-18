/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part;

/**
 * The model for panda ears.
 */
public class PandaEarsModel extends PartModel {

	private final ModelPart root;
	private final ModelPart leftEar;
	private final ModelPart rightEar;

	public PandaEarsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("leftEar", CubeListBuilder.create()
				.mirror().texOffs(0, 0)
				.addBox(-2, -2, 0, 3, 3, 1), PartPose.offset(-4, -8, 0));
		rootDef.addOrReplaceChild("rightEar", CubeListBuilder.create()
				.mirror().texOffs(0, 4)
				.addBox(-1, -2, 0, 3, 3, 1), PartPose.offset(4, -8, 0));
		root = rootDef.bake(32, 32);

		leftEar = root.getChild("leftEar");
		rightEar = root.getChild("rightEar");
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Part.SubType subType, float partialTick) {
		leftEar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		rightEar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}