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

/**
 * The model for fox ears.
 */
public class FoxEarsModel extends PartModel {

	private final ModelPart root;
	private final ModelPart leftEar;
	private final ModelPart rightEar;

	public FoxEarsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("leftEar", CubeListBuilder.create()
				.mirror()
				.texOffs(0, 16).addBox(4, -11, 1, 1, 1, 1)
				.texOffs(4, 16).addBox(3, -10, 1, 2, 2, 1)
				.texOffs(0, 0).addBox(2, -10, 1, 1, 3, 1)
				.texOffs(4, 0).addBox(3, -11, 1, 1, 1, 1)
				.texOffs(4, 4).addBox(4, -12, 1, 1, 1, 1)
				.texOffs(0, 8).addBox(5, -11, 1, 1, 3, 1)
				.texOffs(10, 14).addBox(3, -8, 1, 2, 1, 1)
				.texOffs(4, 8).addBox(4, -11, 2, 1, 3, 1)
				.texOffs(8, 0).addBox(3, -10, 2, 1, 2, 1), PartPose.ZERO);
		rootDef.addOrReplaceChild("rightEar", CubeListBuilder.create()
				.texOffs(0, 19).addBox(-5, -11, 1, 1, 1, 1)
				.texOffs(4, 19).addBox(-5, -10, 1, 2, 2, 1)
				.texOffs(0, 4).addBox(-3, -10, 1, 1, 3, 1)
				.texOffs(4, 2).addBox(-4, -11, 1, 1, 1, 1)
				.texOffs(4, 6).addBox(-5, -12, 1, 1, 1, 1)
				.texOffs(0, 12).addBox(-6, -11, 1, 1, 3, 1)
				.texOffs(10, 12).addBox(-5, -8, 1, 2, 1, 1)
				.texOffs(4, 12).addBox(-5, -11, 2, 1, 3, 1)
				.texOffs(8, 3).addBox(-4, -10, 2, 1, 2, 1), PartPose.ZERO);
		root = rootDef.bake(16, 32);

		leftEar = root.getChild("leftEar");
		rightEar = root.getChild("rightEar");
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, int subtype, float partialTick) {
		poseStack.pushPose();

		poseStack.translate(0f, 0f, -0.0625f);

		if (subtype == 1)
			poseStack.translate(-0.4375f, 0f, 0f);

		leftEar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		if (subtype == 1)
			poseStack.translate(0.875f, 0f, 0f);

		rightEar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		poseStack.popPose();
	}
}