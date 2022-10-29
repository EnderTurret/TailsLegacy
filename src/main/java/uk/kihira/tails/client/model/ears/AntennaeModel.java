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

public final class AntennaeModel extends PartModel {

	private final ModelPart root;
	private final ModelPart rantennae;
	private final ModelPart lantennae;

	public AntennaeModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("rantennae", CubeListBuilder.create()
				.texOffs(5, 5).addBox(-5F, -2F, -5F, 1F, 2F, 1F)
				.texOffs(8, 0).addBox(-5F, -3F, -5.5F, 1F, 1F, 1F)
				.texOffs(0, 8).addBox(-5F, -4F, -6F, 1F, 1F, 1F)
				.texOffs(0, 3).addBox(-5F, -4.5F, -8F, 1F, 1F, 2F)
				, PartPose.offset(2F, 24F, 3F));
		rootDef.addOrReplaceChild("lantennae", CubeListBuilder.create()
				.texOffs(5, 2).addBox(-5F, -2F, -5F, 1F, 2F, 1F)
				.texOffs(0, 6).addBox(-5F, -3F, -5.5F, 1F, 1F, 1F)
				.texOffs(4, 0).addBox(-5F, -4F, -6F, 1F, 1F, 1F)
				.texOffs(0, 0).addBox(-5F, -4.5F, -8F, 1F, 1F, 2F)
				, PartPose.offset(7F, 24F, 3F));

		root = rootDef.bake(16, 16);

		rantennae = root.getChild("rantennae");
		lantennae = root.getChild("lantennae");
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Part.SubType subType, float partialTick) {
		poseStack.pushPose();
		poseStack.translate(0, -2, 0);
		root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}
}