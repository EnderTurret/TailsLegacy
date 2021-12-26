/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.common.part.PartInfo;

public class SeaPickleRenderer extends PartRenderer {

	public SeaPickleRenderer() {
		super(new Model());
	}

	@Override
	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IVertexBuilder bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		final int tint = info.getTints()[0];
		final float r = (tint >> 16 & 255) / 255F;
		final float g = (tint >> 8 & 255) / 255F;
		final float b = (tint & 255) / 255F;
		super.doRender(matrixStack, entity, info, bufferIn, partialTicks, packedLightIn, packedOverlayIn, r, g, b, alpha);
	}

	@Override
	public void compileTextureIfNeeded(LivingEntity entity, PartInfo info) {
		info.setTexture(new ResourceLocation("tails", "texture/ears/sea_pickle.png"));
	}

	public static class Model extends PartModel {

		private ModelRenderer model;

		public Model() {
			texWidth = 32;
			texHeight = 32;

			model = new ModelRenderer(this);
			model.setPos(0F, 18.2875F, 0F);
			model.texOffs(0, 1).addBox(-2F, -0.2875F, -2F, 4F, 6F, 4F, 0F, false);
			model.texOffs(0, 11).addBox(-2F, -0.2375F, -2F, 4F, 0F, 4F, 0F, false);

			final ModelRenderer cube = new ModelRenderer(this);
			cube.setPos(0F, -2.2875F, 0F);
			model.addChild(cube);
			cube.yRot = -0.7854F;
			cube.texOffs(1, 1).addBox(0F, -0.7F, -0.5F, 0F, 3F, 1F, 0F, false);
			cube.texOffs(0, 2).addBox(-0.5F, -0.7F, 0F, 1F, 3F, 0F, 0F, false);
		}

		@Override
		public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
			model = new ModelRenderer(this);
			model.setPos(0F, 18.2875F, 0F);
			model.texOffs(0, 1).addBox(-2F, -0.2875F, -2F, 4F, 6F, 4F, 0F, false);
			model.texOffs(0, 11).addBox(-2F, -0.2375F, -2F, 4F, 0F, 4F, 0F, false);

			matrixStackIn.pushPose();

			matrixStackIn.translate(0, -2, 0);

			model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			matrixStackIn.popPose();
		}
	}
}