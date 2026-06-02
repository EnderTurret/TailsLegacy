/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.render.layer;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.part.ClientPlayerPartManager;
import net.enderturret.tailslegacy.common.client.part.Part;
import net.enderturret.tailslegacy.common.client.render.part.PartRenderer;

public interface BasePartLayer {

	public default ClientPartsData getPartsData(TailsEntity entity) {
		final UUID uuid = entity.t$uuid();
		return ClientPlayerPartManager.get().get(uuid);
	}

	@Nullable
	public TailsModelPart attachmentPart(String attachmentRoot);

	public default void renderParts(TailsEntity entity, TailsPoseStack poseStack, TailsBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
		final ClientPartsData partsData = getPartsData(entity);
		for (ClientPartInfo partInfo : partsData.getParts()) {
			if (partInfo.isInvalid()) continue; // Skip unknown parts.

			final Part part = partInfo.getPart();
			final PartRenderer renderer = partInfo.getRenderer();
			final TailsModelPart parentPart = attachmentPart(part.getAttachment().root().id());

			// Don't render a part if its root attachment isn't visible.
			// Prevents head parts rendering in first person in Sleep Tight beds, for example.
			if (parentPart != null && !parentPart.t$isVisible()) continue;

			poseStack.t$push();

			if (parentPart != null) parentPart.t$translateAndRotate(poseStack);

			try {
				renderer.render(poseStack, entity, partsData, partInfo, bufferSource, 0, 0, 0, partialTick, packedLight, packedOverlay, 0xFF);
			} catch (Exception e) {
				TailsPlatform.get().logError("Exception rendering part {}: ", partInfo, e);
			}

			poseStack.t$pop();
		}
	}
}