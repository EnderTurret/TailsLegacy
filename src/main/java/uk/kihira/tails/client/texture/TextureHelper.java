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

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

@OnlyIn(Dist.CLIENT)
public class TextureHelper {

	private static final int SWITCH_1_COLOR = 0xFFFF10F0;
	private static final int SWITCH_2_COLOR = 0xFFB8E080;

	private static final Point[] DATA_POINTS = new Point[] {new Point(58,16), new Point(58,17), new Point(58,18)};
	private static final Point[][] SWITCH_POINTS = new Point[][]{
		new Point[]{new Point(56,16), new Point(57,16)},
		new Point[]{new Point(56,17), new Point(57,17)},
		new Point[]{new Point(56,18), new Point(57,18)},
		new Point[]{new Point(56,19), new Point(57,19)} // Not serializing muzzle, just here to prevent a crash
	};
	private static final Point[][] TINT_POINTS = new Point[][] {
		new Point[] {new Point(59,16), new Point(60,16), new Point(61,16)},
		new Point[] {new Point(59,17), new Point(60,17), new Point(61,17)},
		new Point[] {new Point(59,18), new Point(60,18), new Point(61,18)}
	};

	/**
	 * Returns if the player has any PartInfo encoded onto the skin file no matter the type.
	 * @param player The player
	 * @return Has part info(s).
	 */
	public static boolean hasSkinData(AbstractClientPlayerEntity player) {
		final BufferedImage image = getPlayerSkinAsBufferedImage(player);
		if (image != null)
			for (PartType partType : PartType.values()) {
				final int ordinal = partType.ordinal();
				final int scol1 = image.getRGB((int) SWITCH_POINTS[ordinal][0].getX(), (int) SWITCH_POINTS[ordinal][0].getY());
				final int scol2 = image.getRGB((int) SWITCH_POINTS[ordinal][1].getX(), (int) SWITCH_POINTS[ordinal][1].getY());

				if (scol1 == SWITCH_1_COLOR && scol2 == SWITCH_2_COLOR)
					return true;
			}
		return false;
	}

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
				final int ordinal = partType.ordinal();
				final int scol1 = image.getRGB((int) SWITCH_POINTS[ordinal][0].getX(), (int) SWITCH_POINTS[ordinal][0].getY());
				final int scol2 = image.getRGB((int) SWITCH_POINTS[ordinal][1].getX(), (int) SWITCH_POINTS[ordinal][1].getY());

