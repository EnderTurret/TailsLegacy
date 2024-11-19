/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.ClientPartsData;
import uk.kihira.tails.client.part.ClientPlayerPartManager;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.helper.RenderHelperManager;
import uk.kihira.tails.client.render.part.PartRenderer;

/**
 * A specialized {@link ArrowLayer} for rendering arrows on Tails parts/accessories in addition to normal body parts.
 *
 * @author EnderTurret
 *
 * @param <T>
 * @param <M>
 */
@Internal
public final class TailsArrowLayer<T extends LivingEntity, M extends PlayerModel<T>> extends ArrowLayer<T, M> {

	@Internal
	public TailsArrowLayer(EntityRendererProvider.Context context, LivingEntityRenderer<T, M> renderer) {
		super(context, renderer);
	}

	@Override
	protected int numStuck(T entity) {
		return super.numStuck(entity);
	}

	protected ClientPartsData getPartData(LivingEntity entity) {
		if (!(entity instanceof Player player)) return null;

		final UUID uuid = UUIDUtil.getOrCreatePlayerUUID(player.getGameProfile());

		return ClientPlayerPartManager.get().get(uuid);
	}

	protected List<PartConfig> getConfigurations(ClientPartsData data, LivingEntity entity) {
		final List<PartConfig> parts = new ArrayList<>();
		parts.add(new PartConfig(new PartConfiguration.Player(getParentModel()), null, null));

		if (!data.isEmpty())
			for (ClientPartInfo info : data.getParts()) {
				if (info.isInvalid()) continue;
				final PartRenderer renderer = info.getRenderer();

				if (renderer != null && renderer.modelPart != null)
					for (PartConfiguration config : renderer.modelPart.getParts(info)) {
						parts.add(new PartConfig(config, info, renderer));
						parts.add(parts.get(0));
					}
			}

		return parts;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		final int stuck = numStuck(entity);

		if (stuck > 0) {
			final RandomSource rand = RandomSource.create(entity.getId());
			final ClientPartsData data = getPartData(entity);
			final List<PartConfig> configurations = getConfigurations(data, entity);

			for (int i = 0; i < stuck; i++) {
				final int pick = rand.nextInt(configurations.size());
				final PartConfig config = configurations.get(pick);

				final ModelPart part = config.config.randomPart(rand);
				final ModelPart.Cube cube = part.getRandomCube(rand);

				poseStack.pushPose();

				if (config.renderer != null) {
					final RenderContext ctx = new RenderContext(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F, partialTick, entity, data, config.info);

					RenderHelperManager.applyRenderHelpers(ctx, config.renderer);

					config.renderer.modelPart.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, headPitch, config.info.getSubType(), config.info.getPart().getModel());
					config.renderer.modelPart.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
				}

				config.config.translate(config.info, poseStack, partialTick, entity, part);

				float xOffset = rand.nextFloat();
				float yOffset = rand.nextFloat();
				float zOffset = rand.nextFloat();
				final float x = Mth.lerp(xOffset, cube.minX, cube.maxX) / 16F;
				final float y = Mth.lerp(yOffset, cube.minY, cube.maxY) / 16F;
				final float z = Mth.lerp(zOffset, cube.minZ, cube.maxZ) / 16F;

				poseStack.translate(x, y, z);

				xOffset = -(xOffset * 2F - 1F);
				yOffset = -(yOffset * 2F - 1F);
				zOffset = -(zOffset * 2F - 1F);

				renderStuckItem(poseStack, buffer, packedLight, entity, xOffset, yOffset, zOffset, partialTick);

				poseStack.popPose();
			}
		}
	}

	private static record PartConfig(PartConfiguration config, ClientPartInfo info, PartRenderer renderer) {
		public PartConfig {
			config.prime();
		}
	}
}