/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.tail;

import java.util.ArrayList;
import java.util.List;

import org.joml.Quaternionf;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The scorpion tail part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class ScorpionTailModel extends PartModel {

	private final ModelPart root;

	public ScorpionTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition tail = rootDef.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0, 2.5F, 7.2F));

		tail.addOrReplaceChild("cube_r0", CubeListBuilder.create().texOffs(11, 12).addBox(-1.5F, -11F, -2F, 3F, 5F, 3F), PartPose.ZERO);
		tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-1.5F, -1.5F, -2F, 3F, 3F, 4F), PartPose.offsetAndRotation(0F, -12.5F, -4F, -0.1745F, 0F, 0F));
		tail.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(13, 5).addBox(-1.6F, -6F, -1.5F, 3F, 4F, 3F), PartPose.offsetAndRotation(0F, -8F, 0.5F, 0.6109F, 0F, 0F));
		tail.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 17).addBox(-1.6F, 1F, -1.5F, 3F, 4F, 3F), PartPose.offsetAndRotation(0F, -8F, -0.5F, -0.3491F, 0F, 0F));
		tail.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -6F, -5F, 3F, 3F, 5F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.2618F, 0F, 0F));
		tail.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(12, 20).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, -3F, -7F, 0.4363F, 0F, 0F));
		tail.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(11, 0).addBox(-1F, 0.5F, -1.5F, 2F, 1F, 3F), PartPose.offsetAndRotation(0F, -13.5F, -7F, 0.6109F, 0F, 0F));

		root = rootDef.bake(32, 32);
		root.xScale = root.yScale = root.zScale = 1.1F;
		root.xRot = -10 * Mth.DEG_TO_RAD;

		final ModelPart _tail = root.getChild("tail");
		final List<ModelPart> parts = new ArrayList<>();

		for (int i = 0; i <= 6; i++)
			parts.add(_tail.getChild("cube_r" + i));

		config = new PartConfiguration(List.copyOf(parts));

		for (ModelPart part : parts)
			config.setParents(part, root, _tail);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().scale(0.8F, 0.8F, 0.8F);
		ctx.poseStack().translate(-0.25, 0.9, 0);
		ctx.poseStack().mulPose(new Quaternionf().rotateY(140 * Mth.DEG_TO_RAD));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}