				PartInfo tailInfo;
				if (scol1 == SWITCH_1_COLOR && scol2 == SWITCH_2_COLOR)
					tailInfo = buildPartInfoFromSkin(partType, image, player.getUniqueID());
				else
					tailInfo = PartInfo.none(partType);
				partsData.setPartInfo(partType, tailInfo);
			}

			Tails.PROXY.addPartsData(uuid, partsData);

			// If local player, send our skin info the server.
			if (player == Minecraft.getInstance().player) {
				Tails.setLocalPartsData(partsData, null);
				Tails.CHANNEL.sendToServer(new PlayerDataMessage(UUIDTypeAdapter.fromString(Minecraft.getInstance().getSession().getPlayerID()), partsData));
			}
		}
	}

	public static BufferedImage writePartsDataToSkin(PartsData partsData, AbstractClientPlayerEntity player) {
		final BufferedImage image = getPlayerSkinAsBufferedImage(player);

		// Check we have the players skin.
		if (image != null)
			for (PartType partType : PartType.values()) {
				final PartInfo partInfo = partsData.getPartInfo(partType);
				final int ordinal = partType.ordinal();
				int switch1 = 0x00000000, switch2 = 0x00000000;
				if (partInfo != null) {
					if (!partInfo.isEmpty()) {
						switch1 = SWITCH_1_COLOR;
						switch2 = SWITCH_2_COLOR;
					}

					// Type, subtype and texture
					int dataColour = 0xFF000000;
					dataColour = dataColour | partInfo.getTypeId() << 16;
					dataColour = dataColour | partInfo.getSubType() << 8;
					dataColour = dataColour | partInfo.getTextureId();
					image.setRGB((int) DATA_POINTS[ordinal].getX(), (int) DATA_POINTS[ordinal].getY(), dataColour);
					// Tints
					image.setRGB((int) TINT_POINTS[ordinal][0].getX(), (int) TINT_POINTS[ordinal][0].getY(), partInfo.getTints()[0]);
					image.setRGB((int) TINT_POINTS[ordinal][1].getX(), (int) TINT_POINTS[ordinal][1].getY(), partInfo.getTints()[1]);
					image.setRGB((int) TINT_POINTS[ordinal][2].getX(), (int) TINT_POINTS[ordinal][2].getY(), partInfo.getTints()[2]);
				}
				// Switch colors
				image.setRGB((int) SWITCH_POINTS[ordinal][0].getX(), (int) SWITCH_POINTS[ordinal][0].getY(), switch1);
				image.setRGB((int) SWITCH_POINTS[ordinal][1].getX(), (int) SWITCH_POINTS[ordinal][1].getY(), switch2);
			}
		else Tails.LOGGER.warn("Attempted to write PartInfo to skin but player doesn't have a skin!");

		return image;
	}

	private static PartInfo buildPartInfoFromSkin(PartType partType, BufferedImage skin, UUID uuid) {
		final int ordinal = partType.ordinal();
		final int data = skin.getRGB((int) DATA_POINTS[ordinal].getX(), (int) DATA_POINTS[ordinal].getY());
		final int typeid = data >> 16 & 0xFF;
					final int subtype = data >> 8 & 0xFF;
					int textureid = data & 0xFF;
					final String[] textures = PartRegistry.getPartRenderer(partType, typeid).getTextureNames(subtype);

					textureid = textureid >= textures.length ? 0 : textureid;

					final int tint1 = skin.getRGB((int) TINT_POINTS[ordinal][0].getX(), (int) TINT_POINTS[ordinal][0].getY());
					final int tint2 = skin.getRGB((int) TINT_POINTS[ordinal][1].getX(), (int) TINT_POINTS[ordinal][1].getY());
					final int tint3 = skin.getRGB((int) TINT_POINTS[ordinal][2].getX(), (int) TINT_POINTS[ordinal][2].getY());

					final ResourceLocation tailTexture = generateTexture(uuid, partType, typeid, subtype, textureid, new int[] {tint1, tint2, tint3});

					return new PartInfo(typeid, subtype, 0, tint1, tint2, tint3, partType, tailTexture);
	}

	/**
	 * Generates a texture for the given part using the given tints.
	 * @param uuid The {@link UUID} of the entity wearing the part.
	 * @param partType The part type.
	 * @param typeid The type ID.
	 * @param subid The subtype ID.
	 * @param textureID The texture ID.
	 * @param tints An array containing three {@code ints} to use for tinting the texture.
	 * @return A resource location for the generated texture.
	 */
	private static ResourceLocation generateTexture(UUID uuid, PartType partType, int typeid, int subid, int textureID, int[] tints) {
		final String[] textures = PartRegistry.getPartRenderer(partType, typeid).getTextureNames(subid);
		textureID = textureID >= textures.length ? 0 : textureID;
		final String texturePath = "texture/" + partType.getId() + "/" + textures[textureID] + ".png";

		// Add UUID to prevent deleting similar textures.
		final ResourceLocation tailTexture = new ResourceLocation("tails_" + uuid + "_" + partType.getId() + "_" + typeid + "_" + subid + "_" + textureID + "_" + tints[0] + "_" + tints[1] + "_" + tints[2]);
		Minecraft.getInstance().getTextureManager().loadTexture(tailTexture, new TripleTintTexture("tails", texturePath, tints[0], tints[1], tints[2]));

		return tailTexture;
	}

	/**
	 * A convenience method for {@link #generateTexture(UUID, PartType, int, int, int, int[])} using data from the given {@link PartInfo}.
	 * @param uuid The {@link UUID} of the entity wearing the part.
	 * @param partInfo The part data.
	 * @return A resource location for the generated texture.
	 */
	public static ResourceLocation generateTexture(UUID uuid, PartInfo partInfo) {
		return generateTexture(uuid, partInfo.getPartType(), partInfo.getTypeId(), partInfo.getSubType(), partInfo.getTextureId(), partInfo.getTints());
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