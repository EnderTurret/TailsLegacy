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

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.common2.client.part.ClientPartsData;
import uk.kihira.tails.common2.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common2.client.part.Part;

/**
 * A {@link RenderLayer} for Tails parts/accessories.
 * @param <T> The entity type.
 * @param <M> The model type.
 */
public class PartLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

	/**
	 * @param renderer The renderer.
	 */
	public PartLayer(LivingEntityRenderer<T, M> renderer) {
		super(renderer);
	}

	protected ClientPartsData getPartsData(T entity) {
		final UUID uuid = entity.getUUID();
		final ClientPartsData partsData = ClientPlayerPartManager.get().get(uuid);
		return partsData;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		final ClientPartsData partsData = getPartsData(entity);
		for (ClientPartInfo partInfo : partsData.getParts()) {
			if (partInfo.isInvalid()) continue; // Skip unknown parts.

			final Part part = partInfo.getPart();
			final PartRenderer renderer = partInfo.getRenderer();
			final String attachmentRoot = part.getAttachment().root().id();
			final M model = getParentModel();

			// Don't render a part if its root attachment isn't visible.
			// Prevents head parts rendering in first person in Sleep Tight beds, for example.
			final boolean visible = switch (attachmentRoot) {
				case "head" -> model.head.visible;
				case "body" -> model.body.visible;
				default -> true;
			};
			if (!visible) continue;

			poseStack.pushPose();

			switch (attachmentRoot) {
				case "head" -> model.head.translateAndRotate(poseStack);
				case "body" -> model.body.translateAndRotate(poseStack);
			}

			try {
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