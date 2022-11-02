/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.part;

import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.helper.RenderHelperManager;
import uk.kihira.tails.client.texture.TextureHelper;

/**
 * A renderer for a part.<br>
 * You can register one using {@link RegisterPartRenderersEvent}.
 */
@OnlyIn(Dist.CLIENT)
public class PartRenderer {

	@Nullable
	public final PartModel modelPart;

	public PartRenderer(@Nullable PartModel modelPart) {
		this.modelPart = modelPart;
	}

	public void compileTextureIfNeeded(LivingEntity entity, ClientPartInfo info) {
		compileTextureIfNeeded(entity.getUUID(), info);
	}

	public void compileTextureIfNeeded(UUID uuid, ClientPartInfo info) {
		if (!info.isEmpty() && (info.needsTextureCompile || info.getTexture() == null)) {
			info.setTexture(TextureHelper.generateTexture(uuid, info));
			info.needsTextureCompile = false;
		}
	}

	/**
	 * A pre-render callback for translation, rotation, and making sure the texture exists.
	 * @param ctx The render context.
	 */
	public void preRender(RenderContext ctx) {
		compileTextureIfNeeded(ctx.entity(), ctx.info());

		if (modelPart != null) {
			modelPart.setupAnim(ctx.entity(), ctx.entity().animationPosition, ctx.entity().animationSpeed, ctx.partialTick(), ctx.info().getSubType(), ctx.entity().getXRot());
			modelPart.prepareMobModel(ctx.entity(), ctx.entity().animationPosition, ctx.entity().animationSpeed, ctx.partialTick());
		}

		RenderHelperManager.applyRenderHelpers(ctx, this);
	}

	/**
	 * Renders the given part on the given entity.
	 * @param poseStack The {@link PoseStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link ClientPartInfo}.
	 * @param bufferSource The {@link MultiBufferSource} to retrieve an {@link VertexConsumer} from.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTick The current partial ticks.
	 * @param packedLight The packed light.
	 * @param packedOverlay The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 * @param alpha The transparency value.
	 */
	public void render(PoseStack poseStack, LivingEntity entity, ClientPartInfo info, MultiBufferSource bufferSource, double x, double y, double z, float partialTick, int packedLight, int packedOverlay, float alpha) {
		if (!info.isEmpty()) {
			compileTextureIfNeeded(entity, info);

			final boolean visible = !entity.isInvisible();
			final boolean visibleToPlayer = !visible && !entity.isInvisibleTo(Minecraft.getInstance().player);
			final boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(entity);

			final RenderType type = getRenderType(entity, info.getTexture(), visible, visibleToPlayer, glowing);

			if (type == null) return;

			alpha = visibleToPlayer && alpha == 1F ? 0.15F : alpha;
			final VertexConsumer buf = bufferSource.getBuffer(type);

			render(poseStack, entity, info, bufferSource, buf, x, y, z, partialTick, packedLight, packedOverlay, alpha);
		}
	}

	/**
	 * Renders the given part on the given entity.
	 * @param poseStack The {@link PoseStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link ClientPartInfo}.
	 * @param bufferSource The buffer to retrieve buffers from.
	 * @param buffer The builder to draw to.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTick The current partial ticks.
	 * @param packedLight The packed light.
	 * @param packedOverlay The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 * @param alpha The transparency value.
	 */
	public void render(PoseStack poseStack, LivingEntity entity, ClientPartInfo info, MultiBufferSource bufferSource, VertexConsumer buffer, double x, double y, double z, float partialTick, int packedLight, int packedOverlay, float alpha) {
		if (!info.isEmpty()) {
			final float red, green, blue;

			if (info.getPartTexture().tintingStrategy() == Part.TintingStrategy.SINGLE_TINT) {
				final int tint = info.getTints()[0];
				red = (tint >> 16 & 255) / 255F;
				green = (tint >> 8 & 255) / 255F;
				blue = (tint & 255) / 255F;
			} else
				red = green = blue = 1F;

			final RenderContext ctx = new RenderContext(
					poseStack, buffer, packedLight, packedOverlay,
					red, green, blue, alpha, partialTick,
					entity, info);

			poseStack.pushPose();

			preRender(ctx);

			doRender(ctx);

			poseStack.popPose();
		}
	}

	@Nullable
	protected RenderType getRenderType(LivingEntity entity, ResourceLocation tex, boolean visible, boolean visibleToPlayer, boolean glowing) {
		if (visibleToPlayer)
			return RenderType.itemEntityTranslucentCull(tex);
		if (visible)
			return RenderType.entityCutoutNoCull(tex);
		return glowing ? RenderType.outline(tex) : null;
	}

	/**
	 * Renders the given part on the given entity.
	 * @param ctx The render context.
	 */
	protected void doRender(RenderContext ctx) {
		if (modelPart != null)
			modelPart.render(ctx);
	}
}