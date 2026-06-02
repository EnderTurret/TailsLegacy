/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.duck;

public interface TailsImage {

	public int getWidth();
	public int getHeight();

	public int getRGBA(int x, int y);
	public void putRGBA(int x, int y, int pixel);
}