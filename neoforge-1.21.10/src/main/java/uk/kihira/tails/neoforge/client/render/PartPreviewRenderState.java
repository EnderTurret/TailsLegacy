package uk.kihira.tails.neoforge.client.render;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.part.ClientPartInfo;

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