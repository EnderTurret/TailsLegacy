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
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The straw hat part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class StrawHatModel extends PartModel {

	private final ModelPart root;

	public StrawHatModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition hat = rootDef.addOrReplaceChild("hat", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-7.8317F, -3.9225F, 1.9124F, 6F, 4F, 6F, new CubeDeformation(0.01F)),
				PartPose.offset(4.8317F, -8.2F, -4.9124F));

		hat.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(15, 18).addBox(-2.7F, -0.2F, -2.5F, 3F, 1F, 5F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.2444F, 0.7854F, 0.3465F));
		hat.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(19, 5).addBox(-2.7F, -0.2F, -2.5F, 3F, 1F, 5F), PartPose.offsetAndRotation(0F, 0F, 9.8248F, -0.2444F, -0.7854F, 0.3465F));
		hat.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 23).addBox(-0.3F, -0.2F, -2.5F, 3F, 1F, 5F), PartPose.offsetAndRotation(-9.6634F, 0F, 9.8248F, -0.2444F, 0.7854F, -0.3465F));
		hat.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(11, 24).addBox(-0.3F, -0.2F, -2.5F, 3F, 1F, 5F), PartPose.offsetAndRotation(-9.6634F, 0F, 0F, 0.2444F, -0.7854F, -0.3465F));
		hat.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 10).addBox(-2.5F, -0.5F, -3F, 4F, 1F, 6F), PartPose.offsetAndRotation(-9.3317F, -0.4225F, 4.9124F, 0F, 0F, -0.1745F));
		hat.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(14, 11).addBox(-1.5F, -0.5F, -3F, 4F, 1F, 6F), PartPose.offsetAndRotation(-0.3317F, -0.4225F, 4.9124F, 0F, 0F, 0.1745F));
		hat.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 18).addBox(-3F, -0.5F, -1.5F, 6F, 1F, 4F), PartPose.offsetAndRotation(-4.8317F, -0.4225F, 9.4124F, -0.1745F, 0F, 0F));
		hat.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(18, 0).addBox(-3F, -0.5F, -2.5F, 6F, 1F, 4F), PartPose.offsetAndRotation(-4.8317F, -0.4225F, 0.4124F, 0.1745F, 0F, 0F));

		root = rootDef.bake(64, 32);
		root.xScale = root.yScale = root.zScale = 1.3F;
		root.y = 3.07F;
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}