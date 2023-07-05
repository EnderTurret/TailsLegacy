/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
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
 * <p>The traffic cone part model.</p>
 * <p>Model created by Dustskys, with implementation & programming by EnderTurret.</p>
 * @author EnderTurret
 */
final class TrafficConeModel extends PartModel {

	private final ModelPart root;

	public TrafficConeModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("root", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1F, -22F, -1F, 2F, 4F, 2F)
				.texOffs(18, 12).addBox(-4F, -9F, -6F, 8F, 1F, 2F)
				.texOffs(0, 0).addBox(-6F, -9F, -4F, 12F, 1F, 8F)
				.texOffs(18, 9).addBox(-4F, -9F, 4F, 8F, 1F, 2F)
				.texOffs(26, 15).addBox(-3F, -11F, -4F, 6F, 2F, 1F)
				.texOffs(10, 22).addBox(-4F, -11F, -3F, 1F, 2F, 6F)
				.texOffs(18, 15).addBox(3F, -11F, -3F, 1F, 2F, 6F)
				.texOffs(18, 23).addBox(-3F, -11F, 3F, 6F, 2F, 1F)
				.texOffs(0, 9).addBox(-3F, -14F, -3F, 6F, 5F, 6F)
				.texOffs(0, 20).addBox(-2F, -18F, -2F, 4F, 4F, 4F), PartPose.ZERO);

		root = rootDef.bake(64, 64);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().translate(0, 0.55, 0);
		ctx.poseStack().mulPose(new Quaternionf().rotateY(-45 * Mth.DEG_TO_RAD));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}