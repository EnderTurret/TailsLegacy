/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.duck;

import java.util.function.BiConsumer;

public interface TailsBuffer {
	public void t$submitCustomGeometry(TailsPoseStack poseStack, BiConsumer<TailsPoseStack.Entry, TailsVertexConsumer> renderer);
	public void t$submitModelPart(TailsModelPart part, TailsPoseStack poseStack, int packedLight, int packedOverlay, int color);
}