/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;

public class PartPreviewRenderer extends PictureInPictureRenderer<PartPreviewRenderState> {

	public PartPreviewRenderer(BufferSource bufferSource) {
		super(bufferSource);
	}

	@Override
	public Class<PartPreviewRenderState> getRenderStateClass() {
		return PartPreviewRenderState.class;
	}

	@Override
	protected String getTextureLabel() {
		return "part_preview";
	}

	@Override
	protected void renderToTexture(PartPreviewRenderState renderState, PoseStack poseStack) {
		Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(180));
		poseStack.scale(50, 50, 50);

		final FeatureRenderDispatcher dispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
		final SubmitNodeStorage storage = dispatcher.getSubmitNodeStorage();

		final TailsBufferSource bufferSource = (TailsBufferSource) storage;

		renderState.partInfo().getRenderer().render(
				(TailsPoseStack) poseStack,
				renderState.entity(),
				null, renderState.partInfo(),
				bufferSource,
				0, 0, 0, renderState.partialTick(),
				LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFF);

		dispatcher.renderAllFeatures();

		poseStack.popPose();
	}

	@Override
	protected float getTranslateY(int height, int guiScale) {
		return height / 2F;
	}
}