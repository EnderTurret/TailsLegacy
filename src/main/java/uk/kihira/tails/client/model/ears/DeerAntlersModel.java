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

public final class DeerAntlersModel extends PartModel {

	private final ModelPart root;

	public DeerAntlersModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("rantler", CubeListBuilder.create()
				.texOffs(7, 2).addBox(-4F, -2F, -5F, 1F, 2F, 1F)
				.texOffs(9, 8).addBox(-3F, -1.5F, -5F, 1F, 1F, 1F)
				.texOffs(6, 9).addBox(-5.5F, -3.5F, -4.5F, 1F, 1F, 1F)
				.texOffs(9, 5).addBox(-6F, -4.5F, -4.5F, 1F, 1F, 1F)
				.texOffs(3, 3).addBox(-4.5F, -5F, -4.5F, 1F, 3F, 1F)
				.texOffs(6, 6).addBox(-5F, -7F, -4F, 1F, 2F, 1F)
				, PartPose.offset(1F, 24F, 3F));
		rootDef.addOrReplaceChild("lantler", CubeListBuilder.create()
				.texOffs(0, 6).addBox(-4F, -2F, -5F, 1F, 2F, 1F)
				.texOffs(0, 9).addBox(-3F, -1.5F, -5F, 1F, 1F, 1F)
				.texOffs(3, 8).addBox(-5.5F, -3.5F, -5.5F, 1F, 1F, 1F)
				.texOffs(8, 0).addBox(-6F, -4.5F, -5.5F, 1F, 1F, 1F)
				.texOffs(0, 0).addBox(-4.5F, -5F, -5.5F, 1F, 3F, 1F)
				.texOffs(4, 0).addBox(-5F, -7F, -6F, 1F, 2F, 1F)
				, PartPose.offsetAndRotation(-1F, 24F, -6F, 0F, 3.1416F, 0F));

		root = rootDef.bake(16, 16);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.poseStack().pushPose();
		ctx.poseStack().translate(0, -2, 0);
		ctx.render(root);
		ctx.poseStack().popPose();
	}
}