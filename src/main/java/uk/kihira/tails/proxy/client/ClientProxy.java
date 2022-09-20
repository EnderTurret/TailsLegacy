/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy.client;

import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

import uk.kihira.tails.api.IPlayerPartManager;
import uk.kihira.tails.client.ClientLibraryManager;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.part.ClientPlayerPartManager;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.client.render.FoxtatoRenderer;
import uk.kihira.tails.client.render.helper.FakeEntityRenderHelper;
import uk.kihira.tails.client.render.helper.PlayerRenderHelper;
import uk.kihira.tails.client.render.helper.RenderHelperManager;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.proxy.IProxy;

/**
 * <p>The client proxy, buried deep inside a random package so that the class loader will be unable to discover it.</p>
 * <p>If the class loader finds this through something other than {@link IProxy#makeClientProxy()},
 * please dispose of the class loader immediately and get one that is less cursed.</p>
 */
@OnlyIn(Dist.CLIENT)
public final class ClientProxy implements IProxy {

	private LibraryManager libraryManager;
	private final IPlayerPartManager partManager = new ClientPlayerPartManager();

	@Override
	public void init() {
		libraryManager = new ClientLibraryManager();

		RenderHelperManager.registerRenderHelper(Player.class, new PlayerRenderHelper());
		RenderHelperManager.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());

		if (ModList.get().isLoaded("botania"))
			MinecraftForge.EVENT_BUS.register(new FoxtatoRenderer());
	}

	@Override
	public void deleteTexture(ResourceLocation tex) {
		Minecraft.getInstance().getTextureManager().release(tex);
	}

	@Override
	public LibraryManager getLibraryManager() {
		return libraryManager;
	}

	@Override
	public IPlayerPartManager getPartManager() {
		return partManager;
	}

	@Override
	public Gson getSidedGson() {
		return LocalPartManager.GSON;
	}
}