/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025-2026 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.duck;

public interface TailsVertexConsumer {

	public TailsVertexConsumer t$beginVertex(TailsPoseStack.Entry pose, float x, float y, float z);
	public TailsVertexConsumer t$color(int color);
	public TailsVertexConsumer t$uv(float u, float v);
	public TailsVertexConsumer t$overlay(int overlay);
	public TailsVertexConsumer t$light(int light);
	public TailsVertexConsumer t$normal(float x, float y, float z);
	public TailsVertexConsumer t$normal(TailsPoseStack.Entry pose, float x, float y, float z);
	public TailsVertexConsumer t$endVertex();
}