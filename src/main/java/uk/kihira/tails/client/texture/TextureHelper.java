/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.texture;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartRegistry;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

@OnlyIn(Dist.CLIENT)
public class TextureHelper {

	@SuppressWarnings("rawtypes")
	public static void buildPlayerPartsData(AbstractClientPlayerEntity player) {
		final GameProfile profile = player.getGameProfile();
		final UUID uuid = profile.getId();
		final BufferedImage image = getPlayerSkinAsBufferedImage(player);
		if (image != null) {
			PartsData partsData = Tails.PROXY.getPartsData(uuid);
			if (partsData == null)
				partsData = new PartsData();

			// Load part data from skin.
			for (PartType partType : PartType.values()) {
				partsData.setPartInfo(partType, PartInfo.none());
			}

			Tails.PROXY.addPartsData(uuid, partsData);

			// If local player, send our skin info the server.
			if (player == Minecraft.getInstance().player) {
				Tails.setLocalPartsData(partsData, null);
				Tails.CHANNEL.sendToServer(new PlayerDataMessage(UUIDTypeAdapter.fromString(Minecraft.getInstance().getSession().getPlayerID()), partsData));
			}
		}
	}

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
		final ResourceLocation tailTexture = new ResourceLocation("tails", part.getId().getNamespace() + "_" + uuid + "_" + part.getId().getPath() + "_" + subid + "_" + textureID + "_" + tints[0] + "_" + tints[1] + "_" + tints[2]);
		Minecraft.getInstance().getTextureManager().loadTexture(tailTexture, new TripleTintTexture(part.getId().getNamespace(), texturePath, tints[0], tints[1], tints[2]));

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

	public static boolean needsBuild(PlayerEntity player) {
		return !Tails.PROXY.hasPartsData(player.getUniqueID()) && player.getGameProfile().getProperties().containsKey("textures");
	}

	private static BufferedImage getPlayerSkinAsBufferedImage(AbstractClientPlayerEntity player) {
		return null;
		/*BufferedImage bufferedImage = null;
		InputStream inputStream = null;
		final Minecraft mc = Minecraft.getInstance();
		final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = mc.getSkinManager().loadSkinFromCache(player.getGameProfile());
		Texture skintex;
		final String playerName = player.getGameProfile().getName();

		try {
			if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
				skintex = mc.getTextureManager().getTexture(mc.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN));
			else
				skintex = mc.getTextureManager().getTexture(player.getLocationSkin());

			if (skintex instanceof DownloadingTexture) {
				final DownloadingTexture imagedata = (DownloadingTexture) skintex;
				Tails.LOGGER.debug("Loading " + playerName + " skin");

				//bufferedImage = ObfuscationReflectionHelper.getPrivateValue(DownloadingTexture.class, imagedata, "field_110560_d", "bufferedImage");
			}
			else if (skintex instanceof DynamicTexture) {
				Tails.LOGGER.warn(playerName+" skin is a DynamicTexture! Attempting to load anyway");
				final DynamicTexture imagedata = (DynamicTexture) skintex;
				final int width = imagedata.getTextureData().getWidth();
				final int height = imagedata.getTextureData().getHeight();
				bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				bufferedImage.setRGB(0, 0, width, height, imagedata.getTextureData().makePixelArray(), 0, width);
			}
			else {
				Tails.LOGGER.warn("Could not fetch "+playerName+" skin, loading default skin");
				inputStream = Minecraft.getInstance().getResourceManager().getResource(DefaultPlayerSkin.getDefaultSkinLegacy()).getInputStream();
				bufferedImage = ImageIO.read(inputStream);
			}
		}
		catch (IOException e) {
			Tails.LOGGER.error("Failed to read "+playerName+" skin texture", e);
		}
		finally {
			IOUtils.closeQuietly(inputStream);
		}
		return bufferedImage;*/
	}

	private static void uploadTexture(Texture textureObject, BufferedImage bufferedImage) {
		//TextureUtil.uploadTextureImage(textureObject.getGlTextureId(), bufferedImage);
	}
}