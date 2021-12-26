/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * Provides an additional {@link RenderType} or two.
 * @author EnderTurret
 */
public class RenderStates extends RenderState {

	private RenderStates() {
		super(null, null, null);
		throw new IllegalStateException();
	}

	/**
	 * Returns a {@link RenderType} much like {@link RenderType#entityCutoutNoCull(ResourceLocation)}, but with diffuse lighting disabled.
	 * @param locationIn The texture location.
	 * @return The newly created {@link RenderType}.
	 */
	public static RenderType getPartPreview(ResourceLocation locationIn) {
		final RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(locationIn, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setDiffuseLightingState(NO_DIFFUSE_LIGHTING)
				.setAlphaState(DEFAULT_ALPHA)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setCullState(NO_CULL)
				.createCompositeState(true);

		return RenderType.create("part_preview", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, state);
	}
}