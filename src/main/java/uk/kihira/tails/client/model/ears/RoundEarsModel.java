/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
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
 * <p>The round ears part model.</p>
 * <p>Model created by Dustskys, with implementation & programming by EnderTurret.</p>
 * @author EnderTurret
 */
final class RoundEarsModel extends PartModel {

	private final ModelPart root;

	public RoundEarsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -0.5F, 3F, 3F, 1F), PartPose.offsetAndRotation(3.5338F, -8.8608F, -0.5F, 0F, 0F, 0.2443F));
		rootDef.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 4).addBox(-1.5F, -1.5F, -0.5F, 3F, 3F, 1F), PartPose.offsetAndRotation(-3.5338F, -8.8608F, -0.5F, 0F, 0F, -0.2443F));

		root = rootDef.bake(16, 16);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}