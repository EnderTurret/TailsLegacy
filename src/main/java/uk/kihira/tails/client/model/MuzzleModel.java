/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import uk.kihira.tails.client.render.RenderContext;

/**
 * The muzzle part model.
 */
@Internal
public final class MuzzleModel extends PartModel {

	private final ModelPart root;
	private final ModelPart muzzle;

	@Internal
	public MuzzleModel(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, int xTex, int yTex) {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("muzzle", CubeListBuilder.create()
						.texOffs(xTex, yTex).addBox(xOffset, yOffset, zOffset, xSize, ySize, zSize), PartPose.ZERO);
		root = rootDef.bake(32, 32);

		muzzle = root.getChild("muzzle");
	}

	@Internal
	public MuzzleModel(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize) {
		this(xOffset, yOffset, zOffset, xSize, ySize, zSize, 0, 0);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.poseStack().pushPose();

		ctx.poseStack().translate(0, -0.001D, 0);

		switch (ctx.info().getSubType().id()) {
		case "very_short" -> ctx.poseStack().translate(0, 0, 4 / 16D);
		case "short" -> ctx.poseStack().translate(0, 0, 3 / 16D);
		case "standard" -> ctx.poseStack().translate(0, 0, 2 / 16D);
		case "long" -> ctx.poseStack().translate(0, 0, 1 / 16D);
		case "very_long" -> {}
		}

		ctx.render(muzzle);

		ctx.poseStack().popPose();
	}
}
