/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 EnderTurret
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
import net.minecraft.client.renderer.LightTexture;

import uk.kihira.tails.client.model.ModelSerializer;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The blaze crown part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class BlazeCrownModel extends PartModel {

	private final ModelPart root;
	private final ModelPart crown;
	private final ModelPart rods;

	public BlazeCrownModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition crown = rootDef.addOrReplaceChild("crown", CubeListBuilder.create()
				.texOffs(0, 9).addBox(-4.5F, -7F, -4F, 1F, 1F, 8F, new CubeDeformation(0.01F))
				.texOffs(0, 0).addBox(3.5F, -7F, -4F, 1F, 1F, 8F, new CubeDeformation(0.01F))
				.texOffs(10, 2).addBox(-4.6F, -7.1F, -4.5F, 9F, 1F, 1F, new CubeDeformation(0.01F))
				.texOffs(10, 0).addBox(-4.4F, -7.1F, 3.5F, 9F, 1F, 1F, new CubeDeformation(0.01F)),
				PartPose.ZERO);

		crown.addOrReplaceChild("rods", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-2F, -9.5F, -5F, 1F, 4F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(0, 0).addBox(-2F, -8.5F, 4F, 1F, 4F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(14, 9).addBox(-5F, -7.5F, -1F, 1F, 4F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(4, 9).addBox(4F, -7.5F, -1F, 1F, 4F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(10, 9).addBox(-5F, -8.5F, 2F, 1F, 4F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(0, 9).addBox(4F, -8.5F, 2F, 1F, 4F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(17, 17).addBox(1F, -8.5F, -5F, 1F, 4F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(4, 0).addBox(1F, -9.5F, 4F, 1F, 4F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(8, 18).addBox(3F, -8F, -5F, 1F, 3F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(10, 4).addBox(3F, -8F, 4F, 1F, 3F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(18, 4).addBox(-4F, -8F, -5F, 1F, 3F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(14, 4).addBox(-4F, -8F, 4F, 1F, 3F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(4, 18).addBox(-5F, -8F, -3F, 1F, 3F, 1F, new CubeDeformation(0F, 0.01F, 0F))
				.texOffs(17, 13).addBox(4F, -8F, -3F, 1F, 3F, 1F, new CubeDeformation(0F, 0.01F, 0F)),
				PartPose.ZERO);

		root = ModelSerializer.bake(rootDef, 32, 32, "ears/blaze_crown");

		this.crown = root.getChild("crown");
		rods = this.crown.getChild("rods");
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(crown);
		ctx.render(rods, LightTexture.FULL_BRIGHT, ctx.packedOverlay());
	}
}