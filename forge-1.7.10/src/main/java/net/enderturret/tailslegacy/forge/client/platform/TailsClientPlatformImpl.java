/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.platform;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;

import net.enderturret.tailslegacy.common.LibraryManager;
import net.enderturret.tailslegacy.common.TailsDirection;
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
import net.enderturret.tailslegacy.forge.client.render.ModelPartCubeExtensions;
import net.enderturret.tailslegacy.forge.client.render.ModelPartExtensions;
import net.enderturret.tailslegacy.forge.client.texture.TripleTintTexture;
import net.enderturret.tailslegacy.forge.common.Tails;
import net.enderturret.tailslegacy.forge.common.TailsConfig;
import net.enderturret.tailslegacy.forge.common.network.C2SPlayerDataMessage;
import net.enderturret.tailslegacy.forge.common.network.TailsNetworkManager;
import net.enderturret.tailslegacy.forge.common.platform.ResourceManagerWrapperImpl;
import net.enderturret.tailslegacy.forge.mixin.client.TextureManagerAccess;

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

	@SuppressWarnings("unchecked")
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
		final boolean oldMirror = parent.mirror;
		parent.mirror = cube.mirror;

		final ModelBox ret = new ModelBox(parent,
				(int) cube.u, (int) cube.v,
				cube.x, cube.y, cube.z,
				(int) cube.sizeX, (int) cube.sizeY, (int) cube.sizeZ,
				cube.growX);

		parent.mirror = oldMirror;

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
	public void registerTripleTintTexture(TResourceLocation id, Part part, SubType subType, PartTexture texture, int[] tints) {
		Minecraft.getMinecraft().getTextureManager().loadTexture((ResourceLocation) id, new TripleTintTexture(
				(ResourceLocation) part.getId().t$withPath(texture.path()),
				tints[0], tints[1], tints[2], texture.tintingStrategy()
				));
	}

	@Override
	public void releaseTexture(TResourceLocation id) {
		try {
			Minecraft.getMinecraft().getTextureManager().deleteTexture((ResourceLocation) id);
			((TextureManagerAccess) Minecraft.getMinecraft().getTextureManager()).tails$mapTextureObjects().remove(id);
		} catch (Exception e) {
			Tails.LOGGER.warn("Exception releasing {}:", e);
		}
	}

	public static void reloadParts(IResourceManager manager) {
		PartRegistry.MANAGER.reload(new ResourceManagerWrapperImpl(manager));
	}

	@Override
	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar) {
		MinecraftForge.EVENT_BUS.post(new RegisterPartRenderersEvent(registrar));
	}

	@Override
	public String fetchUsername(UUID uuid) {
		final Minecraft mc = Minecraft.getMinecraft();

		// So, there are a few different places we can try first...

		// Option A - Forge's "username cache"
		String username = UsernameCache.getLastKnownUsername(uuid);
		if (username != null) return username;

		// Option B - "Just query it lol"
		GameProfile profile = new GameProfile(uuid, null);
		profile = mc.func_152347_ac().fillProfileProperties(profile, false);
		username = profile.getName();

		if (username != null) return username;

		// Option D - Just use the UUID
		return uuid.toString();
	}

	@Override
	public UUID getLocalUUID() {
		final Minecraft mc = Minecraft.getMinecraft();
		return mc.thePlayer != null ? mc.thePlayer.getUniqueID() : EntityPlayer.func_146094_a(mc.getSession().func_148256_e());
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
		if (Minecraft.getMinecraft().theWorld != null)
			TailsNetworkManager.get().sendToServer(new C2SPlayerDataMessage(partsData));
	}

	@Override
	public LibraryManager getLibraryManager() {
		return libraryManager;
	}
}