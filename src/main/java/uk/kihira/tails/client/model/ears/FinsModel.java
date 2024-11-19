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
 * <p>The fins part model.</p>
 * <p>Model created by CogwheelCat.</p>
 * @author EnderTurret
 */
final class FinsModel extends PartModel {

	private final ModelPart root;
	private final boolean sides;

	public FinsModel(boolean sides) {
		this.sides = sides;
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		if (sides) {
			final PartDefinition root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.rotation(0F, 3.1416F, 0F));

			final PartDefinition leftFin = root.addOrReplaceChild("leftFin", CubeListBuilder.create(), PartPose.offsetAndRotation(4F, -3F, 1F, -0.0151F, -0.043F, 0.1739F));

			leftFin.addOrReplaceChild("cube_r3", CubeListBuilder.create()
					.texOffs(0, 3).addBox(-1F, -4F, 0F, 1F, 5F, 1F)
					.texOffs(4, 2).addBox(-0.5F, -5F, -4F, 0F, 6F, 5F),
					PartPose.offsetAndRotation(0F, 0F, 0F, 0.4796F, -0.0201F, 0.6496F));

			final PartDefinition rightFin = root.addOrReplaceChild("rightFin", CubeListBuilder.create(), PartPose.offsetAndRotation(-4F, -3F, 1F, -0.0151F, 0.043F, -0.1739F));

			rightFin.addOrReplaceChild("cube_r4", CubeListBuilder.create()
					.texOffs(0, 3).mirror().addBox(0F, -4F, 0F, 1F, 5F, 1F).mirror(false)
					.texOffs(4, 2).mirror().addBox(0.5F, -5F, -4F, 0F, 6F, 5F).mirror(false),
					PartPose.offsetAndRotation(0F, 0F, 0F, 0.4796F, 0.0201F, -0.6496F));
		} else {
			final PartDefinition topFin = rootDef.addOrReplaceChild("topFin", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -7F, -3F, 0F, 3.1416F, 0F));

			topFin.addOrReplaceChild("cube_r1", CubeListBuilder.create()
					.texOffs(4, -6).addBox(0F, -5.8F, -6F, 0F, 7F, 6F)
					.texOffs(0, 9).addBox(-0.5F, -4F, -1F, 1F, 3F, 1F),
					PartPose.offsetAndRotation(0F, 0F, 0F, 0.3491F, 0F, 0F));

			topFin.addOrReplaceChild("cube_r2", CubeListBuilder.create()
					.texOffs(0, 0).addBox(-0.5F, -2.1F, -1.4F, 1F, 2F, 1F),
					PartPose.offsetAndRotation(0F, -4F, -1F, 0.6981F, 0F, 0F));
		}

		root = rootDef.bake(16, 16);
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.render(root);
	}
}