package uk.kihira.tails.common.client;

import static uk.kihira.tails.common.JavaColor.*;

import uk.kihira.tails.common.JavaColor;
import uk.kihira.tails.common.client.duck.TailsImage;
import uk.kihira.tails.common.client.part.Part;

public class TripleTintTextureHelper {

	private static final int MINBRIGHTNESS = 22;

	public static void colorise(TailsImage texture, Part.TintingStrategy strategy, int tint1, int tint2, int tint3) {
		if (strategy == Part.TintingStrategy.TRIPLE_TINT)
			colorise(texture, tint1, tint2, tint3);
	}

	/**
	 * Colorises the given image.
	 * @param texture The input texture.
	 * @param tint1 The first tint.
	 * @param tint2 The second tint.
	 * @param tint3 The third tint.
	 * @see #colorise(int, int, int, int, int, int, int)
	 */
	private static void colorise(TailsImage texture, int tint1, int tint2, int tint3) {
		for (int x = 0; x < texture.getWidth(); x++)
			for (int y = 0; y < texture.getHeight(); y++) {
				final int rgb = texture.getRGBA(x, y);
				final int newRgb = colorise(rgb, tint1, tint2, tint3);

				if (rgb == newRgb) continue;

				texture.putRGBA(x, y, newRgb);
			}
	}

	/**
	 * Recolors the given input color according to the three given tints.
	 * See {@link #colorise(int, int, int, int, int, int, int)} for more information.
	 * @param input The input color in {@linkplain JavaColor#pack(int, int, int, int) ARGB form}.
	 * @param tint1 The first tint in ABGR form.
	 * @param tint2 The second tint in ABGR form.
	 * @param tint3 The third tint in ABGR form.
	 * @return The resultant color in ABGR form.
	 */
	private static int colorise(int input, int tint1, int tint2, int tint3) {
		final int a = alpha(input);
		if (a == 0) return input;
		final int r = red(input);
		final int g = green(input);
		final int b = blue(input);

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
	 * @return The new color, packed using {@linkplain JavaColor#pack(int, int, int, int)}.
	 */
	private static int colorise(int saturation, int weight2, int weight3, int alpha, int tint1, int tint2, int tint3) {
		double w2 = weight2 / 255D;
		final double w3 = weight3 / 255D;

		w2 *= 1 - w3;

		final double w1 = 1 - (w2 + w3);

		final double r1 = scale(red(tint1)) / 255;
		final double g1 = scale(green(tint1)) / 255;
		final double b1 = scale(blue(tint1)) / 255;

		final double r2 = scale(red(tint2)) / 255;
		final double g2 = scale(green(tint2)) / 255;
		final double b2 = scale(blue(tint2)) / 255;

		final double r3 = scale(red(tint3)) / 255;
		final double g3 = scale(green(tint3)) / 255;
		final double b3 = scale(blue(tint3)) / 255;

		final int rfinal = (int) Math.floor(saturation * (r1 * w1 + r2 * w2 + r3 * w3));
		final int gfinal = (int) Math.floor(saturation * (g1 * w1 + g2 * w2 + g3 * w3));
		final int bfinal = (int) Math.floor(saturation * (b1 * w1 + b2 * w2 + b3 * w3));

		return pack(alpha, rfinal, gfinal, bfinal);
	}

	private static double scale(int color) {
		return MINBRIGHTNESS + (int) Math.floor(color * ((255 - MINBRIGHTNESS) / 255.0));
	}
}