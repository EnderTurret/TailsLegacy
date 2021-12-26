/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartRegistry;

/**
 * A renderer for a part. It also keeps track of some metadata.<br>
 * You can register one in {@link PartRegistry#register(Part)} and {@link PartRenderRegistry#register(Part, PartRenderer)}.
 */
@OnlyIn(Dist.CLIENT)
public class PartRenderer {

	@Nullable
	public final PartModel modelPart;

	public PartRenderer(@Nullable PartModel modelPart) {
		this.modelPart = modelPart;
	}

	public void compileTextureIfNeeded(LivingEntity entity, PartInfo info) {
		if (!info.isEmpty() && (info.needsTextureCompile || info.getTexture() == null)) {
			info.setTexture(TextureHelper.generateTexture(entity.getUUID(), info));
			info.needsTextureCompile = false;
		}
	}

	/**
	 * A pre-render callback for translation, rotation, and making sure the texture exists.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity that is about to be used for rendering.
	 * @param info The {@link PartInfo} about to be rendered.
	 * @param bufferIn The render type buffers. Usually obtained from {@link Minecraft#renderBuffers()}.
	 * @param builderIn The vertex builder for rendering, in case an {@link IRenderHelper} wants to do some rendering.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTicks The current partial tick value.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay.
	 * @param red The red color value.
	 * @param green The green color value.
	 * @param blue The blue color value.
	 * @param alpha The transparency value.
	 */
	public void preRender(PoseStack matrixStack, LivingEntity entity, PartInfo info, MultiBufferSource bufferIn, VertexConsumer builderIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		compileTextureIfNeeded(entity, info);

		if (modelPart != null) {
			modelPart.setupAnim(entity, entity.animationPosition, entity.animationSpeed, partialTicks, info.getSubType(), entity.xRot);
			modelPart.prepareMobModel(entity, entity.animationPosition, entity.animationSpeed, partialTicks);
		}

		// Support for Galacticraft as it adds its own EntityPlayer.
		final List<IRenderHelper<?>> helpers = entity instanceof Player ? RenderHelperManager.getRenderHelpers(Player.class) : RenderHelperManager.getRenderHelpers(entity.getClass());

		for (IRenderHelper helper : helpers)
			helper.onPreRenderTail(matrixStack, entity, this, info, bufferIn, builderIn, x, y, z, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	/**
	 * Renders the given part on the given entity.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link PartInfo}.
	 * @param bufferIn The {@link IRenderTypeBuffer} to retrieve an {@link IVertexBuilder} from.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTicks The current partial ticks.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 * @param red The red color value.
	 * @param green The green color value.
	 * @param blue The blue color value.
	 * @param alpha The transparency value.
	 */
	public void render(PoseStack matrixStack, LivingEntity entity, PartInfo info, MultiBufferSource bufferIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (!info.isEmpty()) {
			compileTextureIfNeeded(entity, info);

			final boolean visible = !entity.isInvisible();
			final boolean visibleToPlayer = !visible && !entity.isInvisibleTo(Minecraft.getInstance().player);
			final boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(entity);

			final RenderType type = getRenderType(entity, info.getTexture(), visible, visibleToPlayer, glowing);

			if (type == null) return;

			alpha = visibleToPlayer && alpha == 1F ? 0.15F : alpha;
			final VertexConsumer buf = bufferIn.getBuffer(type);

			render(matrixStack, entity, info, bufferIn, buf, x, y, z, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}

	/**
	 * Renders the given part on the given entity.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link PartInfo}.
	 * @param bufferIn The buffer to retrieve buffers from.
	 * @param builderIn The builder to draw to.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTicks The current partial ticks.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 * @param red The red color value.
	 * @param green The green color value.
	 * @param blue The blue color value.
	 * @param alpha The transparency value.
	 */
	public void render(PoseStack matrixStack, LivingEntity entity, PartInfo info, MultiBufferSource bufferIn, VertexConsumer builderIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (!info.isEmpty()) {
			matrixStack.pushPose();

			preRender(matrixStack, entity, info, bufferIn, builderIn, x, y, z, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			doRender(matrixStack, entity, info, builderIn, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			matrixStack.popPose();
		}
	}

	@Nullable
	protected RenderType getRenderType(LivingEntity entity, ResourceLocation tex, boolean visible, boolean visibleToPlayer, boolean glowing) {
		if (visibleToPlayer)
			return RenderType.itemEntityTranslucentCull(tex);
		else if (visible)
			return RenderType.entityCutoutNoCull(tex);
		else
			return glowing ? RenderType.outline(tex) : null;
	}

	/**
	 * Renders the given part on the given entity.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link PartInfo}.
	 * @param bufferIn The buffer to draw to.
	 * @param partialTicks The current partial ticks.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 * @param red The red color value.
	 * @param green The green color value.
	 * @param blue The blue color value.
	 * @param alpha The transparency value.
	 */
	protected void doRender(PoseStack matrixStack, LivingEntity entity, PartInfo info, VertexConsumer bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (modelPart != null)
			modelPart.render(matrixStack, bufferIn, entity, packedLightIn, packedOverlayIn, red, green, blue, alpha, info.getSubType(), partialTicks);
	}
}