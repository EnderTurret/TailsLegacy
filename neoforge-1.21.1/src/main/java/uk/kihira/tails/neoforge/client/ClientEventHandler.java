/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
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
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import uk.kihira.tails.common2.ABGRColor;
import uk.kihira.tails.common2.client.TailsClientPlatform;
import uk.kihira.tails.common2.client.gui.TailsIcons;
import uk.kihira.tails.common2.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common2.client.part.LocalPartManager;
import uk.kihira.tails.common2.client.render.helper.FakeEntityRenderHelper;
import uk.kihira.tails.common2.client.render.helper.PlayerRenderHelper;
import uk.kihira.tails.common2.client.render.helper.RenderHelperManager;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;
import uk.kihira.tails.neoforge.client.gui.panel.TintPanel;
import uk.kihira.tails.neoforge.client.gui.widget.IconButton;
import uk.kihira.tails.neoforge.client.render.BotaniaFoxtatoRenderer;
import uk.kihira.tails.neoforge.client.render.layer.PartLayer;
import uk.kihira.tails.neoforge.client.render.layer.TailsArrowLayer;
import uk.kihira.tails.neoforge.common.Tails;
import uk.kihira.tails.neoforge.mixin.client.LivingEntityRendererAccess;

/**
 * Handles a variety of increasingly-exciting events.
 */
@Internal
public final class ClientEventHandler {

	/**
	 * Handles events on the Forge bus.
	 * @author EnderTurret
	 */
	@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
	static class Forge {

		private static boolean sentPartInfoToServer = false;
		private static boolean clearAllPartInfo = false;

		/*
		 * Tails Editor Button
		 */
		@SubscribeEvent
		static void onScreenInitPost(ScreenEvent.Init.Post event) {
			if (event.getScreen() instanceof PauseScreen)
				event.addListener(Button.builder(Component.translatable("tails.gui.button.editor"),
						b -> Minecraft.getInstance().setScreen(EditorScreen.openDefault()))
						.bounds(event.getScreen().width / 2 - 35, event.getScreen().height - 25, 70, 20)
						.build());
		}

		/*
		 * Tails Syncing
		 */
		@SubscribeEvent
		static void onConnectToServer(ClientPlayerNetworkEvent.LoggingIn event) {
			// Add local player texture to map.
			ClientPlayerPartManager.get().set(TailsClientPlatform.get().getLocalUUID(), LocalPartManager.getLocalPartsData());
		}

		@SubscribeEvent
		static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut e) {
			// TODO: Do we need to defer these?
			sentPartInfoToServer = false;
			clearAllPartInfo = true;
		}

		@SubscribeEvent
		static void onClientTick(ClientTickEvent.Pre e) {
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

		@SubscribeEvent
		static void onKeyPressed(InputEvent.Key e) {
			TailsKeybinds.onKeyPressed(e);
		}
	}

	/**
	 * Handles events on the mod bus.
	 * @author EnderTurret
	 */
	@EventBusSubscriber(modid = Tails.MOD_ID, value = Dist.CLIENT)
	static class Mod {

		@SubscribeEvent
		static void clientSetup(FMLClientSetupEvent e) {
			e.enqueueWork(() -> {
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
			final Resource resource = manager.getResource(IconButton.ICONS_TEXTURE).orElse(null);

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
			NeoForge.EVENT_BUS.register(BotaniaFoxtatoRenderer.class);
		}

		@SubscribeEvent
		static void addLayers(EntityRenderersEvent.AddLayers e) {
			final Minecraft mc = Minecraft.getInstance();
			final Map<PlayerSkin.Model, EntityRenderer<? extends Player>> skinMap = mc.getEntityRenderDispatcher().getSkinMap();

			for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
				final PlayerRenderer renderer2 = (PlayerRenderer) renderer;
				renderer2.addLayer(new PartLayer<>(renderer2));

				final List<RenderLayer<?, ?>> layers = ((LivingEntityRendererAccess) renderer2).tails$layers();
				for (int i = 0; i < layers.size(); i++)
					// If other mods do this exact same thing, let them take precedence.
					// If it's just an ArrowLayer mixin, then sucks for them.
					if (layers.get(i).getClass() == ArrowLayer.class) {
						layers.set(i, new TailsArrowLayer<>(e.getContext(), renderer2));
						break;
					}
			}
		}

		@SubscribeEvent
		static void registerKeys(RegisterKeyMappingsEvent e) {
			TailsKeybinds.registerKeys(e);
		}
	}
}