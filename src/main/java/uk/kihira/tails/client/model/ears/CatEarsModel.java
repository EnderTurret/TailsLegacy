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
 * The model for cat ears.
 */
public class CatEarsModel extends PartModel {

	private final ModelPart leftEarBottom;
	private final ModelPart leftEarRearTop;
	private final ModelPart leftEarRearLayer1;
	private final ModelPart leftEarRearBottom;
	private final ModelPart leftEarLayer1;
	private final ModelPart leftEarTop;
	private final ModelPart leftEarLayer3;
	private final ModelPart leftEarLayer2;
	private final ModelPart rightEarBottom;
	private final ModelPart rightEarLayer1;
	private final ModelPart rightEarRearTop;
	private final ModelPart rightEarRearLayer1;
	private final ModelPart rightEarRearBottom;
	private final ModelPart rightEarLayer2;
	private final ModelPart rightEarTop;
	private final ModelPart rightEarLayer3;

	public CatEarsModel() {
		texWidth = 64;
		texHeight = 32;

		leftEarBottom = new ModelPart(this, 0, 0);
		leftEarBottom.addBox(0F, 0F, 0F, 1, 1, 1);
		leftEarBottom.setPos(4F, -8F, 0F);
		leftEarBottom.setTexSize(64, 32);
		leftEarBottom.mirror = true;

		leftEarRearTop = new ModelPart(this, 0, 16);
		leftEarRearTop.addBox(0F, -3F, 1F, 1, 1, 1);
		leftEarRearTop.setPos(4F, -8F, 0F);
		leftEarRearTop.setTexSize(64, 32);
		leftEarRearTop.mirror = true;

		leftEarRearLayer1 = new ModelPart(this, 0, 14);
		leftEarRearLayer1.addBox(-1F, -2F, 1F, 2, 1, 1);
		leftEarRearLayer1.setPos(4F, -8F, 0F);
		leftEarRearLayer1.setTexSize(64, 32);
		leftEarRearLayer1.mirror = true;

		leftEarRearBottom = new ModelPart(this, 0, 12);
		leftEarRearBottom.addBox(-2F, -1F, 1F, 3, 1, 1);
		leftEarRearBottom.setPos(4F, -8F, 0F);
		leftEarRearBottom.setTexSize(64, 32);
		leftEarRearBottom.mirror = true;

		leftEarLayer1 = new ModelPart(this, 0, 2);
		leftEarLayer1.addBox(-3F, -1F, 0F, 5, 1, 1);
		leftEarLayer1.setPos(4F, -8F, 0F);
		leftEarLayer1.setTexSize(64, 32);
		leftEarLayer1.mirror = true;

		leftEarTop = new ModelPart(this, 0, 8);
		leftEarTop.addBox(0F, -4F, 0F, 1, 1, 1);
		leftEarTop.setPos(4F, -8F, 0F);
		leftEarTop.setTexSize(64, 32);
		leftEarTop.mirror = true;

		leftEarLayer3 = new ModelPart(this, 0, 6);
		leftEarLayer3.addBox(-1F, -3F, 0F, 3, 1, 1);
		leftEarLayer3.setPos(4F, -8F, 0F);
		leftEarLayer3.setTexSize(64, 32);
		leftEarLayer3.mirror = true;

		leftEarLayer2 = new ModelPart(this, 0, 4);
		leftEarLayer2.addBox(-2F, -2F, 0F, 4, 1, 1);
		leftEarLayer2.setPos(4F, -8F, 0F);
		leftEarLayer2.setTexSize(64, 32);
		leftEarLayer2.mirror = true;

		rightEarBottom = new ModelPart(this, 13, 0);
		rightEarBottom.addBox(-1F, 0F, 0F, 1, 1, 1);
		rightEarBottom.setPos(-4F, -8F, 0F);
		rightEarBottom.setTexSize(64, 32);
		rightEarBottom.mirror = true;

		rightEarLayer1 = new ModelPart(this, 13, 2);
		rightEarLayer1.addBox(-2F, -1F, 0F, 5, 1, 1);
		rightEarLayer1.setPos(-4F, -8F, 0F);
		rightEarLayer1.setTexSize(64, 32);
		rightEarLayer1.mirror = true;

		rightEarRearTop = new ModelPart(this, 13, 16);
		rightEarRearTop.addBox(-1F, -3F, 1F, 1, 1, 1);
		rightEarRearTop.setPos(-4F, -8F, 0F);
		rightEarRearTop.setTexSize(64, 32);
		rightEarRearTop.mirror = true;

		rightEarRearLayer1 = new ModelPart(this, 13, 14);
		rightEarRearLayer1.addBox(-1F, -2F, 1F, 2, 1, 1);
		rightEarRearLayer1.setPos(-4F, -8F, 0F);
		rightEarRearLayer1.setTexSize(64, 32);
		rightEarRearLayer1.mirror = true;

		rightEarRearBottom = new ModelPart(this, 13, 12);
		rightEarRearBottom.addBox(-1F, -1F, 1F, 3, 1, 1);
		rightEarRearBottom.setPos(-4F, -8F, 0F);
		rightEarRearBottom.setTexSize(64, 32);
		rightEarRearBottom.mirror = true;

		rightEarLayer2 = new ModelPart(this, 13, 4);
		rightEarLayer2.addBox(-2F, -2F, 0F, 4, 1, 1);
		rightEarLayer2.setPos(-4F, -8F, 0F);
		rightEarLayer2.setTexSize(64, 32);
		rightEarLayer2.mirror = true;

		rightEarTop = new ModelPart(this, 13, 8);
		rightEarTop.addBox(-1F, -4F, 0F, 1, 1, 1);
		rightEarTop.setPos(-4F, -8F, 0F);
		rightEarTop.setTexSize(64, 32);
		rightEarTop.mirror = true;

		rightEarLayer3 = new ModelPart(this, 13, 6);
		rightEarLayer3.addBox(-2F, -3F, 0F, 3, 1, 1);
		rightEarLayer3.setPos(-4F, -8F, 0F);
		rightEarLayer3.setTexSize(64, 32);
		rightEarLayer3.mirror = true;
	}

	@Override
	public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		leftEarBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarRearTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarRearLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarRearBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarLayer3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		leftEarLayer2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarRearTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarRearLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarRearBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarLayer2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarTop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEarLayer3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
