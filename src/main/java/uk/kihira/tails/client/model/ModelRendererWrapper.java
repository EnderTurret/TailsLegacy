package uk.kihira.tails.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
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

	public ModelRendererWrapper(Model model, PartsData.PartType partType) {
		super(model);
		this.partType = partType;
		addBox(0, 0, 0, 0, 0, 0); //Adds in a blank box as it's required in certain cases such as rendering arrows in entities
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (RenderingHandler.currentEvent != null && RenderingHandler.currentPartsData != null && RenderingHandler.currentPlayerTexture != null) {
			final PartInfo info = RenderingHandler.currentPartsData.getPartInfo(partType);
			if (info != null && info.hasPart) {
				PartRegistry.getRenderPart(info.partType, info.typeid).render(matrixStackIn, RenderingHandler.currentEvent.getPlayer(),
						info, Minecraft.getInstance().getRenderTypeBuffers().getBufferSource(), 0, 0, 0, RenderingHandler.currentEvent.getPartialRenderTick(), packedLightIn, packedOverlayIn);

				Minecraft.getInstance().getTextureManager().bindTexture(RenderingHandler.currentPlayerTexture);
			}
		}
	}
}