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
 * <p>The beanie part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class BeanieModel extends PartModel {

	private final ModelPart root;

	public BeanieModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition beanie = rootDef.addOrReplaceChild("beanie", CubeListBuilder.create()
				.texOffs(0, 13).addBox(-3.99F, -0.3F, -1F, 8F, 2F, 2F)
				.texOffs(12, 23).addBox(-5.2F, 5.7F, 0.9F, 2F, 2F, 4F)
				.texOffs(22, 10).addBox(3.2F, 5.7F, 0.9F, 2F, 2F, 4F)
				.texOffs(18, 6).addBox(-3F, 7F, 6F, 6F, 2F, 2F)
				.texOffs(0, 0).addBox(-4F, 0F, 1F, 8F, 1F, 5F)
				.texOffs(0, 6).addBox(-4F, 1F, 6F, 8F, 6F, 1F)
				.texOffs(0, 17).addBox(-5F, 1F, 1F, 1F, 6F, 5F)
				.texOffs(15, 12).addBox(4F, 1F, 1F, 1F, 6F, 5F), PartPose.offset(0F, -9F, -2F));

		beanie.addOrReplaceChild("cube_r1", CubeListBuilder.create()
				.texOffs(21, 0).addBox(-1.3F, -1.2F, -1.3F, 2F, 2F, 2F)
				.texOffs(27, 2).addBox(-9.1F, -1.2F, -1.3F, 2F, 2F, 2F), PartPose.offsetAndRotation(4.2F, 7.7F, 6F, -0.0873F, 0F, 0F));

		beanie.addOrReplaceChild("cube_r2", CubeListBuilder.create()
				.texOffs(24, 23).addBox(-1.2F, -3.1F, -0.1F, 2F, 5F, 2F)
				.texOffs(27, 16).addBox(-9.8F, -3.1F, -0.1F, 2F, 5F, 2F), PartPose.offsetAndRotation(4.5F, 5F, 0.4F, 0.2618F, 0F, 0F));

		beanie.addOrReplaceChild("cube_r3", CubeListBuilder.create()
				.texOffs(7, 17).addBox(-2F, -1.3F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(5F, 1F, 0.2F, 0F, 0F, -0.6545F));

		beanie.addOrReplaceChild("cube_r4", CubeListBuilder.create()
				.texOffs(33, 0).addBox(0F, -1.3F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-5F, 1F, 0.2F, 0F, 0F, 0.6545F));

		root = rootDef.bake(64, 32);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().mulPose(new Quaternionf().rotateY(-25 * Mth.DEG_TO_RAD));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.poseStack().pushPose();

		ctx.poseStack().scale(1.01f, 1.01f, 1.01f);

		ctx.render(root);

		ctx.poseStack().popPose();
	}
}