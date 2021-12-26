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
import net.minecraft.world.entity.LivingEntity;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for panda ears.
 */
public class PandaEarsModel extends PartModel {

	private final ModelPart leftEar;
	private final ModelPart rightEar;

	public PandaEarsModel() {
		texWidth = 32;
		texHeight = 32;

		leftEar = new ModelPart(this, 0, 0);
		leftEar.addBox(-2F, -2F, 0F, 3, 3, 1);
		leftEar.setPos(-4F, -8F, 0F);
		leftEar.setTexSize(32, 32);
		leftEar.mirror = true;

		rightEar = new ModelPart(this, 0, 4);
		rightEar.addBox(-1F, -2F, 0F, 3, 3, 1);
		rightEar.setPos(4F, -8F, 0F);
		rightEar.setTexSize(32, 32);
		rightEar.mirror = true;
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		leftEar.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEar.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
