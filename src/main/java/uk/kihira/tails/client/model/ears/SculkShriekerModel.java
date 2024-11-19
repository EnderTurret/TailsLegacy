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
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The sculk shrieker part model.</p>
 * <p>Model created by CogwheelCat.</p>
 * @author EnderTurret
 */
final class SculkShriekerModel extends PartModel {

	private final ModelPart root;

	public SculkShriekerModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef.addOrReplaceChild("root", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4F, -16F, -4F, 8F, 5F, 8F, new CubeDeformation(-0.5F))
				.texOffs(0, 16).addBox(-4F, -12F, -4F, 8F, 4F, 8F),
				PartPose.ZERO);

		root = rootDef.bake(32, 32);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}