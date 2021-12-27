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

	//private final ModelPart stubMuzzle;
	//private final ModelPart tinyMuzzle;

	private final ModelPart root;
	private final ModelPart muzzle;

	public MuzzleModel(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, int xTex, int yTex) {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("muzzle", CubeListBuilder.create()
						.texOffs(xTex, yTex).addBox(xOffset, yOffset, zOffset, xSize, ySize, zSize), PartPose.ZERO);
		root = rootDef.bake(32, 32);

		muzzle = root.getChild("muzzle");

		/*stubMuzzle = new ModelPart(this);
		stubMuzzle.addBox(-2f, -4f, -7f, 4, 4, 3);

		tinyMuzzle = new ModelPart(this);
		tinyMuzzle.addBox(-2f, -2f, -5f, 4, 2, 1);*/
	}

	public MuzzleModel(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize) {
		this(xOffset, yOffset, zOffset, xSize, ySize, zSize, 0, 0);
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, -0.001F, 0);
		switch (subtype) {
		case 0: // Very Short
			matrixStackIn.translate(0f, 0f, 4f / 16f);
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 1: // Short
			matrixStackIn.translate(0f, 0f, 3f / 16f);
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 2: // Standard
			matrixStackIn.translate(0f, 0f, 2f / 16f);
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 3: // Long
			matrixStackIn.translate(0f, 0f, 1f / 16f);
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 4: // Very Long
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		}
		matrixStackIn.popPose();
	}
}
