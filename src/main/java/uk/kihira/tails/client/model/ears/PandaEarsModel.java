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

/**
 * The model for panda ears.
 */
final class PandaEarsModel extends PartModel {

	private final ModelPart root;
	private final ModelPart leftEar;
	private final ModelPart rightEar;

	public PandaEarsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("leftEar", CubeListBuilder.create()
				.mirror().texOffs(0, 0)
				.addBox(-2, -2, 0, 3, 3, 1), PartPose.offset(-4, -8, 0));
		rootDef.addOrReplaceChild("rightEar", CubeListBuilder.create()
				.mirror().texOffs(0, 4)
				.addBox(-1, -2, 0, 3, 3, 1), PartPose.offset(4, -8, 0));
		root = rootDef.bake(16, 16);

		leftEar = root.getChild("leftEar");
		rightEar = root.getChild("rightEar");
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(leftEar);
		ctx.render(rightEar);
	}
}