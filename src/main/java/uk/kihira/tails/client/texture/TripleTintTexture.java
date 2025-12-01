/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.blaze3d.platform.TextureUtil;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common2.client.TripleTintTextureHelper;
import uk.kihira.tails.common2.client.part.Part;

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
	public void load(ResourceManager manager) throws IOException {
		releaseId();

		final Optional<Resource> optional = manager.getResource(textureLocation);

		if (!optional.isPresent()) {
			Tails.LOGGER.error("Using missing texture: unable to find {}.", textureLocation);
			prepareAndUpload(null);
			return;
		}

		final NativeImage texture;
		try (InputStream is = optional.get().open()) {
			texture = NativeImage.read(Format.RGBA, is);
		} catch (IOException e) {
			Tails.LOGGER.error("Using missing texture: failed to load {}.", textureLocation, e);
			prepareAndUpload(null);
			return;
		}

		TripleTintTextureHelper.colorise(new TailsNativeImageWrapper(texture), strategy, tint1, tint2, tint3);
		prepareAndUpload(texture);
	}

	private void prepareAndUpload(@Nullable NativeImage texture) {
		if (texture == null) texture = clone(MissingTextureAtlasSprite.getTexture().getPixels());
		TextureUtil.prepareImage(getId(), texture.getWidth(), texture.getHeight());
		texture.upload(0, 0, 0, true);
	}

	private static NativeImage clone(NativeImage src) {
		final NativeImage ret = new NativeImage(src.format(), src.getWidth(), src.getHeight(), false);
		ret.copyFrom(src);
		return ret;
	}
}