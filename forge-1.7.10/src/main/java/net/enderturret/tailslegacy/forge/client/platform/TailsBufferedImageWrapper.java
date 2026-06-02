/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.platform;

import java.awt.image.BufferedImage;

import net.enderturret.tailslegacy.common.client.duck.TailsImage;

public final class TailsBufferedImageWrapper implements TailsImage {

	private final BufferedImage image;

	public TailsBufferedImageWrapper(BufferedImage image) {
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
		return image.getRGB(x, y);
	}

	@Override
	public void putRGBA(int x, int y, int pixel) {
		image.setRGB(x, y, pixel);
	}
}