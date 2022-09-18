/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Provides an additional {@link RenderType} or two.
 * @author EnderTurret
 */
public final class RenderStates extends RenderStateShard {

	public static final Vector3f PART_PREVIEW_DIFFUSE_LIGHTING_0 = new Vector3f(0, 0, 0);
	public static final Vector3f PART_PREVIEW_DIFFUSE_LIGHTING_1 = new Vector3f(0, 0, 1);

	private RenderStates() {
		super(null, null, null);
		throw new IllegalStateException();
	}

	/**
	 * Returns a {@link RenderType} much like {@link RenderType#entityCutoutNoCull(ResourceLocation)}, but with diffuse lighting disabled.
	 * @param location The texture location.
	 * @return The newly created {@link RenderType}.
	 */
	public static RenderType getPartPreview(ResourceLocation location) {
		final RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
				.setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER)
				.setTransparencyState(NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true);

		return RenderType.create("part_preview", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, state);
	}
}