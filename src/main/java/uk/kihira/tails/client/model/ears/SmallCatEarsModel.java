/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import uk.kihira.tails.client.model.PartModel;

/**
 * The model for small cat ears.
 */
public class SmallCatEarsModel extends PartModel {

	private final ModelRenderer leftEarBottom;
	private final ModelRenderer leftEarRearLayer1;
	private final ModelRenderer leftEarRearBottom;
	private final ModelRenderer leftEarLayer1;
	private final ModelRenderer leftEarLayer3;
	private final ModelRenderer leftEarLayer2;
	private final ModelRenderer rightEarBottom;
	private final ModelRenderer rightEarLayer1;
	private final ModelRenderer rightEarRearLayer1;
	private final ModelRenderer rightEarRearBottom;
	private final ModelRenderer rightEarLayer2;
	private final ModelRenderer rightEarLayer3;

	public SmallCatEarsModel() {
		texWidth = 64;
		texHeight = 32;
		rightEarRearLayer1 = new ModelRenderer(this, 13, 14);
		rightEarRearLayer1.setPos(-3.0F, -8.0F, 0.0F);
		rightEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
		leftEarRearLayer1 = new ModelRenderer(this, 0, 14);
		leftEarRearLayer1.setPos(4.0F, -8.0F, 0.0F);
		leftEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
		rightEarRearBottom = new ModelRenderer(this, 13, 12);
		rightEarRearBottom.setPos(-3.0F, -8.0F, 0.0F);
		rightEarRearBottom.addBox(-1.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
		leftEarLayer1 = new ModelRenderer(this, 0, 2);
		leftEarLayer1.setPos(4.0F, -8.0F, 0.0F);
		leftEarLayer1.addBox(-3.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
		leftEarLayer2 = new ModelRenderer(this, 0, 4);
		leftEarLayer2.setPos(4.0F, -8.0F, 0.0F);
		leftEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
		rightEarLayer1 = new ModelRenderer(this, 13, 2);
		rightEarLayer1.setPos(-3.0F, -8.0F, 0.0F);
		rightEarLayer1.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
		rightEarBottom = new ModelRenderer(this, 13, 0);
		rightEarBottom.setPos(-4.0F, -8.0F, 0.0F);
		rightEarBottom.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		leftEarRearBottom = new ModelRenderer(this, 0, 12);
		leftEarRearBottom.setPos(4.0F, -8.0F, 0.0F);
		leftEarRearBottom.addBox(-2.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
		rightEarLayer3 = new ModelRenderer(this, 13, 6);
		rightEarLayer3.setPos(-2.0F, -8.0F, 0.0F);
		rightEarLayer3.addBox(-2.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
		leftEarBottom = new ModelRenderer(this, 0, 0);
		leftEarBottom.setPos(4.0F, -8.0F, 0.0F);
		leftEarBottom.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		rightEarLayer2 = new ModelRenderer(this, 13, 4);
		rightEarLayer2.setPos(-3.0F, -8.0F, 0.0F);
		rightEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
		leftEarLayer3 = new ModelRenderer(this, 0, 6);
		leftEarLayer3.setPos(4.0F, -8.0F, 0.0F);
		leftEarLayer3.addBox(-1.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
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
