/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.texture;

import static com.mojang.blaze3d.platform.NativeImage.getA;
import static com.mojang.blaze3d.platform.NativeImage.getB;
import static com.mojang.blaze3d.platform.NativeImage.getG;
import static com.mojang.blaze3d.platform.NativeImage.getR;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.blaze3d.platform.TextureUtil;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import uk.kihira.tails.client.ColorUtil;
import uk.kihira.tails.common.Tails;

/**
 * A texture that tints another texture based on three tint values.
 */
public class TripleTintTexture extends AbstractTexture {

	private final String namespace;
	private final String texturename;
	private final int tint1;
	private final int tint2;
	private final int tint3;

	private static final int MINBRIGHTNESS = 22;

	public TripleTintTexture(String namespace, String texturename, int tint1, int tint2, int tint3) {
		this.namespace = Objects.requireNonNull(namespace);
		this.texturename = Objects.requireNonNull(texturename);
		this.tint1 = ColorUtil.fromJavaColor(tint1, true);
		this.tint2 = ColorUtil.fromJavaColor(tint2, true);
		this.tint3 = ColorUtil.fromJavaColor(tint3, true);
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		releaseId();

		try {
			try (InputStream inputstream = manager.getResource(new ResourceLocation(namespace, texturename)).get().open()) {
				final NativeImage texture = NativeImage.read(Format.RGBA, inputstream);

				colorise(texture, tint1, tint2, tint3);

				TextureUtil.prepareImage(getId(), texture.getWidth(), texture.getHeight());
				texture.upload(0, 0, 0, true);
			}
		} catch (IOException e) {
			Tails.LOGGER.error("Couldn't load triple tint texture image", e);
		}
	}

	/**
	 * Colorises the given image.
	 * @param texture The input texture.
	 * @param tint1 The first tint.
	 * @param tint2 The second tint.
	 * @param tint3 The third tint.
	 * @see #colorise(int, int, int, int, int, int, int)
	 */
	private static void colorise(NativeImage texture, int tint1, int tint2, int tint3) {
		for (int x = 0; x < texture.getWidth(); x++)
			for (int y = 0; y < texture.getHeight(); y++) {
				final int rgb = texture.getPixelRGBA(x, y);
				final int newRgb = colorise(rgb, tint1, tint2, tint3);

				if (rgb == newRgb) continue;

				texture.setPixelRGBA(x, y, newRgb);
			}
	}

	/**
	 * Recolors the given input color according to the three given tints.
	 * See {@link #colorise(int, int, int, int, int, int, int)} for more information.
	 * @param input The input color in {@linkplain NativeImage#combine(int, int, int, int) ABGR form}.
	 * @param tint1 The first tint in ABGR form.
	 * @param tint2 The second tint in ABGR form.
	 * @param tint3 The third tint in ABGR form.
	 * @return The resultant color in ABGR form.
	 */
	private static int colorise(int input, int tint1, int tint2, int tint3) {
		final int a = getA(input);
		if (a == 0) return input;
		final int r = getR(input);
		final int g = getG(input);
		final int b = getB(input);

		return colorise(r, g, b, a, tint1, tint2, tint3);
	}

	/**
	 * <p>
	 * Transforms a color using three tints.
	 * The weight values determine how much of each tint the resulting color uses.
	 * </p>
	 * <p>
	 * As a better explanation, imagine a grid of color where the x axis is {@code weight2} and the y axis is {@code weight3}.
	 * Now, imagine a second grid where the first tint is most localized in the top-left corner,
	 * the second tint is most localized in the top-right corner, and the third tint is localized along the bottom.
	 * This together makes up a triple gradient-esque square where each tint is represented and forms smooth transitions between each other.
	 * This method maps the location given by the weight values to the corresponding location on the second grid and returns the color there.
	 * </p>
	 * @param saturation The saturation of the resulting color.
	 * @param weight2 The second weight value. This determines the x coordinate on the grid mentioned in the documentation here.
	 * @param weight3 The third weight value. This determines the y coordinate on the grid mentioned in the documentation here.
	 * @param alpha The transparency of the resulting color.
	 * @param tint1 The first tint.
	 * @param tint2 The second tint.
	 * @param tint3 The third tint.
	 * @return The new color, packed using {@link NativeImage#combine(int, int, int, int)}.
	 */
	private static int colorise(int saturation, int weight2, int weight3, int alpha, int tint1, int tint2, int tint3) {
		double w2 = weight2 / 255D;
		final double w3 = weight3 / 255D;

		w2 *= 1 - w3;

		final double w1 = 1 - (w2 + w3);

		final double r1 = scale(getR(tint1)) / 255;
		final double g1 = scale(getG(tint1)) / 255;
		final double b1 = scale(getB(tint1)) / 255;

		final double r2 = scale(getR(tint2)) / 255;
		final double g2 = scale(getG(tint2)) / 255;
		final double b2 = scale(getB(tint2)) / 255;

		final double r3 = scale(getR(tint3)) / 255;
		final double g3 = scale(getG(tint3)) / 255;
		final double b3 = scale(getB(tint3)) / 255;

		final int rfinal = (int) Math.floor(saturation * (r1 * w1 + r2 * w2 + r3 * w3));
		final int gfinal = (int) Math.floor(saturation * (g1 * w1 + g2 * w2 + g3 * w3));
		final int bfinal = (int) Math.floor(saturation * (b1 * w1 + b2 * w2 + b3 * w3));

		return NativeImage.combine(alpha, bfinal, gfinal, rfinal);
	}

	private static double scale(int color) {
		return MINBRIGHTNESS + (int) Math.floor(color * ((255 - MINBRIGHTNESS) / 255.0));
	}
}