/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.platform;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.TailsDirection;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.api.PartRendererRegistrar;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.model.TailsCubeDefinition;
import uk.kihira.tails.common.client.model.TailsPartDefinition;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.common.client.part.PartRegistry;
import uk.kihira.tails.forge.client.ClientLibraryManager;
import uk.kihira.tails.forge.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.forge.client.render.ModelPartCubeExtensions;
import uk.kihira.tails.forge.client.render.ModelPartExtensions;
import uk.kihira.tails.forge.client.texture.TripleTintTexture;
import uk.kihira.tails.forge.common.TailsConfig;
import uk.kihira.tails.forge.common.network.C2SPlayerDataMessage;
import uk.kihira.tails.forge.common.network.TailsNetworkManager;
import uk.kihira.tails.forge.common.platform.ResourceManagerWrapperImpl;
import uk.kihira.tails.forge.mixin.client.CubeDefinitionAccess;
import uk.kihira.tails.forge.mixin.client.PartDefinitionAccess;

public final class TailsClientPlatformImpl implements TailsClientPlatform {

	private final LibraryManager libraryManager = new ClientLibraryManager();

	@SuppressWarnings("unchecked")
	@Override
	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight) {
		final ModelPart ret = makePartDefinition(part).bake(textureWidth, textureHeight);

		final List<ModelPart> queue = new ArrayList<>();
		queue.add(ret);

		while (!queue.isEmpty()) {
			final ModelPart next = queue.remove(0);
			((ModelPartExtensions) (Object) next).tails$storeInitialPose();
			queue.addAll((Collection) ((TailsModelPart) (Object) next).t$getChildren().values());
		}

		return (TailsModelPart) (Object) ret;
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
		final CubeDefinition ret = CubeDefinitionAccess.tails$new(null,
				cube.u, cube.v,
				cube.x, cube.y, cube.z,
				cube.sizeX, cube.sizeY, cube.sizeZ,
				new CubeDeformation(cube.growX, cube.growY, cube.growZ),
				cube.mirror, 1, 1);

		if (!cube.visibleFaces.containsAll(Arrays.asList(TailsDirection.values()))) {
			final ModelPartCubeExtensions ext = (ModelPartCubeExtensions) (Object) ret;
			final Set<Direction> hidden = EnumSet.allOf(Direction.class);
			for (TailsDirection direction : cube.visibleFaces)
				hidden.remove(Direction.values()[direction.ordinal()]);

			ext.tails$setHiddenFaces(hidden);
		}

		return ret;
	}

	@Override
	public boolean hasTexture(TResourceLocation id) {
		return Minecraft.getInstance().getTextureManager()
				.getTexture((ResourceLocation) id, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture();
	}

	@Override
	public void registerTripleTintTexture(TResourceLocation id, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints) {
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

	private static GameProfileCache gameProfileCache;

	@Override
	public String fetchUsername(UUID uuid) {
		final Minecraft mc = Minecraft.getInstance();

		// So, there are a few different places we can try first...

		// Option A - Forge's "username cache"
		String username = UsernameCache.getLastKnownUsername(uuid);
		if (username != null) return username;

		// Option B - The user cache (some assembly required)
		if (gameProfileCache == null) {
			gameProfileCache = new GameProfileCache(
					new YggdrasilAuthenticationService(mc.getProxy()).createProfileRepository(),
					new File(mc.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
			gameProfileCache.setExecutor(mc);
			GameProfileCache.setUsesAuthentication(false);
		}

		if (gameProfileCache != null) {
			username = gameProfileCache.get(uuid).map(GameProfile::getName).orElse(null);
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
			if (gameProfileCache != null)
				gameProfileCache.add(profile);

			return username;
		}

		// Option D - Just use the UUID
		return uuid.toString();
	}

	@Override
	public UUID getLocalUUID() {
		final Minecraft mc = Minecraft.getInstance();
		return mc.player != null ? mc.player.getUUID() : Player.createPlayerUUID(mc.getUser().getGameProfile());
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