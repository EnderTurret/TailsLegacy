package uk.kihira.tails.client.render.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

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

import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.client.render.RenderHelperManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

public class TailsArrowLayer<T extends LivingEntity, M extends PlayerModel<T>> extends ArrowLayer<T, M> {

	public TailsArrowLayer(EntityRendererProvider.Context context, LivingEntityRenderer<T, M> renderer) {
		super(context, renderer);
	}

	@Override
	protected int numStuck(T entity) {
		return super.numStuck(entity);
	}

	@Nullable
	protected PartsData getPartData(LivingEntity entity) {
		if (!(entity instanceof Player player)) return null;

		final UUID uuid = UUIDUtil.getOrCreatePlayerUUID(player.getGameProfile());

		return Tails.PROXY.hasPartsData(uuid) ? Tails.PROXY.getPartsData(uuid) : null;
	}

	protected List<PartConfig> getConfigurations(@Nullable PartsData data, LivingEntity entity) {
		final List<PartConfig> parts = new ArrayList<>();
		parts.add(new PartConfig(new PartConfiguration.Player(getParentModel()), null, null));

		if (data != null)
			for (PartType type : PartType.values())
				if (data.hasPartInfo(type)) {
					final ClientPartInfo info = (ClientPartInfo) data.getPartInfo(type);
					final PartRenderer renderer = PartRenderRegistry.getRenderer(info.getPart());

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
			final PartsData data = getPartData(entity);
			final List<PartConfig> configurations = getConfigurations(data, entity);

			for (int i = 0; i < stuck; i++) {
				final int pick = rand.nextInt(configurations.size());
				final PartConfig config = configurations.get(pick);

				final ModelPart part = config.config.randomPart(rand);
				final ModelPart.Cube cube = part.getRandomCube(rand);

				poseStack.pushPose();

				if (config.renderer != null) {
					RenderHelperManager.applyRenderHelpers(poseStack, entity, config.renderer, config.info, buffer, null, 0, 0, 0, partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

					config.renderer.modelPart.setupAnim(entity, limbSwing, limbSwingAmount, partialTick, config.info.getSubType(), headPitch);
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