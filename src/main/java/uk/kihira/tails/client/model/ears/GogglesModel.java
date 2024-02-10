/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;

/**
 * <p>The goggles part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class GogglesModel extends PartModel {

	private final ModelPart root;

	public GogglesModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition goggles = rootDef.addOrReplaceChild("goggles", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4F, -8.7F, -1F, 8F, 1F, 2F)
				.texOffs(6, 13).addBox(-4.7F, -8F, -1F, 1F, 4F, 2F)
				.texOffs(0, 13).addBox(3.7F, -8F, -1F, 1F, 4F, 2F)
				.texOffs(10, 6).addBox(1F, -9.7F, -1F, 2F, 2F, 2F)
				.texOffs(13, 3).addBox(-4.7F, -4F, -0.5F, 1F, 2F, 1F)
				.texOffs(9, 3).addBox(3.7F, -4F, -0.5F, 1F, 2F, 1F),
				PartPose.offset(0F, 0F, -1F));

		goggles.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(10, 11).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-2F, -8.7F, 0F, 0F, 0F, -0.0175F));
		goggles.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 3).addBox(-1.5F, -0.5F, -1.5F, 3F, 2F, 3F), PartPose.offsetAndRotation(2F, -8.7F, 0F, 0F, 0F, 0.1745F));
		goggles.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 8).addBox(-1.5F, -0.5F, -1.5F, 3F, 2F, 3F), PartPose.offsetAndRotation(-2F, -8.7F, 0F, 0F, 0F, -0.1745F));

		root = rootDef.bake(32, 32);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}