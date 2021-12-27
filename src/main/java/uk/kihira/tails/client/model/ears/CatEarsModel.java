/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for cat ears.
 */
public class CatEarsModel extends PartModel {

	private final ModelPart root;
	private final ModelPart leftEar;
	private final ModelPart rightEar;

	public CatEarsModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();
		rootDef.addOrReplaceChild("leftEar", CubeListBuilder.create()
				.mirror()
				.texOffs(0, 0).addBox("leftEarBottom", 0, 0, 0, 1, 1, 1)
				.texOffs(0, 16).addBox("leftEarRearTop", 0, -3, 1, 1, 1, 1)
				.texOffs(0, 14).addBox("leftEarRearLayer1", -1, -2, 1, 2, 1, 1)
				.texOffs(0, 12).addBox("leftEarRearBottom", -2, -1, 1, 3, 1, 1)
				.texOffs(0, 2).addBox("leftEarLayer1", -3, -1, 0, 5, 1, 1)
				.texOffs(0, 8).addBox("leftEarTop", 0, -4, 0, 1, 1, 1)
				.texOffs(0, 6).addBox("leftEarLayer3", -1, -3, 0, 3, 1, 1)
				.texOffs(0, 4).addBox("leftEarLayer2", -2, -2, 0, 4, 1, 1)
				, PartPose.offset(4, -8, 0));
		rootDef.addOrReplaceChild("rightEar", CubeListBuilder.create()
				.mirror()
				.texOffs(13, 0).addBox("leftEarBottom", -1, 0, 0, 1, 1, 1)
				.texOffs(13, 16).addBox("leftEarRearTop", -1, -3, 1, 1, 1, 1)
				.texOffs(13, 14).addBox("leftEarRearLayer1", -1, -2, 1, 2, 1, 1)
				.texOffs(13, 12).addBox("leftEarRearBottom", -1, -1, 1, 3, 1, 1)
				.texOffs(13, 2).addBox("leftEarLayer1", -2, -1, 0, 5, 1, 1)
				.texOffs(13, 8).addBox("leftEarTop", -1, -4, 0, 1, 1, 1)
				.texOffs(13, 6).addBox("leftEarLayer3", -2, -3, 0, 3, 1, 1)
				.texOffs(13, 4).addBox("leftEarLayer2", -2, -2, 0, 4, 1, 1)
				, PartPose.offset(-4, -8, 0));
		root = rootDef.bake(64, 32);
		leftEar = root.getChild("leftEar");
		rightEar = root.getChild("rightEar");
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		leftEar.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEar.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}