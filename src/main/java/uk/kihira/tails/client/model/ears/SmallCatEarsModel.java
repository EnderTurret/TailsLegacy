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

public class SmallCatEarsModel extends PartModel {

	public ModelRenderer leftEarBottom;
	public ModelRenderer leftEarRearLayer1;
	public ModelRenderer leftEarRearBottom;
	public ModelRenderer leftEarLayer1;
	public ModelRenderer leftEarLayer3;
	public ModelRenderer leftEarLayer2;
	public ModelRenderer rightEarBottom;
	public ModelRenderer rightEarLayer1;
	public ModelRenderer rightEarRearLayer1;
	public ModelRenderer rightEarRearBottom;
	public ModelRenderer rightEarLayer2;
	public ModelRenderer rightEarLayer3;

	public SmallCatEarsModel() {
		textureWidth = 64;
		textureHeight = 32;
		rightEarRearLayer1 = new ModelRenderer(this, 13, 14);
		rightEarRearLayer1.setRotationPoint(-3.0F, -8.0F, 0.0F);
		rightEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
		leftEarRearLayer1 = new ModelRenderer(this, 0, 14);
		leftEarRearLayer1.setRotationPoint(4.0F, -8.0F, 0.0F);
		leftEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
		rightEarRearBottom = new ModelRenderer(this, 13, 12);
		rightEarRearBottom.setRotationPoint(-3.0F, -8.0F, 0.0F);
		rightEarRearBottom.addBox(-1.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
		leftEarLayer1 = new ModelRenderer(this, 0, 2);
		leftEarLayer1.setRotationPoint(4.0F, -8.0F, 0.0F);
		leftEarLayer1.addBox(-3.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
		leftEarLayer2 = new ModelRenderer(this, 0, 4);
		leftEarLayer2.setRotationPoint(4.0F, -8.0F, 0.0F);
		leftEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
		rightEarLayer1 = new ModelRenderer(this, 13, 2);
		rightEarLayer1.setRotationPoint(-3.0F, -8.0F, 0.0F);
		rightEarLayer1.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
		rightEarBottom = new ModelRenderer(this, 13, 0);
		rightEarBottom.setRotationPoint(-4.0F, -8.0F, 0.0F);
		rightEarBottom.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		leftEarRearBottom = new ModelRenderer(this, 0, 12);
		leftEarRearBottom.setRotationPoint(4.0F, -8.0F, 0.0F);
		leftEarRearBottom.addBox(-2.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
		rightEarLayer3 = new ModelRenderer(this, 13, 6);
		rightEarLayer3.setRotationPoint(-2.0F, -8.0F, 0.0F);
		rightEarLayer3.addBox(-2.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
		leftEarBottom = new ModelRenderer(this, 0, 0);
		leftEarBottom.setRotationPoint(4.0F, -8.0F, 0.0F);
		leftEarBottom.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		rightEarLayer2 = new ModelRenderer(this, 13, 4);
		rightEarLayer2.setRotationPoint(-3.0F, -8.0F, 0.0F);
		rightEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
		leftEarLayer3 = new ModelRenderer(this, 0, 6);
		leftEarLayer3.setRotationPoint(4.0F, -8.0F, 0.0F);
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
