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

import org.apache.logging.log4j.LogManager;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.blaze3d.platform.TextureUtil;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import uk.kihira.tails.client.ColorUtil;

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
		this.namespace = namespace;
		this.texturename = texturename;
		this.tint1 = ColorUtil.fromJavaColor(tint1, true);
		this.tint2 = ColorUtil.fromJavaColor(tint2, true);
		this.tint3 = ColorUtil.fromJavaColor(tint3, true);
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		releaseId();

		try
		{
			if (texturename != null)
				try (InputStream inputstream = manager.getResource(new ResourceLocation(namespace, texturename)).get().open()) {
					final NativeImage texture = NativeImage.read(Format.RGBA, inputstream);

					for (int x = 0; x < texture.getWidth(); x++)
						for (int y = 0; y < texture.getHeight(); y++) {
							final int rgb = texture.getPixelRGBA(x, y);
							final int a = getA(rgb);
							if (a == 0) continue;
							final int r = getR(rgb);
							final int g = getG(rgb);
							final int b = getB(rgb);

							texture.setPixelRGBA(x, y, colorise(r, tint1, g, tint2, b, tint3, a));
						}

					TextureUtil.prepareImage(getId(), texture.getWidth(), texture.getHeight());
					texture.upload(0, 0, 0, true);
				}
		}
		catch (IOException ioexception)
		{
			LogManager.getLogger().error("Couldn't load triple tint texture image", ioexception);
		}
	}

	/**
	 * Colorises a pixel.
	 * @param tone The red color value.
	 * @param tint1 The first tint.
	 * @param weight1 The green color value.
	 * @param tint2 The second tint.
	 * @param weight3 The blue color value.
	 * @param tint3 The third tint.
	 * @param alpha The alpha value.
	 * @return The colorised pixel, packed using {@link NativeImage#combine(int, int, int, int)}.
	 */
	private static int colorise(int tone, int tint1, int weight1, int tint2, int weight3, int tint3, int alpha) {
		double w2 = weight1 / 255D;
		final double w3 = weight3 / 255D;

		w2 *= 1 - w3;

		final double w1 = 1 - (w2 + w3);

		final double r1 = scale(getR(tint1), MINBRIGHTNESS) / 255;
		final double g1 = scale(getG(tint1), MINBRIGHTNESS) / 255;
		final double b1 = scale(getB(tint1), MINBRIGHTNESS) / 255;

		final double r2 = scale(getR(tint2), MINBRIGHTNESS) / 255;
		final double g2 = scale(getG(tint2), MINBRIGHTNESS) / 255;
		final double b2 = scale(getB(tint2), MINBRIGHTNESS) / 255;

		final double r3 = scale(getR(tint3), MINBRIGHTNESS) / 255;
		final double g3 = scale(getG(tint3), MINBRIGHTNESS) / 255;
		final double b3 = scale(getB(tint3), MINBRIGHTNESS) / 255;

		final int rfinal = (int) Math.floor(tone * (r1 * w1 + r2 * w2 + r3 * w3));
		final int gfinal = (int) Math.floor(tone * (g1 * w1 + g2 * w2 + g3 * w3));
		final int bfinal = (int) Math.floor(tone * (b1 * w1 + b2 * w2 + b3 * w3));

		return NativeImage.combine(alpha, bfinal, gfinal, rfinal);
	}

	private static double scale(int color, int min) {
		return min + (int) Math.floor(color * ((255 - min) / 255.0));
	}
}