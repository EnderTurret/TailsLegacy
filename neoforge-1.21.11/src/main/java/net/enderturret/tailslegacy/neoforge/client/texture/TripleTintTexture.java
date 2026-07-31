/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.client.texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import net.enderturret.tailslegacy.common.client.TripleTintTextureHelper;
import net.enderturret.tailslegacy.common.client.part.TintingStrategy;
import net.enderturret.tailslegacy.neoforge.client.platform.TailsNativeImageWrapper;
import net.enderturret.tailslegacy.neoforge.common.TailsLegacy;

/**
 * A texture that tints another texture based on three tint values.
 */
@Internal
public final class TripleTintTexture extends ReloadableTexture {

	private final int tint1;
	private final int tint2;
	private final int tint3;
	private final TintingStrategy strategy;

	@Internal
	public TripleTintTexture(Identifier textureLocation, int tint1, int tint2, int tint3, TintingStrategy strategy) {
		super(textureLocation);
		this.tint1 = tint1;
		this.tint2 = tint2;
		this.tint3 = tint3;
		this.strategy = strategy;
	}

	@Override
	public TextureContents loadContents(ResourceManager manager) throws IOException {
		final Optional<Resource> optional = manager.getResource(resourceId());

		if (!optional.isPresent()) {
			TailsLegacy.LOGGER.error("Using missing texture: unable to find {}.", resourceId());
			return prepareAndUpload(null);
		}

		final NativeImage texture;
		try (InputStream is = optional.get().open()) {
			texture = NativeImage.read(Format.RGBA, is);
		} catch (IOException e) {
			TailsLegacy.LOGGER.error("Using missing texture: failed to load {}.", resourceId(), e);
			return prepareAndUpload(null);
		}

		TripleTintTextureHelper.colorise(new TailsNativeImageWrapper(texture), strategy, tint1, tint2, tint3);
		return prepareAndUpload(texture);
	}

	private TextureContents prepareAndUpload(@Nullable NativeImage texture) {
		if (texture == null) texture = MissingTextureAtlasSprite.generateMissingImage();
		return new TextureContents(texture, null);
	}

	private static NativeImage clone(NativeImage src) {
		final NativeImage ret = new NativeImage(src.format(), src.getWidth(), src.getHeight(), false);
		ret.copyFrom(src);
		return ret;
	}
}