/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/**
 * The model used for muzzles.
 */
public class MuzzleModel extends PartModel {

	private final ModelPart root;
	private final ModelPart muzzle;

	public MuzzleModel(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, int xTex, int yTex) {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("muzzle", CubeListBuilder.create()
						.texOffs(xTex, yTex).addBox(xOffset, yOffset, zOffset, xSize, ySize, zSize), PartPose.ZERO);
		root = rootDef.bake(32, 32);

		muzzle = root.getChild("muzzle");
	}

	public MuzzleModel(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize) {
		this(xOffset, yOffset, zOffset, xSize, ySize, zSize, 0, 0);
	}

	@Override
	public void render(PoseStack poseStack, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		poseStack.pushPose();
		poseStack.translate(0, -0.001F, 0);
		switch (subtype) {
		case 0: // Very Short
			poseStack.translate(0f, 0f, 4f / 16f);
			muzzle.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 1: // Short
			poseStack.translate(0f, 0f, 3f / 16f);
			muzzle.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 2: // Standard
			poseStack.translate(0f, 0f, 2f / 16f);
			muzzle.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 3: // Long
			poseStack.translate(0f, 0f, 1f / 16f);
			muzzle.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 4: // Very Long
			muzzle.render(poseStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		}
		poseStack.popPose();
	}
}
