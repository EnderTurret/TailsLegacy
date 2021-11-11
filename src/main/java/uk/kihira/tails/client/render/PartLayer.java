/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartRegistry;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

@OnlyIn(Dist.CLIENT)
public class PartLayer extends LayerRenderer<AbstractClientPlayerEntity,PlayerModel<AbstractClientPlayerEntity>> {

	private final LivingRenderer<AbstractClientPlayerEntity,PlayerModel<AbstractClientPlayerEntity>> renderer;
	private final PartType partType;
	//private final ModelRenderer modelRenderer;
	//private final boolean mpmCompat;

	public PartLayer(LivingRenderer<AbstractClientPlayerEntity,PlayerModel<AbstractClientPlayerEntity>> renderer, ModelRenderer modelRenderer, PartType partType) {
		super(renderer);
		this.renderer = renderer;
		this.partType = partType;
		//this.modelRenderer = modelRenderer;
		//mpmCompat = ModList.get().isLoaded("moreplayermodels");
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			final UUID uuid = PlayerEntity.getUUID(entity.getGameProfile());
			if (Tails.PROXY.hasPartsData(uuid)) {
				final PartsData partsData = Tails.PROXY.getPartsData(uuid);
				if (partsData.hasPartInfo(partType)) {
					final PartInfo tailInfo = partsData.getPartInfo(partType);

					matrixStackIn.push();

					if (partType == PartType.EARS || partType == PartType.MUZZLE)
						getEntityModel().bipedHead.translateRotate(matrixStackIn);

					else if (partType == PartType.TAIL)
						getEntityModel().bipedBody.translateRotate(matrixStackIn);

					try {
						final Part part = tailInfo.getPart();
						final PartRenderer renderer = PartRenderRegistry.getRenderer(part);
						if (renderer != null)
							renderer.render(matrixStackIn, entity, tailInfo, bufferIn, 0, 0, 0, partialTicks, packedLightIn, LivingRenderer.getPackedOverlay(entity, 0F), 1F, 1F, 1F, 1F);
						else Tails.LOGGER.error("No PartRenderer for part {} found! Did someone forget to register one?", tailInfo);
					} catch (Exception e) {
						Tails.LOGGER.error("Exception rendering part {}: ", tailInfo, e);
					}

					matrixStackIn.pop();
				}
			}
	}
}