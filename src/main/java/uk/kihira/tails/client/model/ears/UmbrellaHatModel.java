/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 EnderTurret
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
 * <p>The umbrella hat part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class UmbrellaHatModel extends PartModel {

	private final ModelPart root;

	public UmbrellaHatModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		final PartDefinition r = rootDef.addOrReplaceChild("root", CubeListBuilder.create()
				.texOffs(15, 21).addBox(-1F, -6F, -1F, 2F, 6F, 2F)
				.texOffs(18, 8).addBox(-3F, -7.3F, -3F, 6F, 1F, 6F)
				.texOffs(23, 25).addBox(-1F, -8F, -1F, 2F, 1F, 2F), PartPose.offset(0, -6, 0));

		r.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(13, 15).addBox(-2.5726F, 0.1253F, -2.5F, 5F, 0F, 5F), PartPose.offsetAndRotation(4.2726F, -5.0253F, 4.2136F, -0.4186F, -0.69F, 0.6175F));
		r.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(19, 20).addBox(-2.5726F, 0.1253F, -2.5F, 5F, 0F, 5F), PartPose.offsetAndRotation(4.2726F, -5.0253F, -4.2136F, 0.4186F, 0.69F, 0.6175F));
		r.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 21).addBox(-2.4274F, 0.1253F, -2.5F, 5F, 0F, 5F), PartPose.offsetAndRotation(-4.2726F, -5.0253F, -4.2136F, 0.4186F, -0.69F, -0.6175F));
		r.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(23, 15).addBox(-2.4274F, 0.1253F, -2.5F, 5F, 0F, 5F), PartPose.offsetAndRotation(-4.2726F, -5.0253F, 4.2136F, -0.4186F, 0.69F, -0.6175F));
		r.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -7.8282F, -3.01F, 6F, 1F, 6F), PartPose.offsetAndRotation(-0.134F, 0.4282F, 0F, 0F, 0F, 0.48F));
		r.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 7).addBox(-5F, -7.8282F, -2.99F, 6F, 1F, 6F), PartPose.offsetAndRotation(0.134F, 0.4282F, 0F, 0F, 0F, -0.48F));
		r.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 14).addBox(-3.01F, -7.8282F, -5F, 6F, 1F, 6F), PartPose.offsetAndRotation(0F, 0.4282F, 0.134F, 0.48F, 0F, 0F));
		r.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(18, 1).addBox(-2.99F, -7.8282F, -1F, 6F, 1F, 6F), PartPose.offsetAndRotation(0F, 0.4282F, -0.134F, -0.48F, 0F, 0F));

		root = rootDef.bake(64, 64);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}