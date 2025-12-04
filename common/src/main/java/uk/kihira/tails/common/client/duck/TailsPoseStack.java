/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.duck;

public interface TailsPoseStack {

	public void t$push();
	public void t$pop();

	public void t$translate(double x, double y, double z);
	public void t$translate(float x, float y, float z);

	public void t$rotateX(float radians);
	public void t$rotateY(float radians);
	public void t$rotateZ(float radians);

	public void t$scale(float x, float y, float z);

	public Entry t$lastEntry();

	public interface Entry {}
}