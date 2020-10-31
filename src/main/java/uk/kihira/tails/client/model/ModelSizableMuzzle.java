package uk.kihira.tails.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class ModelSizableMuzzle extends ModelPartBase {
	private final ModelRenderer stubMuzzle;
	private final ModelRenderer tinyMuzzle;

	private final ModelRenderer muzzle;

	public ModelSizableMuzzle(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize, int xTex, int yTex) {
		textureWidth = textureHeight = 32;

		muzzle = new ModelRenderer(this, xTex, yTex);
		muzzle.addBox(xOffset, yOffset, zOffset, xSize, ySize, zSize);

		stubMuzzle = new ModelRenderer(this);
		stubMuzzle.addBox(-2f, -4f, -7f, 4, 4, 3);

		tinyMuzzle = new ModelRenderer(this);
		tinyMuzzle.addBox(-2f, -2f, -5f, 4, 2, 1);
	}

	public ModelSizableMuzzle(float xOffset, float yOffset, float zOffset, int xSize, int ySize, int zSize) {
		this(xOffset, yOffset, zOffset, xSize, ySize, zSize, 0, 0);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		matrixStackIn.push();
		switch (subtype) {
		case 0: // Very Short
		matrixStackIn.translate(0f, 0f, 4f / 16f);
		muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		break;
		case 1: // Short
			matrixStackIn.translate(0f, 0f, 3f / 16f);
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 2: // Standard
			matrixStackIn.translate(0f, 0f, 2f / 16f);
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 3: // Long
			matrixStackIn.translate(0f, 0f, 1f / 16f);
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		case 4: // Very Long
			muzzle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			break;
		}
		matrixStackIn.pop();
	}
}
