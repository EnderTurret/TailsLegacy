/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.layer;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.UUIDUtil;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

@OnlyIn(Dist.CLIENT)
public class PartLayer extends RenderLayer<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> {

	private final PartType partType;

	public PartLayer(LivingEntityRenderer<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> renderer, ModelPart modelRenderer, PartType partType) {
		super(renderer);
		this.partType = partType;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		final UUID uuid = UUIDUtil.getOrCreatePlayerUUID(entity.getGameProfile());
		if (Tails.PROXY.hasPartsData(uuid)) {
			final PartsData partsData = Tails.PROXY.getPartsData(uuid);
			if (partsData.hasPartInfo(partType)) {
				final ClientPartInfo partInfo = (ClientPartInfo) partsData.getPartInfo(partType);

				poseStack.pushPose();

				if (partType == PartType.EARS || partType == PartType.MUZZLE)
					getParentModel().head.translateAndRotate(poseStack);

				else if (partType == PartType.TAIL)
					getParentModel().body.translateAndRotate(poseStack);

				try {
					final Part part = partInfo.getPart();
					final PartRenderer renderer = PartRenderRegistry.getRenderer(part);
					if (renderer != null)
						renderer.render(poseStack, entity, partInfo, buffer, 0, 0, 0, partialTick, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0F), 1F, 1F, 1F, 1F);
					// TODO: Make this less spammy.
					else Tails.LOGGER.error("No PartRenderer for part {} found! Did someone forget to register one?", partInfo);
				} catch (Exception e) {
					Tails.LOGGER.error("Exception rendering part {}: ", partInfo, e);
				}

				poseStack.popPose();
			}
		}
	}
}