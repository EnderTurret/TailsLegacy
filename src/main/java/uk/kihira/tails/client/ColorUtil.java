package uk.kihira.tails.client;

import java.awt.Color;

import net.minecraft.client.renderer.texture.NativeImage;

public class ColorUtil {

	public static int combined(int alpha, int red, int green, int blue) {
		return NativeImage.getCombined(alpha, blue, green, red);
	}

	public static int combined(int red, int green, int blue) {
		return combined(255, red, green, blue);
	}

	public static int fromJavaColor(int color, boolean ignoreAlpha) {
		return combined(ignoreAlpha ? 255 : color >> 24 & 0xff, color >> 16 & 0xFF, color >> 8 & 0xFF, color >> 0 & 0xFF);
	}

	public static int fromJavaColor(Color color, boolean ignoreAlpha) {
		return fromJavaColor(color.getRGB(), ignoreAlpha);
	}

	public static int toJavaColor(int combined) {
		return (NativeImage.getAlpha(combined) & 0xFF) << 24 |
				(NativeImage.getRed(combined) & 0xFF) << 16 |
				(NativeImage.getGreen(combined) & 0xFF) << 8  |
				(NativeImage.getBlue(combined) & 0xFF) << 0;
	}

	public static String hex(int combined, boolean ignoreAlpha) {
		final String hex = Integer.toHexString(toJavaColor(combined));
		return ignoreAlpha ? hex.substring(2) : hex;
	}

	public static int fromRGBAHex(String hex, boolean ignoreAlpha) throws NumberFormatException {
		return fromJavaColor(Integer.valueOf(hex, 16), ignoreAlpha);
	}
}