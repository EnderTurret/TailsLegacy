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
 * The small cat ears part model.
 */
final class SmallCatEarsModel extends PartModel {

	private final ModelPart root;
	private final ModelPart leftEar;
	private final ModelPart rightEar;

	public SmallCatEarsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("leftEar", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-1, -2, 1, 1, 1, 1)
				.texOffs(0, 12).addBox(-2, -1, 1, 2, 1, 1)
				.texOffs(0, 0).addBox(0, 0, 0, 1, 1, 1)
				.texOffs(0, 2).addBox(-3, -1, 0, 4, 1, 1)
				.texOffs(0, 4).addBox(-2, -2, 0, 3, 1, 1)
				.texOffs(0, 6).addBox(-1, -3, 0, 1, 1, 1), PartPose.offset(4, -8, 0));
		rootDef.addOrReplaceChild("rightEar", CubeListBuilder.create()
				.texOffs(13, 14).addBox(-1, -2, 1, 1, 1, 1)
				.texOffs(13, 12).addBox(-1, -1, 1, 2, 1, 1)
				.texOffs(13, 0).addBox(0, 0, 0, 1, 1, 1)
				.texOffs(13, 2).addBox(-2, -1, 0, 4, 1, 1)
				.texOffs(13, 4).addBox(-2, -2, 0, 3, 1, 1)
				.texOffs(13, 6).addBox(-1, -3, 0, 1, 1, 1), PartPose.offset(-3, -8, 0));
		root = rootDef.bake(32, 32);

		leftEar = root.getChild("leftEar");
		rightEar = root.getChild("rightEar");
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(rightEar);
		ctx.render(leftEar);
	}
}