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
import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;

@OnlyIn(Dist.CLIENT)
public class TextureHelper {

	/**
	 * Generates a texture for the given part using the given tints.
	 * @param uuid The {@link UUID} of the entity wearing the part.
	 * @param part The part.
	 * @param subid The subtype ID.
	 * @param textureID The texture ID.
	 * @param tints An array containing three {@code ints} to use for tinting the texture.
	 * @return A resource location for the generated texture.
	 */
	private static ResourceLocation generateTexture(UUID uuid, Part part, int subid, int textureID, int[] tints) {
		final String[] textures = part.getTextureNames(subid);
		textureID = textureID >= textures.length ? 0 : textureID;
		final String texturePath = "texture/" + part.getType().getId() + "/" + textures[textureID] + ".png";

		// Add UUID to prevent deleting similar textures.
		final ResourceLocation tailTexture = new ResourceLocation("tails",
				part.getId().getNamespace() + "_" + uuid + "_" + part.getId().getPath()
				+ "_" + subid + "_" + textureID + "_" + tints[0] + "_" + tints[1] + "_" + tints[2]);

		Minecraft.getInstance().getTextureManager().register(tailTexture, new TripleTintTexture(part.getId().getNamespace(), texturePath, tints[0], tints[1], tints[2]));

		return tailTexture;
	}

	/**
	 * A convenience method for {@link #generateTexture(UUID, Part, int, int, int[])} using data from the given {@link PartInfo}.
	 * @param uuid The {@link UUID} of the entity wearing the part.
	 * @param partInfo The part data.
	 * @return A resource location for the generated texture.
	 */
	@Nullable
	public static ResourceLocation generateTexture(UUID uuid, PartInfo partInfo) {
		if (partInfo.isEmpty()) return null;
		return generateTexture(uuid, partInfo.getPart(), partInfo.getSubType(), partInfo.getTextureId(), partInfo.getTints());
	}
}