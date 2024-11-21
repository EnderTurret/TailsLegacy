/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.texture;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import uk.kihira.tails.client.ColorUtil;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.ClientPlayerPartManager;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.common.Tails;

/**
 * Manages generation of {@link TripleTintTexture TripleTintTextures} and also provides some texture-related utilities.
 */
@Internal
public final class TextureHelper {

	/**
	 * Whether texture leak debugging is enabled.
	 */
	@Internal
	public static final boolean DEBUG_TEXTURE_LEAKS = Boolean.getBoolean("tails.debugTextureLeaks");

	/**
	 * Whether stack traces should be included in texture leak debugging.
	 */
	private static final boolean DEBUG_TEXTURE_LEAKS_STACKTRACE = Boolean.getBoolean("tails.debugTextureLeaks.stacktrace");

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
		final ResourceLocation textureId = ResourceLocation.fromNamespaceAndPath("tails",
				"%s__%s_%s__%s__%s__%s_%s_%s".formatted(uuid, part.getId().getNamespace(),
						part.getId().getPath(), subType.id(), texture.id(),
						ColorUtil.hex(tints[0], true, true), ColorUtil.hex(tints[1], true, true), ColorUtil.hex(tints[2], true, true)));

		if (Minecraft.getInstance().getTextureManager().getTexture(textureId, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture()) {
			if (DEBUG_TEXTURE_LEAKS)
				Tails.LOGGER.info("* Skipped   {}.", textureId);
			return textureId;
		}

		Minecraft.getInstance().getTextureManager().register(textureId,
				new TripleTintTexture(part.getId().getNamespace(), texture.path(),
						tints[0], tints[1], tints[2], texture.tintingStrategy()));

		if (DEBUG_TEXTURE_LEAKS) {
			Tails.LOGGER.info("+ Generated {}.", textureId);
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

	/**
	 * Releases the texture specified by the given id.
	 * @param id The id of the texture to release.
	 */
	@Internal
	public static void release(ResourceLocation id) {
		try {
			if (DEBUG_TEXTURE_LEAKS) {
				Tails.LOGGER.info("- Released  {}.{}", id, DEBUG_TEXTURE_LEAKS_STACKTRACE ? "\n" + walk(1) : "");
				TRACKED.remove(id);
			}
			Minecraft.getInstance().getTextureManager().release(id);
		} catch (Exception ignored) {}
	}

	private static String walk(int depth) {
		final StackWalker sw = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		return sw.walk(stream -> stream
				.skip(depth + 1)
				.filter(sf -> sf.getClassName().startsWith("uk.kihira.tails"))
				.limit(6)
				.map(sf -> "\tat " + sf.getDeclaringClass().getName() + "." + sf.getMethodName() + "("
						+ (sf.getFileName() == null ? "Unknown Source" : sf.getFileName() + ":" + sf.getLineNumber())
						+ ")")
				.collect(Collectors.joining("\n")));
	}

	/**
	 * If enabled, logs all of the tracked textures.
	 */
	@Internal
	public static void logLeaks() {
		if (!DEBUG_TEXTURE_LEAKS) return;

		final Set<ResourceLocation> inUse = ClientPlayerPartManager.get().getData().values().stream()
				.flatMap(pd -> pd.getPartInfos().stream())
				.filter(pi -> pi instanceof ClientPartInfo)
				.map(pi -> ((ClientPartInfo) pi).getTexture())
				.collect(Collectors.toSet());

		Tails.LOGGER.info("Tracked textures:\n\t{}", TRACKED.stream()
				.map(tex -> tex.toString() + (inUse.contains(tex) ? " (in use)" : " (leak?)"))
				.collect(Collectors.joining("\n\t")));
	}

	/**
	 * Copies a region of pixels from the given {@link NativeImage} into the given buffer.
	 * @param src The source image.
	 * @param dest The destination buffer.
	 * @param fromX The coordinate corresponding to the left side of the region.
	 * @param fromY The coordinate corresponding to the top side of the region.
	 * @param width The width of the region.
	 * @param height The height of the region.
	 */
	public static void copyPixels(NativeImage src, ByteBuffer dest, int fromX, int fromY, int width, int height) {
		for (int y = fromY; y < fromY + height; y++)
			for (int x = fromX; x < fromX + width; x++) {
				final int pixel = src.getPixelRGBA(x, y);
				dest.put((byte) FastColor.ABGR32.red(pixel));
				dest.put((byte) FastColor.ABGR32.green(pixel));
				dest.put((byte) FastColor.ABGR32.blue(pixel));
				dest.put((byte) FastColor.ABGR32.alpha(pixel));
			}
	}
}