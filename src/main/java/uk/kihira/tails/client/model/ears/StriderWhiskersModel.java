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
 * <p>The strider whiskers part model.</p>
 * <p>Model created by Dustskys, with implementation & programming by EnderTurret.</p>
 * @author EnderTurret
 */
final class StriderWhiskersModel extends PartModel {

	private final ModelPart root;

	public StriderWhiskersModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition rwhisk = rootDef.addOrReplaceChild("rwhiskers", CubeListBuilder.create(), PartPose.offset(-4F, 24F - 24, 0F));

		rwhisk.addOrReplaceChild("rwhisker1", CubeListBuilder.create()
				.texOffs(4, 0).addBox(-0.5F, -7F, -3F, 1F, 1F, 1F)
				.texOffs(6, 6).addBox(-0.5F, -6F, -3F, 0F, 1F, 1F)
				.texOffs(2, 3).addBox(-0.5F, -4F, -2F, 0F, 3F, 1F)
				.texOffs(6, 5).addBox(-0.5F, -5F, -2.5F, 0F, 1F, 1F)
				, PartPose.offset(0F, 0F, 4F));

		rwhisk.addOrReplaceChild("rwhisker2", CubeListBuilder.create()
				.texOffs(4, 4).addBox(-0.5F, -7F, -3F, 1F, 1F, 1F)
				.texOffs(7, 1).addBox(-0.5F, -6F, -3F, 0F, 1F, 1F)
				.texOffs(0, 7).addBox(-0.5F, -3F, -2F, 0F, 1F, 1F)
				.texOffs(4, 5).addBox(-0.5F, -5F, -2.5F, 0F, 2F, 1F)
				, PartPose.ZERO);

		final PartDefinition lwhisk = rootDef.addOrReplaceChild("lwhiskers", CubeListBuilder.create(), PartPose.offset(11F, 24F - 24, 0F));

		lwhisk.addOrReplaceChild("lwhisker1", CubeListBuilder.create()
				.texOffs(2, 2).addBox(-7.5F, -7F, -3F, 1F, 1F, 1F)
				.texOffs(6, 2).addBox(-6.5F, -6F, -3F, 0F, 1F, 1F)
				.texOffs(0, 1).addBox(-6.5F, -4F, -2F, 0F, 3F, 1F)
				.texOffs(2, 6).addBox(-6.5F, -5F, -2.5F, 0F, 1F, 1F)
				, PartPose.offset(0F, 0F, 4F));

		lwhisk.addOrReplaceChild("lwhisker2", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-7.5F, -7F, -3F, 1F, 1F, 1F)
				.texOffs(0, 6).addBox(-6.5F, -6F, -3F, 0F, 1F, 1F)
				.texOffs(5, 1).addBox(-6.5F, -3F, -2F, 0F, 1F, 1F)
				.texOffs(0, 4).addBox(-6.5F, -5F, -2.5F, 0F, 2F, 1F)
				, PartPose.ZERO);

		root = rootDef.bake(16, 16);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().scale(0.8f, 0.8f, 0.8f);
		ctx.poseStack().mulPose(Vector3f.YP.rotationDegrees(-45));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}