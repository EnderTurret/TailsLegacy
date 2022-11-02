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

final class AntennaeModel extends PartModel {

	private final ModelPart root;

	public AntennaeModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("rantennae", CubeListBuilder.create()
				.texOffs(5, 5).addBox(-5F, -2F, -5F, 1F, 2F, 1F)
				.texOffs(8, 0).addBox(-5F, -3F, -5.5F, 1F, 1F, 1F)
				.texOffs(0, 8).addBox(-5F, -4F, -6F, 1F, 1F, 1F)
				.texOffs(0, 3).addBox(-5F, -4.5F, -8F, 1F, 1F, 2F)
				, PartPose.offset(2F, 24F - 32, 3F));
		rootDef.addOrReplaceChild("lantennae", CubeListBuilder.create()
				.texOffs(5, 2).addBox(-5F, -2F, -5F, 1F, 2F, 1F)
				.texOffs(0, 6).addBox(-5F, -3F, -5.5F, 1F, 1F, 1F)
				.texOffs(4, 0).addBox(-5F, -4F, -6F, 1F, 1F, 1F)
				.texOffs(0, 0).addBox(-5F, -4.5F, -8F, 1F, 1F, 2F)
				, PartPose.offset(7F, 24F - 32, 3F));

		root = rootDef.bake(16, 16);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().translate(-0.15, 0.25, 0);
		ctx.poseStack().mulPose(Vector3f.YP.rotationDegrees(-45));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}