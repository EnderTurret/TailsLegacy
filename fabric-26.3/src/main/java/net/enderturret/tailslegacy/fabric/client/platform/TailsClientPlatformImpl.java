/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.platform;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mojang.authlib.GameProfile;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

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
import net.enderturret.tailslegacy.fabric.client.ClientLibraryManager;
import net.enderturret.tailslegacy.fabric.client.TailsLegacyClient;
import net.enderturret.tailslegacy.fabric.client.api.RegisterPartRenderersEvent;
import net.enderturret.tailslegacy.fabric.client.texture.TripleTintTexture;
import net.enderturret.tailslegacy.fabric.common.TailsConfig;
import net.enderturret.tailslegacy.fabric.common.network.C2SPlayerDataMessage;
import net.enderturret.tailslegacy.fabric.common.platform.ResourceManagerWrapperImpl;
import net.enderturret.tailslegacy.fabric.mixin.client.CubeDefinitionAccess;
import net.enderturret.tailslegacy.fabric.mixin.client.PartDefinitionAccess;
import net.enderturret.tailslegacy.fabric.mixin.client.TextureManagerAccess;

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

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean hasTexture(TResourceLocation id) {
		final TextureManagerAccess access = (TextureManagerAccess) Minecraft.getInstance().getTextureManager();
		return access.tailslegacy$byPath().containsKey(id);
	}

	@Override
	public void registerTripleTintTexture(TResourceLocation id, Part part, SubType subType, PartTexture texture, int[] tints) {
		Minecraft.getInstance().getTextureManager().registerAndLoad((Identifier) (Object) id, new TripleTintTexture(
				(Identifier) (Object) part.getId().t$withPath(texture.path()),
				tints[0], tints[1], tints[2], texture.tintingStrategy()
				));
	}

	@Override
	public void releaseTexture(TResourceLocation id) {
		try {
			Minecraft.getInstance().getTextureManager().release((Identifier) (Object) id);
		} catch (Exception ignored) {}
	}

	public static void reloadParts(ResourceManager manager) {
		PartRegistry.MANAGER.reload(new ResourceManagerWrapperImpl(manager));
	}

	@Override
	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar) {
		RegisterPartRenderersEvent.REGISTER_PART_RENDERERS.invoker().register(registrar);
	}

	@Override
	public String fetchUsername(UUID uuid) {
		final Minecraft mc = Minecraft.getInstance();

		// Option A - "Just query it lol"
		String username = mc.services().profileResolver().fetchById(uuid).map(GameProfile::name).orElse(null);
		if (username != null) return username;

		// Option B - Just use the UUID
		return uuid.toString();
	}

	@Override
	public UUID getLocalUUID() {
		final Minecraft mc = Minecraft.getInstance();
		return mc.player != null ? mc.player.getUUID() : mc.getUser().getProfileId();
	}

	@Override
	public String getConfigParts() {
		String ret = TailsConfig.CLIENT_INSTANCE.localPlayerData;
		if (ret.isBlank() && TailsLegacyClient.migratingData != null) {
			ret = TailsLegacyClient.migratingData;
			TailsPlatform.get().logInfo("Found old customization data, migrating!\n{}", ret);
			setConfigParts(ret);
		}
		return ret;
	}

	@Override
	public void setConfigParts(String json) {
		TailsConfig.CLIENT_INSTANCE.localPlayerData = json;
		TailsConfig.CLIENT_INSTANCE.save();
	}

	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public void syncLocalToServer(ClientPartsData partsData) {
		if (Minecraft.getInstance().level != null && ClientPlayNetworking.canSend(C2SPlayerDataMessage.TYPE))
			ClientPlayNetworking.send(new C2SPlayerDataMessage(partsData));
	}

	@Override
	public LibraryManager getLibraryManager() {
		return libraryManager;
	}
}