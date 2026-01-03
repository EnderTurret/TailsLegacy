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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;

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

public final class TailsClientPlatformImpl implements TailsClientPlatform {

	private final LibraryManager libraryManager = new ClientLibraryManager();

	@SuppressWarnings("unchecked")
	@Override
	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight) {
		final ModelBase dummyModel = new ModelBase() {};
		dummyModel.isChild = false;
		dummyModel.textureWidth = textureWidth;
		dummyModel.textureHeight = textureHeight;
		final ModelRenderer ret = makePartDefinition(null, part, dummyModel);

		final List<ModelRenderer> queue = new ArrayList<>();
		queue.add(ret);

		while (!queue.isEmpty()) {
			final ModelRenderer next = queue.remove(0);
			((ModelPartExtensions) next).tails$storeInitialPose();
			queue.addAll((Collection) ((TailsModelPart) next).t$getChildren().values());
		}

		dummyModel.boxList.clear();

		return (TailsModelPart) ret;
	}

	private static ModelRenderer makePartDefinition(@Nullable String name, TailsPartDefinition part, ModelBase dummyModel) {
		final ModelRenderer ret = new ModelRenderer(dummyModel, name);

		ret.textureWidth = dummyModel.textureWidth;
		ret.textureHeight = dummyModel.textureHeight;
		ret.offsetX = part.xOffset * 0.0625F;
		ret.offsetY = part.yOffset * 0.0625F;
		ret.offsetZ = part.zOffset * 0.0625F;
		ret.rotateAngleX = part.xRot;
		ret.rotateAngleY = part.yRot;
		ret.rotateAngleZ = part.zRot;

		for (TailsCubeDefinition cube : part.cubes)
			ret.cubeList.add(makeCubeDefinition(ret, cube));

		// Recursion :concern:
		for (Map.Entry<String, TailsPartDefinition> entry : part.children.entrySet()) {
			if (ret.childModels == null) ret.childModels = new ArrayList<>();
			ret.childModels.add(makePartDefinition(entry.getKey(), entry.getValue(), dummyModel));
		}

		return ret;
	}

	private static ModelBox makeCubeDefinition(ModelRenderer parent, TailsCubeDefinition cube) {
		final ModelBox ret = new ModelBox(parent,
				(int) cube.u, (int) cube.v,
				cube.x, cube.y, cube.z,
				(int) cube.sizeX, (int) cube.sizeY, (int) cube.sizeZ,
				cube.growX,
				cube.mirror);

		if (!cube.visibleFaces.containsAll(Arrays.asList(TailsDirection.values()))) {
			final ModelPartCubeExtensions ext = (ModelPartCubeExtensions) ret;
			final Set<EnumFacing> hidden = EnumSet.allOf(EnumFacing.class);
			for (TailsDirection direction : cube.visibleFaces)
				hidden.remove(EnumFacing.values()[direction.ordinal()]);

			ext.tails$setHiddenFaces(hidden);
		}

		return ret;
	}

	@Override
	public boolean hasTexture(TResourceLocation id) {
		return Minecraft.getMinecraft().getTextureManager()
				.getTexture((ResourceLocation) id) != null;
	}

	@Override
	public void registerTripleTintTexture(TResourceLocation id, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints) {
		Minecraft.getMinecraft().getTextureManager().loadTexture((ResourceLocation) id, new TripleTintTexture(
				(ResourceLocation) part.getId().t$withPath(texture.path()),
				tints[0], tints[1], tints[2], texture.tintingStrategy()
				));
	}

	@Override
	public void releaseTexture(TResourceLocation id) {
		try {
			Minecraft.getMinecraft().getTextureManager().deleteTexture((ResourceLocation) id);
		} catch (Exception ignored) {}
	}

	public static void reloadParts(IResourceManager manager, Predicate<IResourceType> predicate) {
		if (predicate == null || predicate.test(VanillaResourceType.TEXTURES))
			PartRegistry.MANAGER.reload(new ResourceManagerWrapperImpl(manager));
	}

	@Override
	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar) {
		MinecraftForge.EVENT_BUS.post(new RegisterPartRenderersEvent(registrar));
	}

	private static PlayerProfileCache gameProfileCache;

	@Override
	public String fetchUsername(UUID uuid) {
		final Minecraft mc = Minecraft.getMinecraft();

		// So, there are a few different places we can try first...

		// Option A - Forge's "username cache"
		String username = UsernameCache.getLastKnownUsername(uuid);
		if (username != null) return username;

		// Option B - The user cache (some assembly required)
		if (gameProfileCache == null) {
			gameProfileCache = new PlayerProfileCache(
					new YggdrasilAuthenticationService(mc.getProxy(), UUID.randomUUID().toString()).createProfileRepository(),
					new File(mc.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
			PlayerProfileCache.setOnlineMode(false);
		}

		if (gameProfileCache != null) {
			final GameProfile profile = gameProfileCache.getProfileByUUID(uuid);
			if (profile != null) return profile.getName();
		}

		// Option C - "Just query it lol"
		GameProfile profile = new GameProfile(uuid, null);
		profile = mc.getSessionService().fillProfileProperties(profile, false);
		username = profile.getName();

		// Surprisingly, we actually got a username. Let's inform the caches, shall we?
		if (username != null) {
			// Unfortunately, it looks like Forge's username cache is and I quote "too good for manipulation."
			// So instead we are only able to let our little profile cache know.
			if (gameProfileCache != null)
				gameProfileCache.addEntry(profile);

			return username;
		}

		// Option D - Just use the UUID
		return uuid.toString();
	}

	@Override
	public UUID getLocalUUID() {
		final Minecraft mc = Minecraft.getMinecraft();
		/*
		if (mc.player != null && mc.player.getUniqueID() != null)
			return mc.player.getUniqueID();
		*/
		return mc.player != null ? mc.player.getUniqueID() : mc.getSession().getProfile().getId();
	}

	@Override
	public String getConfigParts() {
		return TailsConfig.CLIENT_INSTANCE.localPlayerData();
	}

	@Override
	public void setConfigParts(String json) {
		TailsConfig.CLIENT_INSTANCE.setLocalPlayerData(json);
		TailsConfig.CLIENT_INSTANCE.save();
	}

	@Override
	public Path getConfigDir() {
		return TailsConfig.CLIENT_INSTANCE.configDir().toPath();
	}

	@Override
	public void syncLocalToServer(ClientPartsData partsData) {
		if (Minecraft.getMinecraft().world != null)
			TailsNetworkManager.get().sendToServer(new C2SPlayerDataMessage(partsData));
	}

	@Override
	public LibraryManager getLibraryManager() {
		return libraryManager;
	}
}