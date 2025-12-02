/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.render;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;

/**
 * Contains all the context necessary for rendering parts.
 * Created to consolidate the hundreds of parameters being accumulated in the render methods.
 * @author EnderTurret
 */
public final class RenderContext {

	private final TailsPoseStack poseStack;
	private final TailsBufferSource bufferSource;
	private final TailsBuffer buffer;
	private final int packedLight;
	private final int packedOverlay;
	private final int color;
	private final float partialTick;
	private final TailsEntity entity;
	private final @Nullable ClientPartsData parts;
	private final ClientPartInfo info;

	/**
	 * Constructs a new {@code RenderContext}.
	 * @param poseStack The {@link TailsPoseStack} to use for transformations.
	 * @param bufferSource The buffer source.
	 * @param buffer The buffer to render to.
	 * @param packedLight The packed light.
	 * @param packedOverlay The packed overlay.
	 * @param color The color.
	 * @param partialTick The partial tick.
	 * @param entity The entity being rendered.
	 * @param parts The entity's full part data.
	 * @param info The part data.
	 */
	public RenderContext(
			TailsPoseStack poseStack, TailsBufferSource bufferSource, TailsBuffer buffer, int packedLight, int packedOverlay,
			int color, float partialTick, TailsEntity entity, @Nullable ClientPartsData parts, ClientPartInfo info) {
		this.poseStack = poseStack;
		this.bufferSource = bufferSource;
		this.buffer = buffer;
		this.packedLight = packedLight;
		this.packedOverlay = packedOverlay;
		this.color = color;
		this.partialTick = partialTick;
		this.entity = entity;
		this.parts = parts;
		this.info = info;
	}

	public TailsPoseStack poseStack() {
		return poseStack;
	}

	public TailsBufferSource bufferSource() {
		return bufferSource;
	}

	public TailsBuffer buffer() {
		return buffer;
	}

	public int packedLight() {
		return packedLight;
	}

	public int packedOverlay() {
		return packedOverlay;
	}

	public int color() {
		return color;
	}

	public float partialTick() {
		return partialTick;
	}

	public TailsEntity entity() {
		return entity;
	}

	public ClientPartsData parts() {
		return parts;
	}

	public ClientPartInfo info() {
		return info;
	}

	/**
	 * Calls {@link TailsModelPart#t$render(TailsPoseStack, TailsBuffer, int, int, int)} on the given part with parameters from this {@link RenderContext}.
	 * @param part The part to render.
	 * @param packedLight
	 * @param packedOverlay
	 */
	public void render(TailsModelPart part, int packedLight, int packedOverlay) {
		part.t$render(poseStack, buffer, packedLight, packedOverlay, color);
	}

	/**
	 * Calls {@link TailsModelPart#t$render(TailsPoseStack, TailsBuffer, int, int, int)} on the given part with parameters from this {@link RenderContext}.
	 * @param part The part to render.
	 */
	public void render(TailsModelPart part) {
		render(part, packedLight, packedOverlay);
	}

	public TailsModelPart getModel() {
		return info.getPart().getModel();
	}
}