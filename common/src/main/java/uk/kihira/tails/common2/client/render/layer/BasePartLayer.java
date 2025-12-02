package uk.kihira.tails.common2.client.render.layer;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.client.duck.TailsBufferSource;
import uk.kihira.tails.common2.client.duck.TailsEntity;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.duck.TailsPoseStack;
import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.common2.client.part.ClientPartsData;
import uk.kihira.tails.common2.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common2.client.part.Part;
import uk.kihira.tails.common2.client.render.part.PartRenderer;

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