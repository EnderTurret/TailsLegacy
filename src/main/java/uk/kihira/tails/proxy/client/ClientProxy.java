/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy.client;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import uk.kihira.tails.client.ClientEventHandler;
import uk.kihira.tails.client.ClientLibraryManager;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.render.FakeEntityRenderHelper;
import uk.kihira.tails.client.render.FoxtatoRenderer;
import uk.kihira.tails.client.render.PartLayer;
import uk.kihira.tails.client.render.PlayerRenderHelper;
import uk.kihira.tails.client.render.RenderHelperManager;
import uk.kihira.tails.client.render.RenderingHandler;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsConfig;
import uk.kihira.tails.common.part.PartRegistry;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.proxy.CommonProxy;

/**
 * The client proxy, buried deep inside a random package so that the class loader will be unable to discover it.<br><br>
 * If the class loader finds this through something other than {@link CommonProxy#makeClientProxy()},<br>
 * please dispose of the class loader immediately and get one that is not a professional client proxy hunter.
 */
@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		registerMessages();
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		libraryManager = new ClientLibraryManager();

		RenderHelperManager.registerRenderHelper(Player.class, new PlayerRenderHelper());
		RenderHelperManager.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());

		if (ModList.get().isLoaded("botania"))
			MinecraftForge.EVENT_BUS.register(new FoxtatoRenderer());

		// Class-load PartRegistry and PartRenderRegistry.
		PartRegistry.FLUFFY_TAIL.getId();
		PartRenderRegistry.getRenderer(PartRegistry.FLUFFY_TAIL);
	}

	@Override
	public void addPartsData(UUID uuid, PartsData partsData) {
		if (hasPartsData(uuid))
			this.partsData.get(uuid).clearTextures();

		super.addPartsData(uuid, partsData);
	}

	@Override
	public void removePartsData(UUID uuid) {
		if (hasPartsData(uuid))
			partsData.get(uuid).clearTextures();
		super.removePartsData(uuid);
	}

	@Override
	public void clearAllPartsData() {
		for (PartsData partInfo : partsData.values())
			partInfo.clearTextures();
		super.clearAllPartsData();
	}

	@Override
	public void registerRenderers() {
		boolean legacyRenderer = TailsConfig.CLIENT_INSTANCE.forceLegacyRendering.get();

		if (legacyRenderer) Tails.LOGGER.info("Legacy Renderer has been forced enabled.");
		else if (ModList.get().isLoaded("SmartMoving")) {
			Tails.LOGGER.info("Legacy Renderer enabled automatically for mod compatibility.");
			legacyRenderer = true;
		}

		final Map<String, EntityRenderer<? extends Player>> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();

		for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
			final PlayerRenderer renderer2 = (PlayerRenderer) renderer;
			renderer2.addLayer(new PartLayer(renderer2, renderer2.getModel().body, PartType.TAIL));
			renderer2.addLayer(new PartLayer(renderer2, renderer2.getModel().body, PartType.WINGS));
			renderer2.addLayer(new PartLayer(renderer2, renderer2.getModel().head, PartType.EARS));
			renderer2.addLayer(new PartLayer(renderer2, renderer2.getModel().head, PartType.MUZZLE));
		}
	}

	@Override
	public void deleteTexture(ResourceLocation tex) {
		Minecraft.getInstance().getTextureManager().release(tex);
	}
}
