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
 * The model for fox ears.
 */
public class FoxEarsModel extends PartModel {

	private final ModelPart leftEarInnerSmall;
	private final ModelPart leftEarInnerBig;
	private final ModelPart rightEarInnerSmall;
	private final ModelPart rightEarInnerBig;
	private final ModelPart leftEarInnerEdge;
	private final ModelPart rightEarInnerEdge;
	private final ModelPart leftEarMiddleEdge;
	private final ModelPart rightEarMiddleEdge;
	private final ModelPart leftEarTopEdge;
	private final ModelPart rightEarTopEdge;
	private final ModelPart leftEarOuterEdge;
	private final ModelPart rightEarOuterEdge;
	private final ModelPart leftEarBottomEdge;
	private final ModelPart rightEarBottomEdge;
	private final ModelPart leftEarBackBig;
	private final ModelPart rightEarBackBig;
	private final ModelPart leftEarBackSmall;
	private final ModelPart lightEarBackSmall;

	public FoxEarsModel() {
		texWidth = 16;
		texHeight = 32;

		leftEarInnerSmall = new ModelPart(this, 0, 16);
		leftEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
		leftEarInnerSmall.setPos(4F, -11F, 1F);
		leftEarInnerSmall.mirror = true;

		leftEarInnerBig = new ModelPart(this, 4, 16);
		leftEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
		leftEarInnerBig.setPos(3F, -10F, 1F);
		leftEarInnerBig.mirror = true;

		rightEarInnerSmall = new ModelPart(this, 0, 19);
		rightEarInnerSmall.addBox(0F, 0F, 0F, 1, 1, 1);
		rightEarInnerSmall.setPos(-5F, -11F, 1F);

		rightEarInnerBig = new ModelPart(this, 4, 19);
		rightEarInnerBig.addBox(0F, 0F, 0F, 2, 2, 1);
		rightEarInnerBig.setPos(-5F, -10F, 1F);

		leftEarInnerEdge = new ModelPart(this, 0, 0);
		leftEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
		leftEarInnerEdge.setPos(2F, -10F, 1F);
		leftEarInnerEdge.mirror = true;

		rightEarInnerEdge = new ModelPart(this, 0, 4);
		rightEarInnerEdge.addBox(0F, 0F, 0F, 1, 3, 1);
		rightEarInnerEdge.setPos(-3F, -10F, 1F);

		leftEarMiddleEdge = new ModelPart(this, 4, 0);
		leftEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
		leftEarMiddleEdge.setPos(3F, -11F, 1F);
		leftEarMiddleEdge.mirror = true;

		rightEarMiddleEdge = new ModelPart(this, 4, 2);
		rightEarMiddleEdge.addBox(0F, 0F, 0F, 1, 1, 1);
		rightEarMiddleEdge.setPos(-4F, -11F, 1F);

		leftEarTopEdge = new ModelPart(this, 4, 4);
		leftEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
		leftEarTopEdge.setPos(4F, -12F, 1F);
		leftEarTopEdge.mirror = true;

		rightEarTopEdge = new ModelPart(this, 4, 6);
		rightEarTopEdge.addBox(0F, 0F, 0F, 1, 1, 1);
		rightEarTopEdge.setPos(-5F, -12F, 1F);

		leftEarOuterEdge = new ModelPart(this, 0, 8);
		leftEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
		leftEarOuterEdge.setPos(5F, -11F, 1F);
		leftEarOuterEdge.mirror = true;

		rightEarOuterEdge = new ModelPart(this, 0, 12);
		rightEarOuterEdge.addBox(0F, 0F, 0F, 1, 3, 1);
		rightEarOuterEdge.setPos(-6F, -11F, 1F);

		leftEarBottomEdge = new ModelPart(this, 10, 14);
		leftEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
		leftEarBottomEdge.setPos(3F, -8F, 1F);
		leftEarBottomEdge.mirror = true;

		rightEarBottomEdge = new ModelPart(this, 10, 12);
		rightEarBottomEdge.addBox(0F, 0F, 0F, 2, 1, 1);
		rightEarBottomEdge.setPos(-5F, -8F, 1F);

		leftEarBackBig = new ModelPart(this, 4, 8);
		leftEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
		leftEarBackBig.setPos(4F, -11F, 2F);
		leftEarBackBig.mirror = true;

		rightEarBackBig = new ModelPart(this, 4, 12);
		rightEarBackBig.addBox(0F, 0F, 0F, 1, 3, 1);
		rightEarBackBig.setPos(-5F, -11F, 2F);

		leftEarBackSmall = new ModelPart(this, 8, 0);
		leftEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
		leftEarBackSmall.setPos(3F, -10F, 2F);
		leftEarBackSmall.mirror = true;

		lightEarBackSmall = new ModelPart(this, 8, 3);
		lightEarBackSmall.addBox(0F, 0F, 0F, 1, 2, 1);
		lightEarBackSmall.setPos(-4F, -10F, 2F);
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		matrixStackIn.pushPose();

		if (subtype == 1) {
			matrixStackIn.translate(0f, 0f, -0.0625f);
			matrixStackIn.translate(-0.4375f, 0f, 0f);
		}

		leftEarInnerSmall.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarInnerBig.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarInnerEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarMiddleEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarTopEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarOuterEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarBottomEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarBackBig.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarBackSmall.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (subtype == 1)
			matrixStackIn.translate(0.875f, 0f, 0f);

		rightEarInnerSmall.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarInnerBig.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarInnerEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarMiddleEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarTopEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarOuterEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarBottomEdge.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarBackBig.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		lightEarBackSmall.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (subtype == 1) {
			matrixStackIn.translate(-0.4375f, 0f, 0f);
			matrixStackIn.translate(0f, 0f, 0.0625f);
		}

		matrixStackIn.popPose();
	}
}
