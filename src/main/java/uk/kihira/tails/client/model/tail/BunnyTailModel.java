/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.List;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;

/**
 * <p>The bunny tail part model.</p>
 * <p>Model created by carrotcodes.</p>
 */
final class BunnyTailModel extends PartModel {

	private final ModelPart root;
	private final ModelPart tail;

	public BunnyTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("tail", CubeListBuilder.create()
				.addBox(0, 0, 0, 4, 3, 3), PartPose.offset(-2, -1.5F, 0));

		root = rootDef.bake(16, 16);

		tail = root.getChild("tail");

		config = new PartConfiguration(tail, List.of(tail));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(tail);
	}
}