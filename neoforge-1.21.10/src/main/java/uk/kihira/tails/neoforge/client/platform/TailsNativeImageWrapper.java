/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.platform;

import com.mojang.blaze3d.platform.NativeImage;

import uk.kihira.tails.common.client.duck.TailsImage;

public final class TailsNativeImageWrapper implements TailsImage {

	private final NativeImage image;

	public TailsNativeImageWrapper(NativeImage image) {
		this.image = image;
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}

	@Override
	public int getRGBA(int x, int y) {
		return image.getPixel(x, y);
	}

	@Override
	public void putRGBA(int x, int y, int pixel) {
		image.setPixel(x, y, pixel);
	}
}