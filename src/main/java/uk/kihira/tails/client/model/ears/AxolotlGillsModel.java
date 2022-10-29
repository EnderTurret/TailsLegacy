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

public final class AxolotlGillsModel extends PartModel {

	private final ModelPart root;

	public AxolotlGillsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("rgill", CubeListBuilder.create()
				.texOffs(0, 6).addBox(-4.5F, -4.5F, -2F, 1F, 1F, 1F)
				.texOffs(3, 5).addBox(-4.5F, -2.5F, -2F, 1F, 1F, 1F)
				.texOffs(5, 1).addBox(-4.5F, -6.5F, -2F, 1F, 1F, 1F)
				.texOffs(0, 8).addBox(-5.5F, -2.5F, -2F, 1F, 1F, 0F)
				.texOffs(7, 6).addBox(-5.5F, -6.5F, -2F, 1F, 1F, 0F)
				.texOffs(0, 1).addBox(-7.5F, -4.5F, -2F, 3F, 1F, 0F)
				.texOffs(4, 7).addBox(-6.5F, -1.5F, -2F, 2F, 1F, 0F)
				.texOffs(6, 5).addBox(-6.5F, -7.5F, -2F, 2F, 1F, 0F)
				, PartPose.offset(0F, 24F, 0F));
		rootDef.addOrReplaceChild("lgill", CubeListBuilder.create()
				.texOffs(0, 4).addBox(-11.5F, -4.5F, -2F, 1F, 1F, 1F)
				.texOffs(3, 3).addBox(-11.5F, -2.5F, -2F, 1F, 1F, 1F)
				.texOffs(0, 2).addBox(-11.5F, -6.5F, -2F, 1F, 1F, 1F)
				.texOffs(7, 4).addBox(-10.5F, -2.5F, -2F, 1F, 1F, 0F)
				.texOffs(3, 2).addBox(-10.5F, -6.5F, -2F, 1F, 1F, 0F)
				.texOffs(0, 0).addBox(-10.5F, -4.5F, -2F, 3F, 1F, 0F)
				.texOffs(6, 3).addBox(-10.5F, -1.5F, -2F, 2F, 1F, 0F)
				.texOffs(6, 0).addBox(-10.5F, -7.5F, -2F, 2F, 1F, 0F)
				, PartPose.offset(15F, 24F, 0F));

		root = rootDef.bake(16, 16);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Part.SubType subType, float partialTick) {
		poseStack.pushPose();
		poseStack.translate(0, -1.5, 0);
		root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}
}