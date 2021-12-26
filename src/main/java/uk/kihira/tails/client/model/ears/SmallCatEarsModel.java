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
 * The model for small cat ears.
 */
public class SmallCatEarsModel extends PartModel {

	private final ModelPart leftEarBottom;
	private final ModelPart leftEarRearLayer1;
	private final ModelPart leftEarRearBottom;
	private final ModelPart leftEarLayer1;
	private final ModelPart leftEarLayer3;
	private final ModelPart leftEarLayer2;
	private final ModelPart rightEarBottom;
	private final ModelPart rightEarLayer1;
	private final ModelPart rightEarRearLayer1;
	private final ModelPart rightEarRearBottom;
	private final ModelPart rightEarLayer2;
	private final ModelPart rightEarLayer3;

	public SmallCatEarsModel() {
		texWidth = 64;
		texHeight = 32;
		rightEarRearLayer1 = new ModelPart(this, 13, 14);
		rightEarRearLayer1.setPos(-3.0F, -8.0F, 0.0F);
		rightEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
		leftEarRearLayer1 = new ModelPart(this, 0, 14);
		leftEarRearLayer1.setPos(4.0F, -8.0F, 0.0F);
		leftEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
		rightEarRearBottom = new ModelPart(this, 13, 12);
		rightEarRearBottom.setPos(-3.0F, -8.0F, 0.0F);
		rightEarRearBottom.addBox(-1.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
		leftEarLayer1 = new ModelPart(this, 0, 2);
		leftEarLayer1.setPos(4.0F, -8.0F, 0.0F);
		leftEarLayer1.addBox(-3.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
		leftEarLayer2 = new ModelPart(this, 0, 4);
		leftEarLayer2.setPos(4.0F, -8.0F, 0.0F);
		leftEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
		rightEarLayer1 = new ModelPart(this, 13, 2);
		rightEarLayer1.setPos(-3.0F, -8.0F, 0.0F);
		rightEarLayer1.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
		rightEarBottom = new ModelPart(this, 13, 0);
		rightEarBottom.setPos(-4.0F, -8.0F, 0.0F);
		rightEarBottom.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		leftEarRearBottom = new ModelPart(this, 0, 12);
		leftEarRearBottom.setPos(4.0F, -8.0F, 0.0F);
		leftEarRearBottom.addBox(-2.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
		rightEarLayer3 = new ModelPart(this, 13, 6);
		rightEarLayer3.setPos(-2.0F, -8.0F, 0.0F);
		rightEarLayer3.addBox(-2.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
		leftEarBottom = new ModelPart(this, 0, 0);
		leftEarBottom.setPos(4.0F, -8.0F, 0.0F);
		leftEarBottom.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		rightEarLayer2 = new ModelPart(this, 13, 4);
		rightEarLayer2.setPos(-3.0F, -8.0F, 0.0F);
		rightEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
		leftEarLayer3 = new ModelPart(this, 0, 6);
		leftEarLayer3.setPos(4.0F, -8.0F, 0.0F);
		leftEarLayer3.addBox(-1.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		rightEarRearLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarRearLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarRearBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarLayer2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarRearBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarLayer3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarLayer2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarLayer3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
