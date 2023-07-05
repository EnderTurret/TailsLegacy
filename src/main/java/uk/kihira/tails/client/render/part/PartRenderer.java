/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render.part;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.ClientPartsData;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.helper.RenderHelperManager;

/**
 * A renderer for a part.
 * @see RegisterPartRenderersEvent
 */
public class PartRenderer {

	/**
	 * The part model. May be {@code null} if the part renderer has no associated model, such as {@linkplain WingRenderer the wings}.
	 */
	@Nullable
	public final PartModel modelPart;

	public PartRenderer(@Nullable PartModel modelPart) {
		this.modelPart = modelPart;
	}

	public void compileTextureIfNeeded(LivingEntity entity, ClientPartInfo info) {
		info.checkTexture(entity.getUUID(), false);
	}

	/**
	 * A pre-render callback for translation, rotation, etc.
	 * @param ctx The render context.
	 */
	public void preRender(RenderContext ctx) {
		if (modelPart != null) {
			modelPart.setupAnim(ctx.entity(), ctx.entity().walkAnimation.position(ctx.partialTick()), ctx.entity().walkAnimation.speed(ctx.partialTick()), ctx.partialTick(), ctx.info().getSubType(), ctx.entity().getXRot());
			modelPart.prepareMobModel(ctx.entity(), ctx.entity().walkAnimation.position(ctx.partialTick()), ctx.entity().walkAnimation.speed(ctx.partialTick()), ctx.partialTick());
		}

		RenderHelperManager.applyRenderHelpers(ctx, this);
	}

	/**
	 * Renders the given part on the given entity.
	 * @param poseStack The {@link PoseStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param parts The entity's part data.
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
	public void render(PoseStack poseStack, LivingEntity entity, @Nullable ClientPartsData parts, ClientPartInfo info, MultiBufferSource bufferSource, double x, double y, double z, float partialTick, int packedLight, int packedOverlay, float alpha) {
		if (!info.isEmpty()) {
			final boolean visible = !entity.isInvisible();
			final boolean visibleToPlayer = !visible && !entity.isInvisibleTo(Minecraft.getInstance().player);
			final boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(entity);

			info.checkTexture(entity.getUUID(), false);
			final RenderType type = getRenderType(entity, info.getTexture(), visible, visibleToPlayer, glowing);

			if (type == null) return;

			alpha = visibleToPlayer && alpha == 1F ? 0.15F : alpha;
			final VertexConsumer buf = bufferSource.getBuffer(type);

			render(poseStack, entity, parts, info, bufferSource, buf, x, y, z, partialTick, packedLight, packedOverlay, alpha);
		}
	}

	/**
	 * Renders the given part on the given entity.
	 * @param poseStack The {@link PoseStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param parts The entity's part data.
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
	public void render(PoseStack poseStack, LivingEntity entity, @Nullable ClientPartsData parts, ClientPartInfo info, MultiBufferSource bufferSource, VertexConsumer buffer, double x, double y, double z, float partialTick, int packedLight, int packedOverlay, float alpha) {
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
					entity, parts, info);

			poseStack.pushPose();

			preRender(ctx);

			doRender(ctx);

			poseStack.popPose();
		}
	}

	/**
	 * Returns the {@link RenderType} to use for rendering this part.
	 * @param entity The entity being rendered.
	 * @param tex The texture of the part being rendered.
	 * @param visible {@code true} if the entity is visible.
	 * @param visibleToPlayer {@code true} if the entity is visible to the viewer but not others.
	 * @param glowing {@code true} if the entity is glowing.
	 * @return The render type.
	 */
	@Nullable
	protected RenderType getRenderType(LivingEntity entity, ResourceLocation tex, boolean visible, boolean visibleToPlayer, boolean glowing) {
		if (visibleToPlayer)
			return RenderType.itemEntityTranslucentCull(tex);
		if (visible)
			return RenderType.entityCutoutNoCull(tex);
		return glowing ? RenderType.outline(tex) : null;
	}

	/**
	 * <p>Renders the given part on the given entity.</p>
	 * <p>Override this method to perform your own rendering!</p>
	 * @param ctx The render context.
	 */
	protected void doRender(RenderContext ctx) {
		if (modelPart != null)
			modelPart.render(ctx);
	}
}