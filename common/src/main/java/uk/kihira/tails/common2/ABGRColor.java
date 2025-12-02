package uk.kihira.tails.common2;

public final class ABGRColor {

	private ABGRColor() {}

	public static int fromARGB(int argb, boolean includeAlpha) {
		return pack(
				includeAlpha ? JavaColor.alpha(argb) : 0xFF,
				JavaColor.blue(argb),
				JavaColor.green(argb),
				JavaColor.red(argb)
				);
	}

	public static int pack(int alpha, int blue, int green, int red) {
		return (alpha & 0xFF) << 24 | (blue & 0xFF) << 16 | (green & 0xFF) << 8 | (red & 0xFF) << 0;
	}

	public static int pack(int blue, int green, int red) {
		return pack(255, blue, green, red);
	}

	public static int alpha(int packed) {
		return (packed >> 24) & 0xFF;
	}

	public static int blue(int packed) {
		return (packed >> 16) & 0xFF;
	}

	public static int green(int packed) {
		return (packed >> 8) & 0xFF;
	}

	public static int red(int packed) {
		return (packed >> 0) & 0xFF;
	}
}