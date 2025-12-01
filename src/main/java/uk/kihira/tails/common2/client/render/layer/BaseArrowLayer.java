package uk.kihira.tails.common2.client.render.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Mth;

import uk.kihira.tails.common2.client.duck.TailsBufferSource;
import uk.kihira.tails.common2.client.duck.TailsEntity;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.duck.TailsPoseStack;
import uk.kihira.tails.common2.client.duck.TailsRandomSource;
import uk.kihira.tails.common2.client.model.PartConfiguration;
import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.common2.client.part.ClientPartsData;
import uk.kihira.tails.common2.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common2.client.render.RenderContext;
import uk.kihira.tails.common2.client.render.helper.RenderHelperManager;
import uk.kihira.tails.common2.client.render.part.PartRenderer;

public interface BaseArrowLayer {

	@Nullable
	public default ClientPartsData getPartData(TailsEntity entity) {
		if (!entity.t$isPlayer()) return null;

		final UUID uuid = entity.t$uuid();

		return ClientPlayerPartManager.get().get(uuid);
	}

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
			final TailsModelPart.CubePose cube = part.t$getRandomCube(rand);

			poseStack.t$push();

			if (config.renderer != null) {
				final RenderContext ctx = new RenderContext(poseStack, null, null, packedLight, packedOverlay, 0xFFFFFFFF, partialTick, entity, data, config.info);

				RenderHelperManager.applyRenderHelpers(ctx, config.renderer);

				config.renderer.modelPart.setupAnim(entity, partialTick, config.info.getSubType(), config.info.getPart().getModel());
			}

			config.config.translate(config.info, poseStack, partialTick, entity, part);

			float xOffset = rand.t$nextFloat();
			float yOffset = rand.t$nextFloat();
			float zOffset = rand.t$nextFloat();
			final float x = Mth.lerp(xOffset, cube.minX, cube.maxX) / 16F;
			final float y = Mth.lerp(yOffset, cube.minY, cube.maxY) / 16F;
			final float z = Mth.lerp(zOffset, cube.minZ, cube.maxZ) / 16F;

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
	}
}