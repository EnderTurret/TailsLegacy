/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.render.RenderingHandler;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;

@OnlyIn(Dist.CLIENT)
public class ModelRendererWrapper extends ModelRenderer {

	private final PartsData.PartType partType;
	private final PlayerModel model;

	public ModelRendererWrapper(PlayerModel model, PartsData.PartType partType) {
		super(model);
		this.partType = partType;
		this.model = model;
		addBox(0, 0, 0, 0, 0, 0); // Adds in a blank box as it's required in certain cases such as rendering arrows in entities.
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (RenderingHandler.currentEvent != null && RenderingHandler.currentPartsData != null && RenderingHandler.currentPlayerTexture != null) {
			final PartInfo info = RenderingHandler.currentPartsData.getPartInfo(partType);
			if (info != null && info.hasPart) {
				matrixStackIn.push();

				if (partType == PartsData.PartType.EARS || partType == PartsData.PartType.MUZZLE)
					model.bipedHead.translateRotate(matrixStackIn);

				else if (partType == PartsData.PartType.TAIL)
					model.bipedBody.translateRotate(matrixStackIn);

				PartRegistry.getPartRenderer(info.partType, info.typeid).render(matrixStackIn, RenderingHandler.currentEvent.getPlayer(),
						info, Minecraft.getInstance().getRenderTypeBuffers().getBufferSource(), 0, 0, 0, RenderingHandler.currentEvent.getPartialRenderTick(), packedLightIn, packedOverlayIn);

				matrixStackIn.pop();

				Minecraft.getInstance().getTextureManager().bindTexture(RenderingHandler.currentPlayerTexture);
			}
		}
	}
}