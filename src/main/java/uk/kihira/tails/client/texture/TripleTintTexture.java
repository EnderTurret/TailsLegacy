/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.texture;

import static net.minecraft.client.renderer.texture.NativeImage.getAlpha;
import static net.minecraft.client.renderer.texture.NativeImage.getRed;
import static net.minecraft.client.renderer.texture.NativeImage.getBlue;
import static net.minecraft.client.renderer.texture.NativeImage.getGreen;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.NativeImage.PixelFormat;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import uk.kihira.tails.client.ColorUtil;

import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * A tinted texture that has 3 different tints, each tint defined in a different RGB channel
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
                InputStream inputstream = manager.getResource(new ResourceLocation(namespace, texturename)).getInputStream();
                NativeImage texture = NativeImage.read(PixelFormat.RGBA, inputstream);

                for (int x = 0; x < texture.getWidth(); x++)
                	for (int y = 0; y < texture.getHeight(); y++) {
                		final int rgb = texture.getPixelRGBA(x, y);
                		final int a = getAlpha(rgb);
                		if (a == 0) continue;
                		final int r = getRed(rgb);
                		final int g = getGreen(rgb);
                		final int b = getBlue(rgb);

                		texture.setPixelRGBA(x, y, colourise(r, this.tint1, g, this.tint2, b, this.tint3, a));
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
     * Colourises a pixel that has the color model TYPE_INT_ARGB
     * @param tone
     * @param c1
     * @param weight1
     * @param c2
     * @param weight2
     * @param c3
     * @param a Alpha
     * @return The colorised pixel
     */
	private int colourise(int red, int tint1, int green, int tint2, int blue, int tint3, int alpha) {
		double g = green / 255D;
		double b = blue / 255D;

		g *= 1 - b;

		double r = 1 - (g + b);

		double r1 = scale(getRed(tint1), MINBRIGHTNESS) / 255;
		double g1 = scale(getGreen(tint1), MINBRIGHTNESS) / 255;
		double b1 = scale(getBlue(tint1), MINBRIGHTNESS) / 255;

		double r2 = scale(getRed(tint2), MINBRIGHTNESS) / 255;
		double g2 = scale(getGreen(tint2), MINBRIGHTNESS) / 255;
		double b2 = scale(getBlue(tint2), MINBRIGHTNESS) / 255;

		double r3 = scale(getRed(tint3), MINBRIGHTNESS) / 255;
		double g3 = scale(getGreen(tint3), MINBRIGHTNESS) / 255;
		double b3 = scale(getBlue(tint3), MINBRIGHTNESS) / 255;

		int rfinal = (int) (Math.floor(red * (r1 * r + r2 * g + r3 * b)));
		int gfinal = (int) (Math.floor(red * (g1 * r + g2 * g + g3 * b)));
		int bfinal = (int) (Math.floor(red * (b1 * r + b2 * g + b3 * b)));

		return NativeImage.getCombined(alpha, bfinal, gfinal, rfinal);
	}
	
	private double scale(int color, int min) {
		return min + (int) Math.floor(color * ((255 - min) / 255.0));
	}
}