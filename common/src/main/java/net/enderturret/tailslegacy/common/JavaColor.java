/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common;

public final class JavaColor {

	private JavaColor() {}

	public static int fromABGR(int abgr, boolean includeAlpha) {
		return pack(
				includeAlpha ? ABGRColor.alpha(abgr) : 0xFF,
				ABGRColor.red(abgr),
				ABGRColor.green(abgr),
				ABGRColor.blue(abgr)
				);
	}

	public static int pack(int alpha, int red, int green, int blue) {
		return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF) << 0;
	}

	public static int pack(int red, int green, int blue) {
		return pack(255, red, green, blue);
	}

	public static int alpha(int packed) {
		return (packed >> 24) & 0xFF;
	}

	public static int red(int packed) {
		return (packed >> 16) & 0xFF;
	}

	public static int green(int packed) {
		return (packed >> 8) & 0xFF;
	}

	public static int blue(int packed) {
		return (packed >> 0) & 0xFF;
	}

	public static String hex(int combined, boolean ignoreAlpha) {
		return (ignoreAlpha ? "" : Integer.toHexString(alpha(combined)))
				+ String.format("%02x%02x%02x", red(combined), green(combined), blue(combined));
	}
}