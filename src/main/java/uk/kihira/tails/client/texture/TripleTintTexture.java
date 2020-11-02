/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.texture;

import static net.minecraft.client.renderer.texture.NativeImage.getAlpha;
import static net.minecraft.client.renderer.texture.NativeImage.getBlue;
import static net.minecraft.client.renderer.texture.NativeImage.getGreen;
import static net.minecraft.client.renderer.texture.NativeImage.getRed;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.NativeImage.PixelFormat;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import uk.kihira.tails.client.ColorUtil;

/**
 * A texture that tints another texture based on three tint values.
 */
public class TripleTintTexture extends Texture {

	private final String namespace;
	private final String texturename;
	private final int tint1;
	private final int tint2;
	private final int tint3;

	private static final int MINBRIGHTNESS = 22;

	public TripleTintTexture(String namespace, String texturename, int tint1, int tint2, int tint3) {
		this.namespace = namespace;
		this.texturename = texturename;
		this.tint1 = ColorUtil.fromJavaColor(tint1, true);
		this.tint2 = ColorUtil.fromJavaColor(tint2, true);
		this.tint3 = ColorUtil.fromJavaColor(tint3, true);
	}

	@Override
	public void loadTexture(IResourceManager manager) throws IOException {
		deleteGlTexture();

		try
		{
			if (texturename != null)
			{
				final InputStream inputstream = manager.getResource(new ResourceLocation(namespace, texturename)).getInputStream();
				final NativeImage texture = NativeImage.read(PixelFormat.RGBA, inputstream);

				for (int x = 0; x < texture.getWidth(); x++)
					for (int y = 0; y < texture.getHeight(); y++) {
						final int rgb = texture.getPixelRGBA(x, y);
						final int a = getAlpha(rgb);
						if (a == 0) continue;
						final int r = getRed(rgb);
						final int g = getGreen(rgb);
						final int b = getBlue(rgb);

						texture.setPixelRGBA(x, y, colorise(r, tint1, g, tint2, b, tint3, a));
					}

				TextureUtil.prepareImage(getGlTextureId(), texture.getWidth(), texture.getHeight());
				texture.uploadTextureSub(0, 0, 0, true);
			}
		}
		catch (IOException ioexception)
		{
			LogManager.getLogger().error("Couldn't load triple tint texture image", ioexception);
		}
	}

	/**
	 * Colorises a pixel.
	 * @param red The red color value.
	 * @param tint1 The first tint.
	 * @param green The green color value.
	 * @param tint2 The second tint.
	 * @param blue The blue color value.
	 * @param tint3 The third tint.
	 * @param alpha The alpha value.
	 * @return The colorised pixel, packed using {@link NativeImage#getCombined(int, int, int, int)}.
	 */
	private int colorise(int red, int tint1, int green, int tint2, int blue, int tint3, int alpha) {
		double g = green / 255D;
		final double b = blue / 255D;

		g *= 1 - b;

		final double r = 1 - (g + b);

		final double r1 = scale(getRed(tint1), MINBRIGHTNESS) / 255;
		final double g1 = scale(getGreen(tint1), MINBRIGHTNESS) / 255;
		final double b1 = scale(getBlue(tint1), MINBRIGHTNESS) / 255;

		final double r2 = scale(getRed(tint2), MINBRIGHTNESS) / 255;
		final double g2 = scale(getGreen(tint2), MINBRIGHTNESS) / 255;
		final double b2 = scale(getBlue(tint2), MINBRIGHTNESS) / 255;

		final double r3 = scale(getRed(tint3), MINBRIGHTNESS) / 255;
		final double g3 = scale(getGreen(tint3), MINBRIGHTNESS) / 255;
		final double b3 = scale(getBlue(tint3), MINBRIGHTNESS) / 255;

		final int rfinal = (int) Math.floor(red * (r1 * r + r2 * g + r3 * b));
		final int gfinal = (int) Math.floor(red * (g1 * r + g2 * g + g3 * b));
		final int bfinal = (int) Math.floor(red * (b1 * r + b2 * g + b3 * b));

		return NativeImage.getCombined(alpha, bfinal, gfinal, rfinal);
	}

	private double scale(int color, int min) {
		return min + (int) Math.floor(color * ((255 - min) / 255.0));
	}
}