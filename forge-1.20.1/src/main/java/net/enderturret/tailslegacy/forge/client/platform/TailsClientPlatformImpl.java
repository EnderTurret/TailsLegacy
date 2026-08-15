/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.platform;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;

import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import net.enderturret.tailslegacy.common.LibraryManager;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.api.PartRendererRegistrar;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.model.TailsCubeDefinition;
import net.enderturret.tailslegacy.common.client.model.TailsPartDefinition;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.part.Part;
import net.enderturret.tailslegacy.common.client.part.PartRegistry;
import net.enderturret.tailslegacy.common.client.part.PartTexture;
import net.enderturret.tailslegacy.common.client.part.SubType;
import net.enderturret.tailslegacy.forge.client.ClientLibraryManager;
import net.enderturret.tailslegacy.forge.client.api.RegisterPartRenderersEvent;
import net.enderturret.tailslegacy.forge.client.texture.TripleTintTexture;
import net.enderturret.tailslegacy.forge.common.TailsLegacy;
import net.enderturret.tailslegacy.forge.common.TailsConfig;
import net.enderturret.tailslegacy.forge.common.network.C2SPlayerDataMessage;
import net.enderturret.tailslegacy.forge.common.network.TailsNetworkManager;
import net.enderturret.tailslegacy.forge.common.platform.ResourceManagerWrapperImpl;
import net.enderturret.tailslegacy.forge.mixin.client.CubeDefinitionAccess;
import net.enderturret.tailslegacy.forge.mixin.client.MinecraftAccess;
import net.enderturret.tailslegacy.forge.mixin.client.PartDefinitionAccess;

public final class TailsClientPlatformImpl implements TailsClientPlatform {

	private final LibraryManager libraryManager = new ClientLibraryManager();

	@Override
	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight) {
		return (TailsModelPart) (Object) makePartDefinition(part).bake(textureWidth, textureHeight);
	}

	private static PartDefinition makePartDefinition(TailsPartDefinition part) {
		final PartDefinition ret = PartDefinitionAccess.tailslegacy$new(
				part.cubes.stream().map(TailsClientPlatformImpl::makeCubeDefinition).toList(),
				PartPose.offsetAndRotation(part.xOffset, part.yOffset, part.zOffset, part.xRot, part.yRot, part.zRot));

		final PartDefinitionAccess access = (PartDefinitionAccess) ret;

		// Recursion :concern:
		for (var entry : part.children.entrySet())
			access.tailslegacy$children().put(entry.getKey(), makePartDefinition(entry.getValue()));

		return ret;
	}

	private static CubeDefinition makeCubeDefinition(TailsCubeDefinition cube) {
		return CubeDefinitionAccess.tailslegacy$new(null,
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
				.getTexture((ResourceLocation) id, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture();
	}

	@Override
	public void registerTripleTintTexture(TResourceLocation id, Part part, SubType subType, PartTexture texture, int[] tints) {
		Minecraft.getInstance().getTextureManager().register((ResourceLocation) id, new TripleTintTexture(
				(ResourceLocation) part.getId().t$withPath(texture.path()),
				tints[0], tints[1], tints[2], texture.tintingStrategy()
				));
	}

	@Override
	public void releaseTexture(TResourceLocation id) {
		try {
			Minecraft.getInstance().getTextureManager().release((ResourceLocation) id);
		} catch (Exception ignored) {}
	}

	public static void reloadParts(ResourceManager manager) {
		PartRegistry.MANAGER.reload(new ResourceManagerWrapperImpl(manager));
	}

	@Override
	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar) {
		ModLoader.get().postEvent(new RegisterPartRenderersEvent(registrar));
	}

	private static Services services;

	@Override
	public String fetchUsername(UUID uuid) {
		final Minecraft mc = Minecraft.getInstance();

		// So, there are a few different places we can try first...

		// Option A - Forge's "username cache"
		String username = UsernameCache.getLastKnownUsername(uuid);
		if (username != null) return username;

		// Option B - The user cache (some assembly required)
		if (services == null && mc instanceof MinecraftAccess access) {
			services = Services.create(access.tailslegacy$authenticationService(), mc.gameDirectory);
			services.profileCache().setExecutor(mc);
			GameProfileCache.setUsesAuthentication(false);
		}

		if (services != null) {
			username = services.profileCache().get(uuid).map(GameProfile::getName).orElse(null);
			if (username != null) return username;
		}

		// Option C - "Just query it lol"
		GameProfile profile = new GameProfile(uuid, null);
		profile = mc.getMinecraftSessionService().fillProfileProperties(profile, false);
		username = profile.getName();

		// Surprisingly, we actually got a username. Let's inform the caches, shall we?
		if (username != null) {
			// Unfortunately, it looks like Forge's username cache is and I quote "too good for manipulation."
			// So instead we are only able to let our little profile cache know.
			if (services != null)
				services.profileCache().add(profile);

			return username;
		}

		// Option D - Just use the UUID
		return uuid.toString();
	}

	@Override
	public UUID getLocalUUID() {
		final Minecraft mc = Minecraft.getInstance();
		return mc.player != null ? mc.player.getUUID() : UUIDUtil.getOrCreatePlayerUUID(mc.getUser().getGameProfile());
	}

	@Override
	public String getConfigParts() {
		String ret = TailsConfig.CLIENT_INSTANCE.localPlayerData.get();
		if (ret.isBlank() && TailsLegacy.migratingData != null) {
			ret = TailsLegacy.migratingData;
			TailsPlatform.get().logInfo("Found old customization data, migrating!\n{}", ret);
			setConfigParts(ret);
		}
		return ret;
	}

	@Override
	public void setConfigParts(String json) {
		TailsConfig.CLIENT_INSTANCE.localPlayerData.set(json);
		TailsConfig.getConfig().save();
	}

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public void syncLocalToServer(ClientPartsData partsData) {
		if (Minecraft.getInstance().level != null)
			TailsNetworkManager.get().sendToServer(new C2SPlayerDataMessage(partsData));
	}

	@Override
	public LibraryManager getLibraryManager() {
		return libraryManager;
	}
}