/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.render;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;

import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;

public record PartPreviewRenderState(
		TailsEntity entity,
		ClientPartInfo partInfo,
		float partialTick,
		Matrix3x2f pose,
		int x0, int y0, int x1, int y1,
		@Nullable ScreenRectangle scissorArea,
		@Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {

	public PartPreviewRenderState(TailsEntity entity, ClientPartInfo partInfo, float partialTick, Matrix3x2f pose, int x0, int y0, int x1, int y1,
			@Nullable ScreenRectangle scissorArea) {
		this(entity, partInfo, partialTick, pose, x0, y0, x1, y1, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
	}

	@Override
	public float scale() {
		return 1;
	}
}