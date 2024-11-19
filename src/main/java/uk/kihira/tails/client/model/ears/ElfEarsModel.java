/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * <p>The elf ears part model.</p>
 * <p>Model created by Dustskys.</p>
 * @author EnderTurret
 */
final class ElfEarsModel extends PartModel {

	private final ModelPart root;
	private final ModelPart ears;
	private final ModelPart feathers;

	public ElfEarsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition ears = rootDef.addOrReplaceChild("ears", CubeListBuilder.create(), PartPose.ZERO);

		final PartDefinition right = ears.addOrReplaceChild("right", CubeListBuilder.create().texOffs(4, 6).addBox(-4.5F, -3F, -3F, 1F, 2F, 2F), PartPose.offset(0.4F, 0F, 0F));

		right.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.9091F, -1F, 1F, 2F, 2F), PartPose.offsetAndRotation(-4.3612F, -2.4909F, -0.8956F, 0.5555F, -0.2784F, -0.1012F));
		right.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(8, 0).addBox(-0.6767F, -0.3675F, -1F, 1F, 1F, 2F), PartPose.offsetAndRotation(-4.6233F, -3.6325F, 0.4723F, -2.4573F, -0.3161F, -0.1206F));

		final PartDefinition left = ears.addOrReplaceChild("left", CubeListBuilder.create().texOffs(4, 2).addBox(3.5F, -3F, -3F, 1F, 2F, 2F), PartPose.offset(-0.4F, 0F, 0F));

		left.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, -0.9091F, -1F, 1F, 2F, 2F), PartPose.offsetAndRotation(4.3612F, -2.4909F, -0.8956F, 0.5555F, 0.2784F, 0.1012F));
		left.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(8, 4).addBox(-0.3233F, -0.3675F, -1F, 1F, 1F, 2F), PartPose.offsetAndRotation(4.6233F, -3.6325F, 0.4723F, -2.4573F, 0.3161F, 0.1206F));

		final PartDefinition feathers = rootDef.addOrReplaceChild("feathers", CubeListBuilder.create(), PartPose.offset(-4.0009F, -3.0029F, 1.0916F));

		feathers.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(1, 8).mirror().addBox(0.0098F, -0.3279F, -0.6319F, 0F, 1F, 2F).mirror(false), PartPose.offsetAndRotation(8.391F, 2.0308F, -0.9596F, 2.9671F, 0.3142F, 0.1047F));
		feathers.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 7).mirror().addBox(0.1991F, -0.5F, -1.9F, 0F, 1F, 3F).mirror(false), PartPose.offsetAndRotation(8.6018F, 0F, 0F, -2.6616F, 0.3142F, 0.1047F));
		feathers.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 7).mirror().addBox(0.2991F, -1.5F, -1F, 0F, 1F, 3F).mirror(false), PartPose.offsetAndRotation(8.6018F, 0F, 0F, -3.098F, 0.3142F, 0.1047F));
		feathers.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(1, 8).addBox(-0.0098F, -0.3279F, -0.6319F, 0F, 1F, 2F), PartPose.offsetAndRotation(-0.3893F, 2.0308F, -0.9596F, 2.9671F, -0.3142F, -0.1047F));
		feathers.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 7).addBox(-0.2991F, -1.5F, -1F, 0F, 1F, 3F), PartPose.offsetAndRotation(-0.6F, 0F, 0F, -3.098F, -0.3142F, -0.1047F));
		feathers.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 7).addBox(-0.1991F, -0.5F, -1.9F, 0F, 1F, 3F), PartPose.offsetAndRotation(-0.6F, 0F, 0F, -2.6616F, -0.3142F, -0.1047F));

		root = rootDef.bake(16, 16);
		this.ears = root.getChild("ears");
		this.feathers = root.getChild("feathers");
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(ears);

		if ("with_feathers".equals(ctx.info().getSubType().id()))
			ctx.render(feathers);
	}
}