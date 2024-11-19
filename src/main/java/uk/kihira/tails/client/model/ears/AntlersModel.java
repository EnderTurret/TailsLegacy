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
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.model.ModelSerializer;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.Part.SubType;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The antlers part model.</p>
 * <p>Model created by CogwheelCat.</p>
 * @author EnderTurret
 */
final class AntlersModel extends PartModel {

	private final ModelPart root;

	public AntlersModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition leftAntler = rootDef.addOrReplaceChild("leftAntler", CubeListBuilder.create(), PartPose.offset(-2F, -9F, 0F));

		leftAntler.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(4, 0).addBox(-1F, -3F, -1F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.3491F));
		leftAntler.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0864F, -2.8389F, -1F, 1F, 4F, 1F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-0.9468F, -2.9838F, 0F, 0F, 0F, -0.0436F));
		leftAntler.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(4, 5).addBox(-1.2F, -4F, -1F, 1F, 4F, 1F), PartPose.offsetAndRotation(-0.9415F, -3.0003F, 0F, 0F, 0F, -1.1345F));
		leftAntler.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 10).addBox(-0.9171F, -1.9058F, -0.4342F, 1F, 3F, 1F), PartPose.offsetAndRotation(-4.2126F, -4.1434F, 0.0325F, -1.6988F, 0.1143F, -0.8137F));
		leftAntler.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(4, 11).addBox(-1.9F, -2.8F, -0.1384F, 1F, 2F, 1F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-4.3496F, -4.1388F, 0.1384F, -1.6418F, 0.8087F, -0.8896F));

		final PartDefinition leftTopBranch = leftAntler.addOrReplaceChild("leftTopBranch", CubeListBuilder.create(), PartPose.offset(-4.9276F, -4.9694F, -1.0462F));

		leftTopBranch.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 5).addBox(-1.3F, -3.8F, 0.4F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, -1.0036F, 0F, -1.1345F));
		leftTopBranch.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(4, 11).addBox(-0.6115F, -2.1699F, 0.2F, 1F, 2F, 1F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-2.0608F, 0.2394F, 2.9596F, -1.2983F, 0.6364F, -0.9699F));

		final PartDefinition rightAntler = rootDef.addOrReplaceChild("rightAntler", CubeListBuilder.create(), PartPose.offset(2F, -9F, 0F));

		rightAntler.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(4, 0).mirror().addBox(0F, -3F, -1F, 1F, 4F, 1F).mirror(false), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.3491F));
		rightAntler.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0864F, -2.8389F, -1F, 1F, 4F, 1F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(0.9468F, -2.9838F, 0F, 0F, 0F, 0.0436F));
		rightAntler.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(4, 5).mirror().addBox(0.2F, -4F, -1F, 1F, 4F, 1F).mirror(false), PartPose.offsetAndRotation(0.9415F, -3.0003F, 0F, 0F, 0F, 1.1345F));
		rightAntler.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 10).mirror().addBox(-0.0829F, -1.9058F, -0.4342F, 1F, 3F, 1F).mirror(false), PartPose.offsetAndRotation(4.2126F, -4.1434F, 0.0325F, -1.6988F, -0.1143F, 0.8137F));
		rightAntler.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(4, 11).mirror().addBox(0.9F, -2.8F, -0.1384F, 1F, 2F, 1F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(4.3496F, -4.1388F, 0.1384F, -1.6418F, -0.8087F, 0.8896F));

		final PartDefinition rightTopBranch = rightAntler.addOrReplaceChild("rightTopBranch", CubeListBuilder.create(), PartPose.offset(7F, -5F, 2F));

		rightTopBranch.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(4, 11).mirror().addBox(-0.3885F, -2.1699F, 0.2F, 1F, 2F, 1F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(-0.0115F, 0.2699F, -0.0866F, -1.2983F, -0.6364F, 0.9699F));
		rightTopBranch.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 5).mirror().addBox(0.3F, -3.8F, 0.4F, 1F, 4F, 1F).mirror(false), PartPose.offsetAndRotation(-2.0724F, 0.0306F, -3.0462F, -1.0036F, 0F, 1.1345F));

		root = ModelSerializer.bake(rootDef, 16, 16, "ears/antlers");
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, SubType subType, float headPitch) {
		root.yRot = "forward".equals(subType.id()) ? Mth.PI : 0;
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}