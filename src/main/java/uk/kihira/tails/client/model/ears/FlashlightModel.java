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
 * <p>The flashlight part model.</p>
 * <p>Model created by Dustskys, with implementation & programming by EnderTurret.</p>
 * @author EnderTurret
 */
final class FlashlightModel extends PartModel {

	private final ModelPart root;

	public FlashlightModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("root", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1F, -1.2F, -4.2F, 2F, 2F, 1F)
				.texOffs(0, 8).addBox(-1.5F, -1.7F, -4F, 3F, 3F, 1F)
				.texOffs(0, 0).addBox(-1F, -1.2F, -3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, -9F, 1F, -0.0873F, 0F, 0F));

		root = rootDef.bake(16, 16);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().translate(0, 0.2, 0);
		ctx.poseStack().mulPose(Vector3f.YP.rotationDegrees(-45));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}