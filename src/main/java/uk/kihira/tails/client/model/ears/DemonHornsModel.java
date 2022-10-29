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

public final class DemonHornsModel extends PartModel {

	private final ModelPart root;
	private final ModelPart rhorn;
	private final ModelPart lhorn;

	public DemonHornsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("rhorn", CubeListBuilder.create()
				.texOffs(0, 3).addBox(-5F, -0.01F, -5F, 2F, 1F, 2F)
				.texOffs(4, 10).addBox(-5F, -2.01F, -5F, 1F, 2F, 1F)
				.texOffs(8, 11).addBox(-4F, -1.01F, -5F, 1F, 1F, 1F)
				.texOffs(11, 7).addBox(-5F, -1.01F, -4F, 1F, 1F, 1F)
				.texOffs(10, 0).addBox(-4.5F, -3.01F, -5.3F, 1F, 2F, 1F)
				.texOffs(0, 10).addBox(-5.3F, -3.01F, -4.5F, 1F, 2F, 1F)
				.texOffs(4, 6).addBox(-5.5F, -5.01F, -5.5F, 1F, 3F, 1F)
				, PartPose.offset(0.99F, 24F, 0F));
		rootDef.addOrReplaceChild("lhorn", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5F, -0.01F, -5F, 2F, 1F, 2F)
				.texOffs(8, 8).addBox(-5F, -2.01F, -5F, 1F, 2F, 1F)
				.texOffs(11, 3).addBox(-4F, -1.01F, -5F, 1F, 1F, 1F)
				.texOffs(6, 0).addBox(-5F, -1.01F, -4F, 1F, 1F, 1F)
				.texOffs(8, 5).addBox(-4.5F, -3.01F, -5.3F, 1F, 2F, 1F)
				.texOffs(7, 2).addBox(-5.3F, -3.01F, -4.5F, 1F, 2F, 1F)
				.texOffs(0, 6).addBox(-5.5F, -5.01F, -5.5F, 1F, 3F, 1F)
				, PartPose.offsetAndRotation(-0.99F, 24F, 0F, 0F, -1.5708F, 0F));

		root = rootDef.bake(16, 16);

		rhorn = root.getChild("rhorn");
		lhorn = root.getChild("lhorn");
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Part.SubType subType, float partialTick) {
		poseStack.pushPose();
		poseStack.translate(0, -2, 0);
		root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}
}