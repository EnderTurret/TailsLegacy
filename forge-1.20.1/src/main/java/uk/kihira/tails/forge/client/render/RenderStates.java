/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.render;

import org.joml.Vector3f;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Provides an additional {@link RenderType} or two.
 * @author EnderTurret
 */
public final class RenderStates extends RenderStateShard {

	/**
	 * Defines the first vector of the part preview diffuse lighting.
	 * @see RenderSystem#setShaderLights(Vector3f, Vector3f)
	 * @see Lighting
	 */
	public static final Vector3f PART_PREVIEW_DIFFUSE_LIGHTING_0 = new Vector3f(0, 0, 0);
	/**
	 * Defines the second vector of the part preview diffuse lighting.
	 * @see RenderSystem#setShaderLights(Vector3f, Vector3f)
	 * @see Lighting
	 */
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