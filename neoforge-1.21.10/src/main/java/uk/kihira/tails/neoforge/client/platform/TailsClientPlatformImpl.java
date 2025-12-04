/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.platform;

import java.util.EnumSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.server.packs.resources.ResourceManager;

import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.UsernameCache;

import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.api.PartRendererRegistrar;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.TailsCubeDefinition;
import uk.kihira.tails.common.client.model.TailsPartDefinition;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.common.client.part.PartRegistry;
import uk.kihira.tails.neoforge.client.ClientLibraryManager;
import uk.kihira.tails.neoforge.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.neoforge.client.texture.TripleTintTexture;
import uk.kihira.tails.neoforge.common.TailsConfig;
import uk.kihira.tails.neoforge.common.network.C2SPlayerDataMessage;
import uk.kihira.tails.neoforge.common.platform.ResourceManagerWrapperImpl;
import uk.kihira.tails.neoforge.mixin.client.CubeDefinitionAccess;
import uk.kihira.tails.neoforge.mixin.client.PartDefinitionAccess;

public final class TailsClientPlatformImpl implements TailsClientPlatform {

	private final LibraryManager libraryManager = new ClientLibraryManager();

	@Override
	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight) {
		return (TailsModelPart) (Object) makePartDefinition(part).bake(textureWidth, textureHeight);
	}

	private static PartDefinition makePartDefinition(TailsPartDefinition part) {
		final PartDefinition ret = PartDefinitionAccess.tails$new(
				part.cubes.stream().map(TailsClientPlatformImpl::makeCubeDefinition).toList(),
				PartPose.offsetAndRotation(part.xOffset, part.yOffset, part.zOffset, part.xRot, part.yRot, part.zRot));

		final PartDefinitionAccess access = (PartDefinitionAccess) ret;

		// Recursion :concern:
		for (var entry : part.children.entrySet())
			access.tails$children().put(entry.getKey(), makePartDefinition(entry.getValue()));

		return ret;
	}

	private static CubeDefinition makeCubeDefinition(TailsCubeDefinition cube) {
		return CubeDefinitionAccess.tails$new(null,
				cube.u, cube.v,
				cube.x, cube.y, cube.z,
				cube.sizeX, cube.sizeY, cube.sizeZ,
				new CubeDeformation(cube.growX, cube.growY, cube.growZ),
				cube.mirror, 1, 1, cube.visibleFaces.stream()
				.map(dir -> Direction.values()[dir.ordinal()])
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class))));
	}

	@Override
	public boolean hasTexture(TResourceLocation id) {
		return Minecraft.getInstance().getTextureManager()
				.getTexture((ResourceLocation) (Object) id) instanceof TripleTintTexture;
	}

	@Override
	public void registerTripleTintTexture(TResourceLocation id, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints) {
		Minecraft.getInstance().getTextureManager().register((ResourceLocation) (Object) id, new TripleTintTexture(
				(ResourceLocation) (Object) part.getId().t$withPath(texture.path()),
				tints[0], tints[1], tints[2], texture.tintingStrategy()
				));
	}

	@Override
	public void releaseTexture(TResourceLocation id) {
		try {
			Minecraft.getInstance().getTextureManager().release((ResourceLocation) (Object) id);
		} catch (Exception ignored) {}
	}

	public static void reloadParts(ResourceManager manager) {
		PartRegistry.MANAGER.reload(new ResourceManagerWrapperImpl(manager));
	}

	@Override
	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar) {
		ModLoader.postEvent(new RegisterPartRenderersEvent(registrar));
	}

	private static Services services;

	@Override
	public String fetchUsername(UUID uuid) {
		final Minecraft mc = Minecraft.getInstance();

		// So, there are a few different places we can try first...

		// Option A - Forge's "username cache"
		String username = UsernameCache.getLastKnownUsername(uuid);
		if (username != null) return username;

		// Option B - "Just query it lol"
		username = mc.services().profileResolver().fetchById(uuid).map(GameProfile::name).orElse(null);
		if (username != null) return username;

		// Option C - Just use the UUID
		return uuid.toString();
	}

	@Override
	public UUID getLocalUUID() {
		final Minecraft mc = Minecraft.getInstance();
		/*
		if (mc.player != null && mc.player.getUniqueID() != null)
			return mc.player.getUniqueID();
		*/
		return mc.player != null ? mc.player.getUUID() : mc.getUser().getProfileId();
	}

	@Override
	public String getConfigParts() {
		return TailsConfig.CLIENT_INSTANCE.localPlayerData.get();
	}

	@Override
	public void setConfigParts(String json) {
		TailsConfig.CLIENT_INSTANCE.localPlayerData.set(json);
		TailsConfig.getConfig().save();
	}

	@Override
	public void syncLocalToServer(ClientPartsData partsData) {
		if (Minecraft.getInstance().level != null)
			ClientPacketDistributor.sendToServer(new C2SPlayerDataMessage(partsData));
	}

	@Override
	public LibraryManager getLibraryManager() {
		return libraryManager;
	}
}