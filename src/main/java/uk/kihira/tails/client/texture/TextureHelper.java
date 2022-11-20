/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.texture;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import uk.kihira.tails.client.ColorUtil;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.common.Tails;

@Internal
@OnlyIn(Dist.CLIENT)
public final class TextureHelper {

	@Internal
	public static final boolean DEBUG_TEXTURE_LEAKS = Boolean.getBoolean("tails.debugTextureLeaks");

	private static final Set<ResourceLocation> TRACKED;

	static {
		TRACKED = DEBUG_TEXTURE_LEAKS ? new TreeSet<>() : null;
	}

	/**
	 * Generates a texture for the given part using the given tints.
	 * @param uuid The {@link UUID} of the entity wearing the part.
	 * @param part The part.
	 * @param subType The sub type.
	 * @param texture The texture.
	 * @param tints An array containing three {@code ints} to use for tinting the texture.
	 * @return A resource location for the generated texture.
	 */
	private static ResourceLocation generateTexture(UUID uuid, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints) {
		// Add UUID to prevent deleting similar textures.
		final ResourceLocation textureId = new ResourceLocation("tails",
				"%s__%s_%s__%s__%s__%s_%s_%s".formatted(uuid, part.getId().getNamespace(),
						part.getId().getPath(), subType.id(), texture.id(),
						ColorUtil.hex(tints[0], true, true), ColorUtil.hex(tints[1], true, true), ColorUtil.hex(tints[2], true, true)));

		if (Minecraft.getInstance().getTextureManager().getTexture(textureId, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture()) {
			if (DEBUG_TEXTURE_LEAKS)
				Tails.LOGGER.info("Skipped   {}.", textureId);
			return textureId;
		}

		Minecraft.getInstance().getTextureManager().register(textureId,
				new TripleTintTexture(part.getId().getNamespace(), texture.path(),
						tints[0], tints[1], tints[2], texture.tintingStrategy()));

		if (DEBUG_TEXTURE_LEAKS) {
			Tails.LOGGER.info("Generated {}.", textureId);
			TRACKED.add(textureId);
		}

		return textureId;
	}

	/**
	 * A convenience method for {@link #generateTexture(UUID, Part, Part.SubType, Part.PartTexture, int[])} using data from the given {@link ClientPartInfo}.
	 * @param uuid The {@link UUID} of the entity wearing the part.
	 * @param partInfo The part data.
	 * @return A resource location for the generated texture.
	 */
	@Internal
	@Nullable
	public static ResourceLocation generateTexture(UUID uuid, ClientPartInfo partInfo) {
		if (partInfo.isEmpty() || partInfo.isInvalid()) return null;
		return generateTexture(uuid, partInfo.getPart(), partInfo.getSubType(), partInfo.getPartTexture(), partInfo.getTints());
	}

	@Internal
	public static void release(ResourceLocation id) {
		try {
			if (DEBUG_TEXTURE_LEAKS) {
				Tails.LOGGER.info("Released  {}.", id);
				TRACKED.remove(id);
			}
			Minecraft.getInstance().getTextureManager().release(id);
		} catch (Exception ignored) {}
	}

	public static void logLeaks() {
		if (!DEBUG_TEXTURE_LEAKS) return;

		final Set<ResourceLocation> inUse = Tails.PROXY.getPartManager().getData().values().stream()
				.flatMap(pd -> pd.getPartInfos().stream())
				.filter(pi -> pi instanceof ClientPartInfo)
				.map(pi -> ((ClientPartInfo) pi).getTexture())
				.collect(Collectors.toSet());

		Tails.LOGGER.info("Tracked textures:\n\t{}", TRACKED.stream()
				.map(tex -> tex.toString() + (inUse.contains(tex) ? " (in use)" : " (leak?)"))
				.collect(Collectors.joining("\n\t")));
	}
}