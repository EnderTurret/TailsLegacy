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

public class RenderStates extends RenderState {

	private RenderStates() {
		super(null, null, null);
		throw new IllegalStateException();
	}

	public static RenderType getWings(ResourceLocation tex) {
		final RenderType.State state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(tex, false, false))
				.transparency(NO_TRANSPARENCY)
				.diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
				.alpha(DEFAULT_ALPHA)
				.cull(CULL_DISABLED)
				.lightmap(LIGHTMAP_DISABLED)
				.overlay(OVERLAY_DISABLED)
				.build(false);

		return RenderType.makeType("position_tex", DefaultVertexFormats.POSITION_TEX, 7, 256, true, false, state);
	}

	public static RenderType getPartPreview(ResourceLocation locationIn) {
		final RenderType.State state = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(locationIn, false, false))
				.transparency(NO_TRANSPARENCY)
				.diffuseLighting(DIFFUSE_LIGHTING_DISABLED)
				.alpha(DEFAULT_ALPHA)
				.lightmap(LIGHTMAP_ENABLED)
				.overlay(OVERLAY_ENABLED)
				.cull(CULL_DISABLED)
				.build(true);

		return RenderType.makeType("part_preview", DefaultVertexFormats.ENTITY, 7, 256, true, false, state);
	}
}