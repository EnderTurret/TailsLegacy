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

public final class SeaPickleModel extends PartModel {

	private final ModelPart root;
	private final ModelPart pickle;

	public SeaPickleModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("pickle", CubeListBuilder.create()
				.texOffs(0, 1).addBox(-2, -0.2875F, -2, 4, 6, 4)
				.texOffs(0, 11).addBox(-2, -0.2375F, -2, 4, 0, 4)
				, PartPose.offset(0, 18.2875F, 0));
		root = rootDef.bake(32, 32);

		pickle = root.getChild("pickle");
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Part.SubType subType, float partialTick) {
		poseStack.pushPose();

		poseStack.translate(0, -2, 0);

		pickle.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		poseStack.popPose();
	}
}