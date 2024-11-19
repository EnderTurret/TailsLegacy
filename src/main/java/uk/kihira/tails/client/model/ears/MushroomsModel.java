/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import org.joml.Quaternionf;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The mushrooms part model.</p>
 * <p>Model created by CogwheelCat.</p>
 * @author EnderTurret
 */
final class MushroomsModel extends PartModel {

	private final ModelPart root;

	public MushroomsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef.addOrReplaceChild("short", CubeListBuilder.create()
				.texOffs(8, 10).addBox(-2F, -11F, -4F, 1F, 1F, 1F)
				.texOffs(9, 1).addBox(-2F, -9F, -4F, 1F, 1F, 1F)
				.texOffs(0, 6).addBox(-3F, -10F, -5F, 3F, 1F, 3F),
				PartPose.offset(0F, 0F, 1F));

		rootDef.addOrReplaceChild("tall", CubeListBuilder.create()
				.texOffs(8, 10).addBox(4F, -13F, 4F, 1F, 1F, 1F)
				.texOffs(9, 1).addBox(4F, -9F, 4F, 1F, 1F, 1F)
				.texOffs(0, 0).addBox(3F, -12F, 3F, 3F, 3F, 3F),
				PartPose.offset(-3F, 0F, -3F));

		root = rootDef.bake(16, 16);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}