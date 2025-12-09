/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import uk.kihira.tails.common.client.TripleTintTextureHelper;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.forge.client.platform.TailsBufferedImageWrapper;
import uk.kihira.tails.forge.common.Tails;

/**
 * A texture that tints another texture based on three tint values.
 */
@Internal
public final class TripleTintTexture extends AbstractTexture {

	private final ResourceLocation textureLocation;
	private final int tint1;
	private final int tint2;
	private final int tint3;
	private final Part.TintingStrategy strategy;

	@Internal
	public TripleTintTexture(ResourceLocation textureLocation, int tint1, int tint2, int tint3, Part.TintingStrategy strategy) {
		this.textureLocation = textureLocation;
		this.tint1 = tint1;
		this.tint2 = tint2;
		this.tint3 = tint3;
		this.strategy = strategy;
	}

	@Override
	public void loadTexture(IResourceManager manager) throws IOException {
		deleteGlTexture();

		final BufferedImage texture;
		try (IResource resource = manager.getResource(textureLocation); InputStream is = resource.getInputStream()) {
			texture = TextureUtil.readBufferedImage(is);
		} catch (IOException e) {
			Tails.LOGGER.error("Using missing texture: failed to load {}.", textureLocation, e);
			prepareAndUpload(null);
			return;
		}

		TripleTintTextureHelper.colorise(new TailsBufferedImageWrapper(texture), strategy, tint1, tint2, tint3);
		prepareAndUpload(texture);
	}

	private void prepareAndUpload(@Nullable BufferedImage texture) {
		if (texture == null) {
			texture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			texture.setRGB(0, 0, texture.getWidth(), texture.getHeight(), TextureUtil.MISSING_TEXTURE_DATA, 0, texture.getWidth());
		}

		TextureUtil.uploadTextureImage(getGlTextureId(), texture);
	}
}