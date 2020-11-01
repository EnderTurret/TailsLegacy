/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.proxy;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import uk.kihira.tails.client.ClientEventHandler;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.model.ModelRendererWrapper;
import uk.kihira.tails.client.render.FakeEntityRenderHelper;
import uk.kihira.tails.client.render.FoxtatoRenderer;
import uk.kihira.tails.client.render.PartLayer;
import uk.kihira.tails.client.render.PlayerRenderHelper;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.client.render.RenderingHandler;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.TailsConfig;
import uk.kihira.tails.common.network.LibraryEntriesMessage;
import uk.kihira.tails.common.network.LibraryRequestMessage;
import uk.kihira.tails.common.network.PlayerDataMapMessage;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.network.ServerCapabilitiesMessage;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		registerMessages();
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		libraryManager = new LibraryManager.ClientLibraryManager();

		PartRenderer.registerRenderHelper(PlayerEntity.class, new PlayerRenderHelper());
		PartRenderer.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());

		if (ModList.get().isLoaded("botania"))
			MinecraftForge.EVENT_BUS.register(new FoxtatoRenderer());
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

		final Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();

		if (legacyRenderer) {
			MinecraftForge.EVENT_BUS.register(new RenderingHandler());

			for (PlayerRenderer renderer : skinMap.values()) {
				final PlayerModel model = renderer.getEntityModel();
				model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.TAIL));
				model.bipedBody.addChild(new ModelRendererWrapper(model, PartsData.PartType.WINGS));
				model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.EARS));
				model.bipedHead.addChild(new ModelRendererWrapper(model, PartsData.PartType.MUZZLE));
			}
		} else {
			for (PlayerRenderer renderer : skinMap.values()) {
				renderer.addLayer(new PartLayer(renderer, renderer.getEntityModel().bipedBody, PartsData.PartType.TAIL));
				renderer.addLayer(new PartLayer(renderer, renderer.getEntityModel().bipedBody, PartsData.PartType.WINGS));
				renderer.addLayer(new PartLayer(renderer, renderer.getEntityModel().bipedHead, PartsData.PartType.EARS));
				renderer.addLayer(new PartLayer(renderer, renderer.getEntityModel().bipedHead, PartsData.PartType.MUZZLE));
			}
		}
	}
}
