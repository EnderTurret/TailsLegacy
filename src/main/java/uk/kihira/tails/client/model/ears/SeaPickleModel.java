/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2024 EnderTurret
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
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The sea pickle part model.</p>
 * <p>Original sea pickle model created by Mojang (duh), ported to Tails by EnderTurret.</p>
 * @author EnderTurret
 */
final class SeaPickleModel extends PartModel {

	private final ModelPart root;

	public SeaPickleModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef.addOrReplaceChild("pickle", CubeListBuilder.create()
				.texOffs(0, 1).addBox(-2, -0.2875F, -2, 4, 6, 4)
				.texOffs(0, 11).addBox(-2, -0.2375F, -2, 4, 0, 4)
				, PartPose.offset(0, -13.7125F, 0));

		root = rootDef.bake(32, 32);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().translate(0, 0.25, 0);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}