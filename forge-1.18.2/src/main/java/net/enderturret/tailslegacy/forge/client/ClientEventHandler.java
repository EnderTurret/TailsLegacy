/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import net.enderturret.tailslegacy.common.ABGRColor;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.common.client.part.ClientPlayerPartManager;
import net.enderturret.tailslegacy.common.client.part.LocalPartManager;
import net.enderturret.tailslegacy.common.client.render.helper.FakeEntityRenderHelper;
import net.enderturret.tailslegacy.common.client.render.helper.PlayerRenderHelper;
import net.enderturret.tailslegacy.common.client.render.helper.RenderHelperManager;
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.forge.client.gui.panel.TintPanel;
import net.enderturret.tailslegacy.forge.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.forge.client.platform.TailsClientPlatformImpl;
import net.enderturret.tailslegacy.forge.client.render.BotaniaFoxtatoRenderer;
import net.enderturret.tailslegacy.forge.client.render.layer.PartLayer;
import net.enderturret.tailslegacy.forge.client.render.layer.TailsArrowLayer;
import net.enderturret.tailslegacy.forge.mixin.client.LivingEntityRendererAccess;

/**
 * Handles a variety of increasingly-exciting events.
 */
@Internal
public final class ClientEventHandler {

	/**
	 * Handles events on the Forge bus.
	 * @author EnderTurret
	 */
	@EventBusSubscriber(modid = TailsPlatform.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	static class Forge {

		private static boolean sentPartInfoToServer = false;
		private static boolean clearAllPartInfo = false;

		/*
		 * Tails Editor Button
		 */
		@SubscribeEvent
		static void onScreenInitPost(ScreenEvent.InitScreenEvent.Post event) {
			if (event.getScreen() instanceof PauseScreen)
				event.addListener(new ExtendedButton(event.getScreen().width / 2 - 35, event.getScreen().height - 25, 70, 20, TailsComponents.EDITOR_BUTTON,
						b -> Minecraft.getInstance().setScreen(EditorScreen.openDefault())));
		}

		/*
		 * Tails Syncing
		 */
		@SubscribeEvent
		static void onConnectToServer(ClientPlayerNetworkEvent.LoggedInEvent event) {
			// Add local player texture to map.
			ClientPlayerPartManager.get().set(TailsClientPlatform.get().getLocalUUID(), LocalPartManager.getLocalPartsData());
		}

		@SubscribeEvent
		static void onDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent e) {
			// TODO: Do we need to defer these?
			sentPartInfoToServer = false;
			clearAllPartInfo = true;
		}

		@SubscribeEvent
		@SuppressWarnings("unchecked")
		static void onClientTick(ClientTickEvent e) {
			if (e.phase == TickEvent.Phase.START) {
				if (clearAllPartInfo) {
					ClientPlayerPartManager.get().clear();
					clearAllPartInfo = false;
				}
				// World can't be null if we want to send a packet it seems.
				else if (!sentPartInfoToServer && Minecraft.getInstance().level != null) {
					LocalPartManager.syncToServer();

					sentPartInfoToServer = true;
				}
			} else {
				if (Minecraft.getInstance().level == null || Minecraft.getInstance().isPaused()) return;

				ClientPlayerPartManager.get().tick((Collection) Minecraft.getInstance().level.players());
			}
		}

		@SubscribeEvent
		static void onKeyPressed(InputEvent.KeyInputEvent e) {
			TailsKeybinds.onKeyPressed(e);
		}
	}

	/**
	 * Handles events on the mod bus.
	 * @author EnderTurret
	 */
	@EventBusSubscriber(modid = TailsPlatform.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	static class Mod {

		@SubscribeEvent
		static void clientSetup(FMLClientSetupEvent e) {
			e.enqueueWork(() -> {
				TailsKeybinds.registerKeys();
				RenderHelperManager.registerRenderHelper(new PlayerRenderHelper());
				RenderHelperManager.registerRenderHelper(new FakeEntityRenderHelper());
			});

			if (ModList.get().isLoaded("botania"))
				registerFoxtato(); // Try to avoid class loading it if Botania isn't present.
		}

		@SubscribeEvent
		static void addClientReloadListeners(RegisterClientReloadListenersEvent e) {
			e.registerReloadListener((ResourceManagerReloadListener) TailsClientPlatformImpl::reloadParts);
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
			final Resource resource;

			try {
				resource = manager.getResource(IconButton.ICONS_TEXTURE);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}

			final NativeImage iconImg;

			try (InputStream is = resource.getInputStream()) {
				iconImg = NativeImage.read(NativeImage.Format.RGBA, is);
			} catch (IOException e) {
				throw new IllegalStateException("Failed to read icon texture:", e);
			}

			try (GLFWImage img = GLFWImage.malloc(); MemoryStack stack = MemoryStack.stackPush()) {
				final ByteBuffer data = stack.malloc(16 * 16 * 4);

				copyPixels(iconImg, data, TailsIcons.EYEDROPPER.u, TailsIcons.EYEDROPPER.v + 16, 16, 16);
				data.flip();
				img.set(16, 16, data);

				final long handle = GLFW.glfwCreateCursor(img, 0, 14);

				if (handle == MemoryUtil.NULL)
					throw new IllegalStateException("Failed to create cursor!");

				TintPanel.pickerCursorHandle = handle;
			}

			iconImg.close();
		}

		/**
		 * Copies a region of pixels from the given {@link NativeImage} into the given buffer.
		 * @param src The source image.
		 * @param dest The destination buffer.
		 * @param fromX The coordinate corresponding to the left side of the region.
		 * @param fromY The coordinate corresponding to the top side of the region.
		 * @param width The width of the region.
		 * @param height The height of the region.
		 */
		public static void copyPixels(NativeImage src, ByteBuffer dest, int fromX, int fromY, int width, int height) {
			for (int y = fromY; y < fromY + height; y++)
				for (int x = fromX; x < fromX + width; x++) {
					final int pixel = src.getPixelRGBA(x, y);
					dest.put((byte) ABGRColor.red(pixel));
					dest.put((byte) ABGRColor.green(pixel));
					dest.put((byte) ABGRColor.blue(pixel));
					dest.put((byte) ABGRColor.alpha(pixel));
				}
		}

		private static void registerFoxtato() {
			MinecraftForge.EVENT_BUS.register(BotaniaFoxtatoRenderer.class);
		}

		@SubscribeEvent
		static void addLayers(EntityRenderersEvent.AddLayers e) {
			final Minecraft mc = Minecraft.getInstance();
			final Map<String, EntityRenderer<? extends Player>> skinMap = mc.getEntityRenderDispatcher().getSkinMap();

			for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
				final PlayerRenderer renderer2 = (PlayerRenderer) renderer;
				renderer2.addLayer(new PartLayer<>(renderer2));

				final List<RenderLayer<?, ?>> layers = ((LivingEntityRendererAccess) renderer2).tails$layers();
				for (int i = 0; i < layers.size(); i++)
					// If other mods do this exact same thing, let them take precedence.
					// If it's just an ArrowLayer mixin, then sucks for them.
					if (layers.get(i).getClass() == ArrowLayer.class) {
						layers.set(i, new TailsArrowLayer<>(Minecraft.getInstance().getEntityRenderDispatcher(), renderer2));
						break;
					}
			}
		}
	}
}