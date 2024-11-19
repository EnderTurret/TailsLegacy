/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 EnderTurret
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
import net.minecraft.util.Mth;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The ram horns part model.</p>
 * <p>Model created by CogwheelCat.</p>
 * @author EnderTurret
 */
final class RamHornsModel extends PartModel {

	private final ModelPart root;

	public RamHornsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition right = rootDef.addOrReplaceChild("right", CubeListBuilder.create(), PartPose.offset(-3F, -8F, 0F));

		right.addOrReplaceChild("5_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.4F, -5.9F, -1.8998F, 3F, 7F, 3F), PartPose.offsetAndRotation(-3F, -2F, -3F, 2.5504F, 0.5041F, -0.1628F));
		right.addOrReplaceChild("4_r1", CubeListBuilder.create().texOffs(24, 0).addBox(-0.5105F, -4.5752F, -2.5F, 2F, 5F, 2F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-4F, 2F, -5F, -1.5791F, 0.544F, 1.901F));
		right.addOrReplaceChild("3_r1", CubeListBuilder.create().texOffs(0, 10).addBox(0F, -1.8676F, 0F, 1F, 3F, 1F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-3F, 5F, -1F, -1.4471F, 0.1949F, 1.9039F));
		right.addOrReplaceChild("2_r1", CubeListBuilder.create().texOffs(12, 0).addBox(-1.6F, -4.4F, -2.1F, 3F, 5F, 3F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0F, -2F, 0F, 1.2008F, 0.3956F, -0.3559F));
		right.addOrReplaceChild("1_r1", CubeListBuilder.create().texOffs(12, 8).addBox(-1F, -2F, -1F, 3F, 4F, 3F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0.3327F, 0.1171F, -0.2828F));

		final PartDefinition left = rootDef.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offset(3F, -8F, 0F));

		left.addOrReplaceChild("5_r2", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.6F, -5.9F, -1.8998F, 3F, 7F, 3F).mirror(false), PartPose.offsetAndRotation(3F, -2F, -3F, 2.5504F, -0.5041F, 0.1628F));
		left.addOrReplaceChild("4_r2", CubeListBuilder.create().texOffs(24, 0).mirror().addBox(-1.4895F, -4.5752F, -2.5F, 2F, 5F, 2F, new CubeDeformation(0.2F)).mirror(false), PartPose.offsetAndRotation(4F, 2F, -5F, -1.5791F, -0.544F, -1.901F));
		left.addOrReplaceChild("3_r2", CubeListBuilder.create().texOffs(0, 10).mirror().addBox(-1F, -1.8676F, 0F, 1F, 3F, 1F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(3F, 5F, -1F, -1.4471F, -0.1949F, -1.9039F));
		left.addOrReplaceChild("2_r2", CubeListBuilder.create().texOffs(12, 0).mirror().addBox(-1.4F, -4.4F, -2.1F, 3F, 5F, 3F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(0F, -2F, 0F, 1.2008F, -0.3956F, 0.3559F));
		left.addOrReplaceChild("1_r2", CubeListBuilder.create().texOffs(12, 8).mirror().addBox(-2F, -2F, -1F, 3F, 4F, 3F, new CubeDeformation(0.2F)).mirror(false), PartPose.offsetAndRotation(0F, 0F, 0F, 0.3327F, -0.1171F, 0.2828F));

		root = rootDef.bake(32, 16);
		root.yRot = Mth.PI;
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}