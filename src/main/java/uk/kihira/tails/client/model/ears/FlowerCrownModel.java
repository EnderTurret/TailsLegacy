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
 * <p>The flower crown part model.</p>
 * <p>Model created by Dustskys, with implementation & programming by EnderTurret.</p>
 * @author EnderTurret
 */
final class FlowerCrownModel extends PartModel {

	private final ModelPart root;

	public FlowerCrownModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		PartDefinition vine = rootDef.addOrReplaceChild("vine", CubeListBuilder.create()
				.texOffs(5, 11).addBox(-2F, -7F, -5F, 4F, 1F, 1F)
				.texOffs(0, 5).addBox(-4.9F, -5F, -2F, 1F, 1F, 4F)
				.texOffs(0, 0).addBox(3.9F, -5F, -2F, 1F, 1F, 4F)
				.texOffs(9, 9).addBox(-2F, -3F, 3.9F, 4F, 1F, 1F), PartPose.offset(0F, 0F, 0F));

		vine.addOrReplaceChild("cube_r1", CubeListBuilder.create()
				.texOffs(11, 5).addBox(-1.7F, 2F, -0.5F, 3F, 1F, 1F), PartPose.offsetAndRotation(2.5F, -5.5F, 4.5F, 0F, 0F, -0.2618F));

		vine.addOrReplaceChild("cube_r2", CubeListBuilder.create()
				.texOffs(7, 13).addBox(-1.3F, 2F, -0.5F, 3F, 1F, 1F), PartPose.offsetAndRotation(-2.5F, -5.5F, 4.5F, 0F, 0F, 0.2618F));

		vine.addOrReplaceChild("cube_r3", CubeListBuilder.create()
				.texOffs(6, 0).addBox(-0.7F, -0.5F, 4.5F, 1F, 1F, 3F)
				.texOffs(0, 10).addBox(-9.3F, -0.5F, 4.5F, 1F, 1F, 3F)
				.texOffs(6, 5).addBox(-0.5F, 0.1F, -2F, 1F, 1F, 3F)
				.texOffs(11, 1).addBox(-9.5F, 0.1F, -2F, 1F, 1F, 3F), PartPose.offsetAndRotation(4.5F, -5.5F, -2.5F, -0.2618F, 0F, 0F));

		vine.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 14).addBox(0F, -7F, -4.9F, 3F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.2618F));

		vine.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(14, 7).addBox(-3F, -7F, -4.9F, 3F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.2618F));

		PartDefinition flower = rootDef.addOrReplaceChild("flower", CubeListBuilder.create().texOffs(14, 12).addBox(2F, -7F, -5.4F, 1F, 1F, 1F), PartPose.offset(0F, 0F, 0F));

		flower.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(13, 7).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(3.5F, -6.5F, -5.2F, 0F, 0.1745F, 0F));

		flower.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(14, 11).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(1.5F, -6.5F, -5.2F, 0F, -0.1745F, 0F));

		flower.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(15, 0).addBox(-0.5F, -0.5222F, 0.0425F, 1F, 1F, 0F), PartPose.offsetAndRotation(2.5F, -5.4778F, -5.2425F, -0.1745F, 0F, 0F));

		flower.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(8, 15).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(2.5F, -7.5F, -5.2F, 0.1745F, 0F, 0F));

		PartDefinition flower2 = rootDef.addOrReplaceChild("flower2", CubeListBuilder.create().texOffs(0, 7).addBox(-0.5F, -0.5015F, -0.2606F, 1F, 1F, 1F), PartPose.offset(-2F, -6.9985F, -5.1394F));

		flower2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(11, 2).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(1F, -0.0015F, -0.0606F, 0F, 0.1745F, 0F));

		flower2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(-1F, -0.0015F, -0.0606F, 0F, -0.1745F, 0F));

		flower2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 12).addBox(-0.5F, -0.5222F, 0.0425F, 1F, 1F, 0F), PartPose.offsetAndRotation(0F, 1.0207F, -0.1032F, -0.1745F, 0F, 0F));

		flower2.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(13, 0).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(0F, -1.0015F, -0.0606F, 0.1745F, 0F, 0F));

		PartDefinition flower3 = rootDef.addOrReplaceChild("flower3", CubeListBuilder.create().texOffs(0, 5).addBox(-0.2F, -0.5015F, -0.4606F, 1F, 1F, 1F), PartPose.offset(-5.2F, -5.1985F, -2.1394F));

		flower3.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(6, 6).addBox(0.0606F, -0.5F, -0.3606F, 0F, 1F, 1F), PartPose.offsetAndRotation(-0.0606F, -0.0015F, -1F, 0F, 0.1745F, 0F));

		flower3.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(0, 9).addBox(0.0606F, -0.5F, -0.5606F, 0F, 1F, 1F), PartPose.offsetAndRotation(-0.0606F, -0.0015F, 1F, 0F, -0.1745F, 0F));

		flower3.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(5, 9).addBox(0.1032F, -0.6222F, -0.4606F, 0F, 1F, 1F), PartPose.offsetAndRotation(-0.1032F, 1.0207F, 0F, 0F, 0F, 0.1745F));

		flower3.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(7, 9).addBox(0.0606F, -0.4F, -0.4606F, 0F, 1F, 1F), PartPose.offsetAndRotation(-0.0606F, -1.0015F, 0F, 0F, 0F, -0.1745F));

		PartDefinition flower4 = rootDef.addOrReplaceChild("flower4", CubeListBuilder.create().texOffs(0, 2).addBox(-0.8F, -0.5015F, -0.4606F, 1F, 1F, 1F), PartPose.offset(5.2F, -4.6985F, 0.2606F));

		flower4.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(6, 0).addBox(-0.0606F, -0.5F, -0.3606F, 0F, 1F, 1F), PartPose.offsetAndRotation(0.0606F, -0.0015F, -1F, 0F, -0.1745F, 0F));

		flower4.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(6, 1).addBox(-0.0606F, -0.5F, -0.5606F, 0F, 1F, 1F), PartPose.offsetAndRotation(0.0606F, -0.0015F, 1F, 0F, 0.1745F, 0F));

		flower4.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(6, 4).addBox(-0.1032F, -0.6222F, -0.4606F, 0F, 1F, 1F), PartPose.offsetAndRotation(0.1032F, 1.0207F, 0F, 0F, 0F, -0.1745F));

		flower4.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(6, 5).addBox(-0.0606F, -0.4F, -0.4606F, 0F, 1F, 1F), PartPose.offsetAndRotation(0.0606F, -1.0015F, 0F, 0F, 0F, 0.1745F));

		PartDefinition flower5 = rootDef.addOrReplaceChild("flower5", CubeListBuilder.create().texOffs(0, 0).addBox(2F, -7F, 4.4F, 1F, 1F, 1F), PartPose.offset(-1.6F, 3.3F, 0F));

		flower5.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(6, 0).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(3.5F, -6.5F, 5.2F, 0F, -0.1745F, 0F));

		flower5.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(0, 11).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(1.5F, -6.5F, 5.2F, 0F, 0.1745F, 0F));

		flower5.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(11, 0).addBox(-0.5F, -0.5222F, -0.0425F, 1F, 1F, 0F), PartPose.offsetAndRotation(2.5F, -5.4778F, 5.2425F, 0.1745F, 0F, 0F));

		flower5.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(11, 1).addBox(-0.5F, -0.5F, 0F, 1F, 1F, 0F), PartPose.offsetAndRotation(2.5F, -7.5F, 5.2F, -0.1745F, 0F, 0F));

		root = rootDef.bake(32, 32);
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}