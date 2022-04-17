/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

// TODO: Move to layer/
@OnlyIn(Dist.CLIENT)
public class PartLayer extends RenderLayer<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> {

	private final PartType partType;

	public PartLayer(LivingEntityRenderer<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> renderer, ModelPart modelRenderer, PartType partType) {
		super(renderer);
		this.partType = partType;
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		final UUID uuid = Player.createPlayerUUID(entity.getGameProfile());
		if (Tails.PROXY.hasPartsData(uuid)) {
			final PartsData partsData = Tails.PROXY.getPartsData(uuid);
			if (partsData.hasPartInfo(partType)) {
				final PartInfo partInfo = partsData.getPartInfo(partType);

				matrixStackIn.pushPose();

				if (partType == PartType.EARS || partType == PartType.MUZZLE)
					getParentModel().head.translateAndRotate(matrixStackIn);

				else if (partType == PartType.TAIL)
					getParentModel().body.translateAndRotate(matrixStackIn);

				try {
					final Part part = partInfo.getPart();
					final PartRenderer renderer = PartRenderRegistry.getRenderer(part);
					if (renderer != null)
						renderer.render(matrixStackIn, entity, partInfo, bufferIn, 0, 0, 0, partialTicks, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0F), 1F, 1F, 1F, 1F);
					else Tails.LOGGER.error("No PartRenderer for part {} found! Did someone forget to register one?", partInfo);
				} catch (Exception e) {
					Tails.LOGGER.error("Exception rendering part {}: ", partInfo, e);
				}

				matrixStackIn.popPose();
			}
		}
	}
}