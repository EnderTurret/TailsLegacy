/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;

/**
 * Provides an additional {@link RenderType} or two.
 * @author EnderTurret
 */
public class RenderStates extends RenderStateShard {

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
		final RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setDiffuseLightingState(NO_DIFFUSE_LIGHTING)
				.setAlphaState(DEFAULT_ALPHA)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setCullState(NO_CULL)
				.createCompositeState(true);

		return RenderType.create("part_preview", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, state);
	}
}