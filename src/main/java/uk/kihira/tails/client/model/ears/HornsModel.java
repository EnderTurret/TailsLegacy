/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The horns part model.</p>
 * <p>Model created by Dustskys, with implementation & programming by EnderTurret.</p>
 * @author EnderTurret
 */
final class HornsModel extends PartModel {

	private final ModelPart root;

	public HornsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition right = rootDef.addOrReplaceChild("right", CubeListBuilder.create(), PartPose.offset(-2.1691F, -6.591F, -3.5F));

		right.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-0.9F, -2.8536F, -1F, 1F, 3F, 1F), PartPose.offsetAndRotation(-0.7497F, -1.9489F, -0.5176F, 0.0873F, -0.0348F, 0.2413F));
		right.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(10, 5).addBox(-0.9042F, -0.9292F, -1.0529F, 1F, 2F, 1F), PartPose.offsetAndRotation(-0.1497F, -2.0666F, -0.2929F, 0.2618F, 0.0537F, -0.4842F));
		right.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(8, 0).addBox(-1F, -0.5F, -1F, 1F, 2F, 1F), PartPose.offsetAndRotation(-0.7259F, -1.8554F, -0.4223F, 0.2618F, 0F, -0.1745F));
		right.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 4).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.7854F));

		final PartDefinition left = rootDef.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offset(2.1691F, -6.591F, -3.5F));

		left.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 8).mirror().addBox(-0.1F, -2.8536F, -1F, 1F, 3F, 1F).mirror(false), PartPose.offsetAndRotation(0.7497F, -1.9489F, -0.5176F, 0.0873F, 0.0348F, -0.2413F));
		left.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(10, 5).mirror().addBox(-0.0958F, -0.9292F, -1.0529F, 1F, 2F, 1F).mirror(false), PartPose.offsetAndRotation(0.1497F, -2.0666F, -0.2929F, 0.2618F, -0.0537F, 0.4842F));
		left.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(8, 0).mirror().addBox(0F, -0.5F, -1F, 1F, 2F, 1F).mirror(false), PartPose.offsetAndRotation(0.7259F, -1.8554F, -0.4223F, 0.2618F, 0F, 0.1745F));
		left.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 4).mirror().addBox(-1F, -1F, -1F, 2F, 2F, 2F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.7854F));

		root = rootDef.bake(16, 16);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}