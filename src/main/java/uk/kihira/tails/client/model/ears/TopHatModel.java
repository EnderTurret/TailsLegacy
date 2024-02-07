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
 * <p>The top hat part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class TopHatModel extends PartModel {

	private final ModelPart root;

	public TopHatModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition root = rootDef.addOrReplaceChild("root", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5F, -9F, -5F, 10F, 1F, 10F)
				.texOffs(30, 0).addBox(-4.5F, -10.4F, -3.9F, 1F, 2F, 8F)
				.texOffs(15, 28).addBox(3.5F, -10.4F, -4.1F, 1F, 2F, 8F)
				.texOffs(33, 34).addBox(-4.1F, -10.5F, -4.5F, 8F, 2F, 1F)
				.texOffs(25, 31).addBox(-3.9F, -10.5F, 3.5F, 8F, 2F, 1F)
				.texOffs(0, 11).addBox(-4F, -17F, -4F, 8F, 8F, 8F), PartPose.ZERO);

		root.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(21, 16).addBox(-0.9645F, 0.1589F, -5.5F, 2F, 1F, 11F), PartPose.offsetAndRotation(5.9645F, -8.6589F, 0F, 0F, 0F, 0.5236F));
		root.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 11).addBox(-5.1F, 0.1589F, -1.0355F, 10F, 1F, 2F), PartPose.offsetAndRotation(0F, -8.6589F, -5.9645F, 0.5236F, 0F, 0F));
		root.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(25, 28).addBox(-4.9F, 0.1589F, -0.9645F, 10F, 1F, 2F), PartPose.offsetAndRotation(0F, -8.6589F, 5.9645F, -0.5236F, 0F, 0F));
		root.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 27).addBox(-1.0355F, 0.1589F, -5.5F, 2F, 1F, 11F), PartPose.offsetAndRotation(-5.9645F, -8.6589F, 0F, 0F, 0F, -0.5236F));

		this.root = rootDef.bake(64, 64);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().translate(0, 0.4, 0);
		ctx.poseStack().mulPose(new Quaternionf().rotateY(-45 * Mth.DEG_TO_RAD));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}