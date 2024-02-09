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
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The leaves part model.</p>
 * <p>Model created by CogwheelCat.</p>
 * @author EnderTurret
 */
final class LeavesModel extends PartModel {

	private final ModelPart root;

	public LeavesModel() {
		final PartDefinition _rootDef = new MeshDefinition().getRoot();

		final PartDefinition rootDef = _rootDef.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offsetAndRotation(5F, -6F, -1F, 0F, 3.1416F, 0F));

		final PartDefinition back = rootDef.addOrReplaceChild("back", CubeListBuilder.create(), PartPose.offsetAndRotation(5F, 0F, -5F, 0F, -1.5708F, 0F));

		back.addOrReplaceChild("cube_r1", CubeListBuilder.create()
				.texOffs(0, 5).addBox(1.1801F, -0.2244F, -4F, 0F, 4F, 3F)
				.texOffs(0, 1).addBox(0.1801F, -2.2244F, 1F, 0F, 4F, 3F)
				.texOffs(0, -3).addBox(2.1801F, 0.7756F, 0F, 0F, 4F, 3F),
				PartPose.rotation(0F, 0F, 0.5672F));

		final PartDefinition top = back.addOrReplaceChild("top", CubeListBuilder.create(), PartPose.ZERO);

		top.addOrReplaceChild("cube_r2", CubeListBuilder.create()
				.texOffs(0, 1).addBox(-3.8199F, -4.2244F, 0F, 0F, 4F, 3F)
				.texOffs(0, -3).addBox(-4.8199F, -6.2244F, -3F, 0F, 4F, 3F),
				PartPose.rotation(0F, 0F, 1.9635F));

		final PartDefinition left = rootDef.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offsetAndRotation(10F, 0F, 0F, 0F, 3.1416F, 0F));

		left.addOrReplaceChild("cube_r3", CubeListBuilder.create()
				.texOffs(0, 5).addBox(0.1801F, -2.2244F, 1F, 0F, 4F, 3F)
				.texOffs(0, 1).addBox(2.1801F, 0.7756F, 0F, 0F, 4F, 3F)
				.texOffs(0, -3).addBox(1.1801F, -0.2244F, -4F, 0F, 4F, 3F),
				PartPose.rotation(0F, 0F, 0.5672F));

		final PartDefinition right = rootDef.addOrReplaceChild("right", CubeListBuilder.create(), PartPose.ZERO);

		right.addOrReplaceChild("cube_r4", CubeListBuilder.create()
				.texOffs(0, 5).addBox(2.1801F, 0.7756F, 0F, 0F, 4F, 3F)
				.texOffs(0, 1).addBox(1.1801F, -0.2244F, -4F, 0F, 4F, 3F)
				.texOffs(0, -3).addBox(0.1801F, -2.2244F, 1F, 0F, 4F, 3F),
				PartPose.rotation(0F, 0F, 0.5672F));

		root = _rootDef.bake(16, 16);
		root.z = 1;
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().scale(0.9F, 0.9F, 0.9F);
		ctx.poseStack().mulPose(new Quaternionf().rotateY(10 * Mth.DEG_TO_RAD));
		ctx.poseStack().translate(-0.1, -0.1, 0);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}