/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.panel.TintPanel;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.client.part.ClientPlayerPartManager;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.client.render.FoxtatoRenderer;
import uk.kihira.tails.client.render.helper.FakeEntityRenderHelper;
import uk.kihira.tails.client.render.helper.PlayerRenderHelper;
import uk.kihira.tails.client.render.helper.RenderHelperManager;
import uk.kihira.tails.client.render.layer.PartLayer;
import uk.kihira.tails.client.render.layer.TailsArrowLayer;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.Tails;

/**
 * Handles a variety of increasingly-exciting events.
 */
@Internal
public final class ClientEventHandler {

	/**
	 * Handles events on the Forge bus.
	 * @author EnderTurret
	 */
	@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	static class Forge {

		private static boolean sentPartInfoToServer = false;
		private static boolean clearAllPartInfo = false;

		/*
		 * Tails Editor Button
		 */
		@SubscribeEvent
		static void onScreenInitPost(ScreenEvent.Init.Post event) {
			if (event.getScreen() instanceof PauseScreen)
				event.addListener(new Button(event.getScreen().width / 2 - 35, event.getScreen().height - 25, 70, 20, Component.translatable("tails.gui.button.editor"), b -> {
					Minecraft.getInstance().setScreen(EditorScreen.openDefault());
				}));
		}

		/*
		 * Tails Syncing
		 */
		@SubscribeEvent
		static void onConnectToServer(ClientPlayerNetworkEvent.LoggingIn event) {
			// Add local player texture to map.
			ClientPlayerPartManager.get().set(ClientUtils.getPlayerUUID(), LocalPartManager.getLocalPartsData());
		}

		@SubscribeEvent
		static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut e) {
			// TODO: Do we need to defer these?
			sentPartInfoToServer = false;
			clearAllPartInfo = true;
		}

		@SubscribeEvent
		static void onClientTick(TickEvent.ClientTickEvent e) {
			if (e.phase == TickEvent.Phase.START)
				if (clearAllPartInfo) {
					ClientPlayerPartManager.get().clear();
					clearAllPartInfo = false;
				}
				// World can't be null if we want to send a packet it seems.
				else if (!sentPartInfoToServer && Minecraft.getInstance().level != null) {
					LocalPartManager.syncToServer();

					sentPartInfoToServer = true;
				}
		}
	}

	/**
	 * Handles events on the mod bus.
	 * @author EnderTurret
	 */
	@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	static class Mod {

		@SubscribeEvent
		static void clientSetup(FMLClientSetupEvent e) {
			RenderHelperManager.registerRenderHelper(Player.class, new PlayerRenderHelper());
			RenderHelperManager.registerRenderHelper(FakeEntity.class, new FakeEntityRenderHelper());

			if (ModList.get().isLoaded("botania"))
				registerFoxtato(); // Try to avoid class loading it if Botania isn't present.
		}

		@SubscribeEvent
		static void addClientReloadListeners(RegisterClientReloadListenersEvent e) {
			e.registerReloadListener((ResourceManagerReloadListener) manager -> {
				maybeDestroyCursor();
				registerCursor(manager);
			});
		}

		private static void maybeDestroyCursor() {
			if (TintPanel.pickerCursorHandle != MemoryUtil.NULL)
				GLFW.glfwDestroyCursor(TintPanel.pickerCursorHandle);
		}

		private static void registerCursor(ResourceManager manager) {
			final Resource resource = manager.getResource(IconButton.iconsTextures).orElse(null);

			if (resource == null)
				throw new IllegalStateException("Could not find icon textures!");

			final NativeImage iconImg;

			try (InputStream is = resource.open()) {
				iconImg = NativeImage.read(NativeImage.Format.RGBA, is);
			} catch (IOException e) {
				throw new IllegalStateException("Failed to read icon texture:", e);
			}

			try (GLFWImage img = GLFWImage.malloc(); MemoryStack stack = MemoryStack.stackPush()) {
				final ByteBuffer data = stack.malloc(16 * 16 * 4);

				TextureHelper.copyPixels(iconImg, data, IconButton.Icons.EYEDROPPER.u, IconButton.Icons.EYEDROPPER.v + 16, 16, 16);
				data.flip();
				img.set(16, 16, data);

				final long handle = GLFW.glfwCreateCursor(img, 0, 14);

				if (handle == MemoryUtil.NULL)
					throw new IllegalStateException("Failed to create cursor!");

				TintPanel.pickerCursorHandle = handle;
			}

			iconImg.close();
		}

		private static void registerFoxtato() {
			MinecraftForge.EVENT_BUS.register(new FoxtatoRenderer());
		}

		@SubscribeEvent
		static void addLayers(EntityRenderersEvent.AddLayers e) {
			final Minecraft mc = Minecraft.getInstance();
			final Map<String, EntityRenderer<? extends Player>> skinMap = mc.getEntityRenderDispatcher().getSkinMap();

			// Make a context here because the event doesn't have one even though it's literally three lines away.
			final EntityRendererProvider.Context ctx = new EntityRendererProvider.Context(mc.getEntityRenderDispatcher(),
					mc.getItemRenderer(), mc.getBlockRenderer(), mc.getEntityRenderDispatcher().getItemInHandRenderer(),
					mc.getResourceManager(), mc.getEntityModels(), mc.font);

			for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
				final PlayerRenderer renderer2 = (PlayerRenderer) renderer;
				renderer2.addLayer(new PartLayer(renderer2));

				for (int i = 0; i < renderer2.layers.size(); i++)
					// If other mods do this exact same thing, let them take precedence.
					// If it's just an ArrowLayer mixin, then sucks for them.
					if (renderer2.layers.get(i).getClass() == ArrowLayer.class) {
						renderer2.layers.set(i, new TailsArrowLayer<>(ctx, renderer2));
						break;
					}
			}
		}
	}
}