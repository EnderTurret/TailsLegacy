package uk.kihira.tails.neoforge.client.texture;

import com.mojang.blaze3d.platform.NativeImage;

import uk.kihira.tails.common2.ABGRColor;
import uk.kihira.tails.common2.JavaColor;
import uk.kihira.tails.common2.client.duck.TailsImage;

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
		return JavaColor.fromABGR(image.getPixelRGBA(x, y), false);
	}

	@Override
	public void putRGBA(int x, int y, int pixel) {
		image.setPixelRGBA(x, y, ABGRColor.fromARGB(pixel, false));
	}
}