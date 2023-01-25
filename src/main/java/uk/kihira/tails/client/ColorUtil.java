/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.awt.Color;

import com.mojang.blaze3d.platform.NativeImage;

/**
 * Miscellaneous utilities for converting between {@link NativeImage} color values and {@link Color} color values.
 * This is needed because the packed versions are different:
 * {@link NativeImage} packs it in the format {@code ABGR} whereas {@link Color} packs it in the format {@code ARGB}.
 * @author EnderTurret
 */
public final class ColorUtil {

	/**
	 * Packs the given color values into the format used in {@link NativeImage}.
	 * @param alpha The alpha value.
	 * @param red The red color value.
	 * @param green The green color value.
	 * @param blue The blue color value.
	 * @return The packed value.
	 */
	public static int combined(int alpha, int red, int green, int blue) {
		return NativeImage.combine(alpha, blue, green, red);
	}

	/**
	 * A convenience method for calling {@link #combined(int, int, int, int)} with an alpha value of {@code 255}.
	 * @param red The red color value.
	 * @param green The green color value.
	 * @param blue The blue color value.
	 * @return The packed value.
	 */
	public static int combined(int red, int green, int blue) {
		return combined(255, red, green, blue);
	}

	/**
	 * Converts the given {@link Color} RGB into a {@link NativeImage} color value.
	 * @param color The color to convert.
	 * @param ignoreAlpha Whether to ignore the alpha bits in the given color.
	 * @return The converted color.
	 */
	public static int fromJavaColor(int color, boolean ignoreAlpha) {
		return combined(ignoreAlpha ? 255 : color >> 24 & 0xFF, color >> 16 & 0xFF, color >> 8 & 0xFF, color >> 0 & 0xFF);
	}

	/**
	 * A convenience method to call {@link #fromJavaColor(int, boolean)} using {@link Color#getRGB()}.
	 * @param color The color to convert.
	 * @param ignoreAlpha Whether to ignore the alpha bits in the given color.
	 * @return The converted color.
	 */
	public static int fromJavaColor(Color color, boolean ignoreAlpha) {
		return fromJavaColor(color.getRGB(), ignoreAlpha);
	}

	/**
	 * Converts the given {@link NativeImage} color into a {@link Color} RGB value.
	 * @param combined The combined value to convert.
	 * @return The converted color.
	 */
	public static int toJavaColor(int combined) {
		return (NativeImage.getA(combined) & 0xFF) << 24 |
				(NativeImage.getR(combined) & 0xFF) << 16 |
				(NativeImage.getG(combined) & 0xFF) << 8  |
				(NativeImage.getB(combined) & 0xFF) << 0;
	}

	/**
	 * Calculates and returns the hexadecimal value for the given {@link NativeImage} color value.
	 * @param combined The combined color value.
	 * @param ignoreAlpha Whether to ignore the alpha bits in the value.
	 * @param java Whether the color is in Java format -- that is, ARGB -- versus the {@link NativeImage} format (BGRA).
	 * @return The hex string.
	 */
	public static String hex(int combined, boolean ignoreAlpha, boolean java) {
		if (!java)
			return (ignoreAlpha ? "" : Integer.toHexString(NativeImage.getA(combined)))
					+ String.format("%02x%02x%02x", NativeImage.getR(combined), NativeImage.getG(combined), NativeImage.getB(combined));

		return (ignoreAlpha ? "" : Integer.toHexString(combined >> 24 & 0xFF))
				+ String.format("%02x%02x%02x", combined >> 16 & 0xFF, combined >> 8 & 0xFF, combined & 0xFF);
	}

	/**
	 * Converts the given hex string into a {@link NativeImage} color value.
	 * @param hex The hexadecimal string to convert.
	 * @param ignoreAlpha Whether to ignore the alpha value in the string, if present.
	 * @return The converted value.
	 * @throws NumberFormatException
	 */
	public static int fromRGBAHex(String hex, boolean ignoreAlpha) throws NumberFormatException {
		return fromJavaColor(Integer.parseInt(hex, 16), ignoreAlpha);
	}
}