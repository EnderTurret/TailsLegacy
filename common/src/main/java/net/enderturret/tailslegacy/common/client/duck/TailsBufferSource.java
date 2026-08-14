/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.duck;

import org.jetbrains.annotations.Nullable;

/**
 * Bindings for a {@code MultiBufferSource}, {@code SubmitNodeStorage}, or {@code Tessellator}/{@code BufferBuilder} combo.
 * @author EnderTurret
 */
public interface TailsBufferSource {

	/**
	 * Returns a {@link TailsBuffer} using the specified texture, for rendering things on the specified entity.
	 * This handles switching the {@code RenderType} based on the entity being visible, partially visible, or glowing.
	 * If the entity is completely invisible, this returns {@code null}.
	 * @param entity The entity to create a buffer for.
	 * @param texture The texture to configure the buffer with.
	 * @return The buffer, or {@code null}.
	 */
	public @Nullable TailsBuffer t$getEntityBuffer(TailsEntity entity, TResourceLocation texture);
}