/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.client.render.part;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.OverlayTexture;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.client.duck.TailsBuffer;
import uk.kihira.tails.common2.client.duck.TailsBufferSource;
import uk.kihira.tails.common2.client.duck.TailsEntity;
import uk.kihira.tails.common2.client.duck.TailsPoseStack;
import uk.kihira.tails.common2.client.model.PartModel;
import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.common2.client.part.ClientPartsData;
import uk.kihira.tails.common2.client.part.Part;
import uk.kihira.tails.common2.client.render.RenderContext;
import uk.kihira.tails.common2.client.render.helper.RenderHelperManager;

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

	public void compileTextureIfNeeded(TailsEntity entity, ClientPartInfo info) {
		info.checkTexture(entity.t$uuid(), false);
	}

	/**
	 * A pre-render callback for translation, rotation, etc.
	 * @param ctx The render context.
	 */
	public void preRender(RenderContext ctx) {
		try {
			if (modelPart != null) {
				modelPart.setupAnim(ctx.entity(), ctx.partialTick(), ctx.info().getSubType(), ctx.info().getPart().getModel());
			}

			RenderHelperManager.applyRenderHelpers(ctx, this);
		} catch (Exception e) {
			Tails.LOGGER.error("Exception rendering part:", e);
		}
	}

	/**
	 * Renders the given part on the given entity.
	 * @param poseStack The {@link TailsPoseStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param parts The entity's part data.
	 * @param info The {@link ClientPartInfo}.
	 * @param bufferSource The {@link TailsBufferSource} to retrieve a {@link TailsBuffer} from.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTick The current partial ticks.
	 * @param packedLight The packed light.
	 * @param packedOverlay The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 * @param alpha The transparency value.
	 */
	public void render(TailsPoseStack poseStack, TailsEntity entity, @Nullable ClientPartsData parts, ClientPartInfo info, TailsBufferSource bufferSource, double x, double y, double z, float partialTick, int packedLight, int packedOverlay, int alpha) {
		if (!info.isEmpty()) {
			info.checkTexture(entity.t$uuid(), false);

			final TailsBuffer buf = bufferSource.t$getEntityBuffer(entity, info.getTexture());
			if (buf == null) return;

			if (entity.t$isVisibleToPlayer() && alpha == 0xFF)
				alpha = 0x26;

			render(poseStack, entity, parts, info, bufferSource, buf, x, y, z, partialTick, packedLight, packedOverlay, alpha);
		}
	}

	/**
	 * Renders the given part on the given entity.
	 * @param poseStack The {@link TailsPoseStack} to use for transformations.
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
	public void render(TailsPoseStack poseStack, TailsEntity entity, @Nullable ClientPartsData parts, ClientPartInfo info, TailsBufferSource bufferSource, TailsBuffer buffer, double x, double y, double z, float partialTick, int packedLight, int packedOverlay, int alpha) {
		if (!info.isEmpty()) {
			int color = alpha << 24;

			if (info.getPartTexture().tintingStrategy() == Part.TintingStrategy.SINGLE_TINT) {
				final int tint = info.getTints()[0];
				color |= tint;
			} else
				color |= 0xFFFFFF;

			final RenderContext ctx = new RenderContext(
					poseStack, bufferSource, buffer, packedLight, packedOverlay,
					color, partialTick,
					entity, parts, info);

			poseStack.t$push();

			preRender(ctx);

			doRender(ctx);

			poseStack.t$pop();
		}
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