/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.render.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsRandomSource;
import uk.kihira.tails.common.client.model.PartConfiguration;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common.client.render.RenderContext;
import uk.kihira.tails.common.client.render.helper.RenderHelperManager;
import uk.kihira.tails.common.client.render.part.PartRenderer;

public interface BaseArrowLayer {

	@Nullable
	public default ClientPartsData getPartData(TailsEntity entity) {
		if (!entity.t$isPlayer()) return null;

		final UUID uuid = entity.t$uuid();

		return ClientPlayerPartManager.get().get(uuid);
	}

	@Nullable
	public TailsModelPart attachmentPart(String attachmentRoot);

	public PartConfiguration makeRootConfig(ClientPartsData data, TailsEntity entity);

	public default List<PartConfig> getConfigurations(ClientPartsData data, TailsEntity entity) {
		final List<PartConfig> parts = new ArrayList<>();
		parts.add(new PartConfig(makeRootConfig(data, entity), null, null));

		if (!data.isEmpty())
			for (ClientPartInfo info : data.getParts()) {
				if (info.isInvalid()) continue;
				final PartRenderer renderer = info.getRenderer();

				if (renderer != null && renderer.modelPart != null)
					for (PartConfiguration config : renderer.modelPart.collectParts(info)) {
						parts.add(new PartConfig(config, info, renderer));
						parts.add(parts.get(0));
					}
			}

		return parts;
	}

	public default void renderArrows(TailsEntity entity, TailsPoseStack poseStack, TailsBufferSource bufferSource, TailsRandomSource rand, int stuck, float partialTick, int packedLight, int packedOverlay) {
		final ClientPartsData data = getPartData(entity);
		final List<PartConfig> configurations = getConfigurations(data, entity);

		for (int i = 0; i < stuck; i++) {
			final int pick = rand.t$nextInt(configurations.size());
			final PartConfig config = configurations.get(pick);

			final TailsModelPart part = config.config.randomPart(rand);

			final TailsModelPart.CubePose cube;
			try {
				cube = part.t$getRandomCube(rand);
			} catch (IllegalArgumentException e) {
				// The t$isEmpty() checks in PartConfiguration exclude empty parts,
				// so if we get a "bound must be positive" error from the Random
				// then we know that the platform implementation is broken.
				if (e.getMessage().contains("bound must be positive"))
					throw new AssertionError(String.format("Detected faulty platform implementation: part (%s from config %s) was unexpectably empty!", part, config));

				TailsPlatform.get().logError("[BaseArrowLayer] Exception choosing cube:", e);
				continue;
			}

			float xOffset = rand.t$nextFloat();
			float yOffset = rand.t$nextFloat();
			float zOffset = rand.t$nextFloat();

			// Don't render arrows on invisible parts — avoids arrows occluding your vision with first-person model mods, for example.
			// We do this after the random calls so that we don't introduce differences in arrow positions.
			if (!part.t$isVisible()) continue;

			poseStack.t$push();

			if (config.renderer != null) {
				final RenderContext ctx = new RenderContext(poseStack, null, null, packedLight, packedOverlay, 0xFFFFFFFF, partialTick, entity, data, config.info);

				config.renderer.modelPart.setupAnim(entity, partialTick, config.info.getSubType(), config.info.getPart().getModel());
				if (config.info.getPart().getAnimation() != null) {
					config.info.getPart().getAnimation().setupAnim(entity.t$getAnimatorStorage(config.info), entity, part, config.info.getSubType(), partialTick);
				}

				final TailsModelPart attachment = attachmentPart(config.info.getPart().getAttachment().root().id());
				// Don't skip if null, since that'll skew the Random instance.
				if (attachment != null) attachment.t$translateAndRotate(poseStack);

				RenderHelperManager.applyRenderHelpers(ctx, config.renderer);
			}

			config.config.translate(config.info, poseStack, partialTick, entity, part);

			final float x = TailsMath.lerp(xOffset, cube.minX, cube.maxX) / 16F;
			final float y = TailsMath.lerp(yOffset, cube.minY, cube.maxY) / 16F;
			final float z = TailsMath.lerp(zOffset, cube.minZ, cube.maxZ) / 16F;

			poseStack.t$translate(x, y, z);

			xOffset = -(xOffset * 2F - 1F);
			yOffset = -(yOffset * 2F - 1F);
			zOffset = -(zOffset * 2F - 1F);

			renderStuckItem(poseStack, bufferSource, packedLight, entity, xOffset, yOffset, zOffset, partialTick);

			poseStack.t$pop();
		}
	}

	public void renderStuckItem(TailsPoseStack poseStack, TailsBufferSource bufferSource, int packedLight, TailsEntity entity, float x, float y, float z, float partialTick);

	static final class PartConfig {

		public final PartConfiguration config;
		public final ClientPartInfo info;
		public final PartRenderer renderer;

		public PartConfig(PartConfiguration config, ClientPartInfo info, PartRenderer renderer) {
			this.config = config;
			this.info = info;
			this.renderer = renderer;
			config.prime();
		}

		@Override
		public String toString() {
			if (renderer == null)
				return "PartConfig[player]";

			return "PartConfig[" + info.getPartId() + "." + info.getSubTypeId() + "." + info.getTextureId() + " (renderer " + renderer.getClass().getName() + ")]";
		}
	}
}