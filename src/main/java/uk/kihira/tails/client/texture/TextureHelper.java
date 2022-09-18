/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.texture;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;

@OnlyIn(Dist.CLIENT)
public class TextureHelper {

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
				"%s_%s_%s_%s_%s_%s_%s_%s".formatted(part.getId().getNamespace(), uuid, part.getId().getPath(),
						subType.id(), texture.id(), tints[0], tints[1], tints[2]));

		Minecraft.getInstance().getTextureManager().register(textureId,
				new TripleTintTexture(part.getId().getNamespace(), texture.path(), tints[0], tints[1], tints[2]));

		return textureId;
	}

	/**
	 * A convenience method for {@link #generateTexture(UUID, Part, Part.SubType, Part.PartTexture, int[])} using data from the given {@link ClientPartInfo}.
	 * @param uuid The {@link UUID} of the entity wearing the part.
	 * @param partInfo The part data.
	 * @return A resource location for the generated texture.
	 */
	@Nullable
	public static ResourceLocation generateTexture(UUID uuid, ClientPartInfo partInfo) {
		if (partInfo.isEmpty()) return null;
		return generateTexture(uuid, partInfo.getPart(), partInfo.getSubType(), partInfo.getPartTexture(), partInfo.getTints());
	}
}