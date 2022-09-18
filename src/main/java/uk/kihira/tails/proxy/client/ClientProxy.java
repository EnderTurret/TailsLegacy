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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import uk.kihira.tails.client.ClientLibraryManager;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.render.FakeEntityRenderHelper;
import uk.kihira.tails.client.render.FoxtatoRenderer;
import uk.kihira.tails.client.render.PlayerRenderHelper;
import uk.kihira.tails.client.render.RenderHelperManager;
import uk.kihira.tails.client.render.layer.PartLayer;
import uk.kihira.tails.client.render.layer.TailsArrowLayer;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.proxy.CommonProxy;

/**
 * The client proxy, buried deep inside a random package so that the class loader will be unable to discover it.<br><br>
 * If the class loader finds this through something other than {@link CommonProxy#makeClientProxy()},<br>
 * please dispose of the class loader immediately and get one that is not a professional client proxy hunter.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

	private LibraryManager libraryManager;

	@Override
	public void init() {
		registerMessages();
		libraryManager = new ClientLibraryManager();

		RenderHelperManager.registerRenderHelper(Player.class, new PlayerRenderHelper());
		RenderHelperManager.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());

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

	@SubscribeEvent
	public static void addLayers(EntityRenderersEvent.AddLayers e) {
		final Minecraft mc = Minecraft.getInstance();
		final Map<String, EntityRenderer<? extends Player>> skinMap = mc.getEntityRenderDispatcher().getSkinMap();

		// Make a context here because the event doesn't have one even though it's literally three lines away.
		final EntityRendererProvider.Context ctx = new EntityRendererProvider.Context(mc.getEntityRenderDispatcher(),
				mc.getItemRenderer(), mc.getBlockRenderer(), mc.getEntityRenderDispatcher().getItemInHandRenderer(),
				mc.getResourceManager(), mc.getEntityModels(), mc.font);

		for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
			final PlayerRenderer renderer2 = (PlayerRenderer) renderer;
			renderer2.addLayer(new PartLayer(renderer2, renderer2.getModel().body, PartType.TAIL));
			renderer2.addLayer(new PartLayer(renderer2, renderer2.getModel().body, PartType.WINGS));
			renderer2.addLayer(new PartLayer(renderer2, renderer2.getModel().head, PartType.EARS));
			renderer2.addLayer(new PartLayer(renderer2, renderer2.getModel().head, PartType.MUZZLE));

			for (int i = 0; i < renderer2.layers.size(); i++)
				// If other mods do this exact same thing, let them take precedence.
				// If it's just an ArrowLayer mixin, then sucks for them.
				if (renderer2.layers.get(i).getClass() == ArrowLayer.class) {
					renderer2.layers.set(i, new TailsArrowLayer<>(ctx, renderer2));
					break;
				}
		}
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
	public Gson configureGson(GsonBuilder builder) {
		builder.registerTypeHierarchyAdapter(IPartInfo.class, new ClientPartInfo.Serializer());
		return builder.create();
	}
}