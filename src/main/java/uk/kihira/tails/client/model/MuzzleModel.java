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

import uk.kihira.tails.client.part.Part;

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
	public void render(PoseStack poseStack, VertexConsumer buffer, LivingEntity entity, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Part.SubType subType, float partialTick) {
		poseStack.pushPose();

		poseStack.translate(0, -0.001D, 0);

		switch (subType.id()) {
		case "very_short" -> poseStack.translate(0, 0, 4 / 16D);
		case "short" -> poseStack.translate(0, 0, 3 / 16D);
		case "standard" -> poseStack.translate(0, 0, 2 / 16D);
		case "long" -> poseStack.translate(0, 0, 1 / 16D);
		case "very_long" -> {}
		}

		muzzle.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		poseStack.popPose();
	}
}
