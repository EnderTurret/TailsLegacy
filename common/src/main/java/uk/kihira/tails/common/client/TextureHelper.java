/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client;

import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common.JavaColor;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.Part;

/**
 * Manages generation of {code TripleTintTextures} and also provides some texture-related utilities.
 */
@Internal
public final class TextureHelper {

	/**
	 * Generates a texture for the given part using the given tints.
	 * @param uuid The {@link UUID} of the entity wearing the part.
	 * @param part The part.
	 * @param subType The sub type.
	 * @param texture The texture.
	 * @param tints An array containing three {@code ints} to use for tinting the texture.
	 * @return A resource location for the generated texture.
	 */
	private static TResourceLocation generateTexture(UUID uuid, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints) {
		// Add UUID to prevent deleting similar textures.
		final TResourceLocation textureId = TailsPlatform.get().newResourceLocation(
				String.format("%s__%s_%s__%s__%s__%s_%s_%s", uuid, part.getId().t$getNamespace(),
						part.getId().t$getPath(), subType.id(), texture.id(),
						JavaColor.hex(tints[0], true), JavaColor.hex(tints[1], true), JavaColor.hex(tints[2], true)));

		if (TailsClientPlatform.get().hasTexture(textureId))
			return textureId;

		TailsClientPlatform.get().registerTripleTintTexture(textureId, part, subType, texture, tints);

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
	public static TResourceLocation generateTexture(UUID uuid, ClientPartInfo partInfo) {
		if (partInfo.isEmpty() || partInfo.isInvalid()) return null;
		return generateTexture(uuid, partInfo.getPart(), partInfo.getSubType(), partInfo.getPartTexture(), partInfo.getTints());
	}

	/**
	 * Releases the texture specified by the given id.
	 * @param id The id of the texture to release.
	 */
	@Internal
	public static void release(TResourceLocation id) {
		TailsClientPlatform.get().releaseTexture(id);
	}
}