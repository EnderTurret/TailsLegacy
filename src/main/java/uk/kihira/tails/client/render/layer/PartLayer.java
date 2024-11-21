/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.layer;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.UUIDUtil;

import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.ClientPartsData;
import uk.kihira.tails.client.part.ClientPlayerPartManager;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common.Tails;

/**
 * A {@link RenderLayer} for Tails parts/accessories.
 */
public final class PartLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	/**
	 * @param renderer The renderer.
	 */
	public PartLayer(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
		super(renderer);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		final UUID uuid = entity.getUUID();
		final ClientPartsData partsData = ClientPlayerPartManager.get().get(uuid);
		for (ClientPartInfo partInfo : partsData.getParts()) {
			if (partInfo.isInvalid()) return; // Skip unknown parts.

			poseStack.pushPose();

			if ("head".equals(partInfo.getPart().getAttachment().root().id()))
				getParentModel().head.translateAndRotate(poseStack);

			else if ("body".equals(partInfo.getPart().getAttachment().root().id()))
				getParentModel().body.translateAndRotate(poseStack);

			try {
				final Part part = partInfo.getPart();
				final PartRenderer renderer = partInfo.getRenderer();
				if (renderer != null)
					renderer.render(poseStack, entity, partsData, partInfo, buffer, 0, 0, 0, partialTick, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0F), 0xFF);
				// TODO: Make this less spammy.
				else Tails.LOGGER.error("No PartRenderer for part {} found! Did someone forget to register one?", partInfo);
			} catch (Exception e) {
				Tails.LOGGER.error("Exception rendering part {}: ", partInfo, e);
			}

			poseStack.popPose();
		}
	}
}