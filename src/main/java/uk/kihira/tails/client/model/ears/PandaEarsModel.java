/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model.ears;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import uk.kihira.tails.client.model.PartModel;

public class PandaEarsModel extends PartModel {

	final ModelRenderer leftEar;
	final ModelRenderer rightEar;

	public PandaEarsModel() {
		textureWidth = 32;
		textureHeight = 32;

		leftEar = new ModelRenderer(this, 0, 0);
		leftEar.addBox(-2F, -2F, 0F, 3, 3, 1);
		leftEar.setRotationPoint(-4F, -8F, 0F);
		leftEar.setTextureSize(32, 32);
		leftEar.mirror = true;

		rightEar = new ModelRenderer(this, 0, 4);
		rightEar.addBox(-1F, -2F, 0F, 3, 3, 1);
		rightEar.setRotationPoint(4F, -8F, 0F);
		rightEar.setTextureSize(32, 32);
		rightEar.mirror = true;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		leftEar.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		rightEar.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
