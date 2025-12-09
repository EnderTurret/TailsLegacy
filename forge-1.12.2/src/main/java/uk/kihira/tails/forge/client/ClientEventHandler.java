/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import uk.kihira.tails.common.ABGRColor;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common.client.part.LocalPartManager;
import uk.kihira.tails.common.client.render.helper.FakeEntityRenderHelper;
import uk.kihira.tails.common.client.render.helper.PlayerRenderHelper;
import uk.kihira.tails.common.client.render.helper.RenderHelperManager;
import uk.kihira.tails.forge.client.gui.EditorScreen;
import uk.kihira.tails.forge.client.gui.TailsComponents;
import uk.kihira.tails.forge.client.gui.panel.TintPanel;
import uk.kihira.tails.forge.client.gui.widget.IconButton;
import uk.kihira.tails.forge.client.platform.TailsClientPlatformImpl;
import uk.kihira.tails.forge.client.render.BotaniaFoxtatoRenderer;
import uk.kihira.tails.forge.client.render.layer.PartLayer;
import uk.kihira.tails.forge.client.render.layer.TailsArrowLayer;
import uk.kihira.tails.forge.mixin.client.LivingEntityRendererAccess;

/**
 * Handles a variety of increasingly-exciting events.
 */
@Internal
public final class ClientEventHandler {

	/**
	 * Handles events on the Forge bus.
	 * @author EnderTurret
	 */
	@EventBusSubscriber(modid = TailsPlatform.MOD_ID, value = Side.CLIENT)
	static class Forge {

		private static boolean sentPartInfoToServer = false;
		private static boolean clearAllPartInfo = false;

		/*
		 * Tails Editor Button
		 */
		@SubscribeEvent
		static void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
			if (event.getGui() instanceof GuiIngameMenu)
				event.addListener(new GuiButtonExt(event.getGui().width / 2 - 35, event.getGui().height - 25, 70, 20, TailsComponents.EDITOR_BUTTON,
						b -> Minecraft.getInstance().setScreen(EditorScreen.openDefault())));
		}

		/*
		 * Tails Syncing
		 */
		@SubscribeEvent
		static void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
			// Add local player texture to map.
			ClientPlayerPartManager.get().set(TailsClientPlatform.get().getLocalUUID(), LocalPartManager.getLocalPartsData());
		}

		@SubscribeEvent
		static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
			// TODO: Do we need to defer these?
			sentPartInfoToServer = false;
			clearAllPartInfo = true;
		}

		@SubscribeEvent
		static void onClientTick(ClientTickEvent e) {
			if (e.phase != TickEvent.Phase.START) return;

			if (clearAllPartInfo) {
				ClientPlayerPartManager.get().clear();
				clearAllPartInfo = false;
			}
			// World can't be null if we want to send a packet it seems.
			else if (!sentPartInfoToServer && Minecraft.getMinecraft().world != null) {
				LocalPartManager.syncToServer();

				sentPartInfoToServer = true;
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
	@EventBusSubscriber(modid = TailsPlatform.MOD_ID, value = Side.CLIENT)
	static class Mod {

		@SubscribeEvent
		static void clientSetup(FMLClientSetupEvent e) {
			e.enqueueWork(() -> {
				TailsKeybinds.registerKeys();
				RenderHelperManager.registerRenderHelper(new PlayerRenderHelper());
				RenderHelperManager.registerRenderHelper(new FakeEntityRenderHelper());
			});

			if (Loader.isModLoaded("botania"))
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

		private static void registerCursor(IResourceManager manager) {
			final BufferedImage iconImg;

			try (IResource resource = manager.getResource(IconButton.ICONS_TEXTURE); InputStream is = resource.getInputStream()) {
				iconImg = TextureUtil.readBufferedImage(is);
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