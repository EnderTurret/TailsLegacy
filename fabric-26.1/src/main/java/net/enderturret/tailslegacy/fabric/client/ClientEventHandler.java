/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.platform.NativeImage;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback.RegistrationHelper;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import net.enderturret.tailslegacy.common.JavaColor;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.common.client.part.ClientPlayerPartManager;
import net.enderturret.tailslegacy.common.client.part.LocalPartManager;
import net.enderturret.tailslegacy.common.client.render.helper.FakeEntityRenderHelper;
import net.enderturret.tailslegacy.common.client.render.helper.PlayerRenderHelper;
import net.enderturret.tailslegacy.common.client.render.helper.RenderHelperManager;
import net.enderturret.tailslegacy.common.network.BasePlayerDataMapMessage;
import net.enderturret.tailslegacy.common.network.BaseS2CPlayerDataMessage;
import net.enderturret.tailslegacy.fabric.client.gui.EditorScreen;
import net.enderturret.tailslegacy.fabric.client.gui.TailsComponents;
import net.enderturret.tailslegacy.fabric.client.gui.panel.TintPanel;
import net.enderturret.tailslegacy.fabric.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.fabric.client.platform.TailsClientPlatformImpl;
import net.enderturret.tailslegacy.fabric.client.render.BotaniaFoxtatoRenderer;
import net.enderturret.tailslegacy.fabric.client.render.PartPreviewRenderer;
import net.enderturret.tailslegacy.fabric.client.render.layer.PartLayer;
import net.enderturret.tailslegacy.fabric.client.render.layer.TailsArrowLayer;
import net.enderturret.tailslegacy.fabric.client.toast.ToastManager;
import net.enderturret.tailslegacy.fabric.common.network.S2CBulkPlayerDataMessage;
import net.enderturret.tailslegacy.fabric.common.network.S2CPlayerDataMessage;
import net.enderturret.tailslegacy.fabric.mixin.client.LivingEntityRendererAccess;

/**
 * Handles a variety of increasingly-exciting events.
 */
@Internal
public final class ClientEventHandler {

	private static final MethodHandle SCREEN_ADDRENDERABLEWIDGET;

	static {
		try {
			final Method m = Screen.class.getDeclaredMethod("addRenderableWidget", GuiEventListener.class);
			m.setAccessible(true);
			SCREEN_ADDRENDERABLEWIDGET = MethodHandles.publicLookup().unreflect(m);
		} catch (Exception e) {
			throw new RuntimeException("Exception binding addRenderableWidget():", e);
		}
	}

	public static void register() {
		Mod.clientSetup();
		TailsKeybinds.registerKeys();
		Mod.registerPiPs();
		Mod.addClientReloadListeners();
		ToastManager.register();
		ClientTickEvents.START_CLIENT_TICK.register(Forge::onClientTickPre);
		ClientTickEvents.END_CLIENT_TICK.register(Forge::onClientTickPost);
		ScreenEvents.AFTER_INIT.register(Forge::onScreenInitPost);
		ClientPlayConnectionEvents.JOIN.register(Forge::onConnectToServer);
		ClientPlayConnectionEvents.DISCONNECT.register(Forge::onDisconnect);
		LivingEntityRenderLayerRegistrationCallback.EVENT.register(Mod::addLayers);

		ClientPlayNetworking.registerGlobalReceiver(S2CPlayerDataMessage.TYPE, ClientEventHandler::handle);
		ClientPlayNetworking.registerGlobalReceiver(S2CBulkPlayerDataMessage.TYPE, ClientEventHandler::handle);
	}

	public static void handle(S2CPlayerDataMessage message, ClientPlayNetworking.Context context) {
		BaseS2CPlayerDataMessage.handle(message.uuid(), message.partsData());
	}

	public static void handle(S2CBulkPlayerDataMessage message, ClientPlayNetworking.Context context) {
		BasePlayerDataMapMessage.handle(message.partsDataMap());
	}

	/**
	 * Handles events on the Forge bus.
	 * @author EnderTurret
	 */
	static class Forge {

		private static boolean sentPartInfoToServer = false;
		private static boolean clearAllPartInfo = false;

		/*
		 * Tails Editor Button
		 */
		static void onScreenInitPost(Minecraft mc, Screen screen, int scaledWidth, int scaledHeight) {
			ScreenEvents.afterExtract(screen).register(ToastManager::extractRenderState);
			if (screen instanceof PauseScreen)
				try {
					SCREEN_ADDRENDERABLEWIDGET.invoke(screen, Button.builder(TailsComponents.EDITOR_BUTTON,
							_ -> Minecraft.getInstance().setScreen(EditorScreen.openDefault()))
							.bounds(screen.width / 2 - 35, screen.height - 25, 70, 20)
							.build());
				} catch (Throwable e) {
					TailsPlatform.get().logError("Exception adding button to pause screen:", e);
				}
		}

		/*
		 * Tails Syncing
		 */
		static void onConnectToServer(ClientPacketListener listener, PacketSender sender, Minecraft mc) {
			// Add local player texture to map.
			ClientPlayerPartManager.get().set(TailsClientPlatform.get().getLocalUUID(), LocalPartManager.getLocalPartsData());
		}

		static void onDisconnect(ClientPacketListener listener, Minecraft mc) {
			// TODO: Do we need to defer these?
			sentPartInfoToServer = false;
			clearAllPartInfo = true;
		}

		static void onClientTickPre(Minecraft mc) {
			TailsKeybinds.checkKeys();

			if (clearAllPartInfo) {
				ClientPlayerPartManager.get().clear();
				clearAllPartInfo = false;
			}
			// World can't be null if we want to send a packet it seems.
			else if (!sentPartInfoToServer && mc.level != null) {
				LocalPartManager.syncToServer();

				sentPartInfoToServer = true;
			}
		}

		@SuppressWarnings("unchecked")
		static void onClientTickPost(Minecraft mc) {
			if (mc.level == null || mc.isPaused()) return;

			ClientPlayerPartManager.get().tick((Collection) mc.level.players());
		}
	}

	/**
	 * Handles events on the mod bus.
	 * @author EnderTurret
	 */
	static class Mod {

		static void clientSetup() {
			{
				RenderHelperManager.registerRenderHelper(new PlayerRenderHelper());
				RenderHelperManager.registerRenderHelper(new FakeEntityRenderHelper());
			}

			if (FabricLoader.getInstance().isModLoaded("botania"))
				registerFoxtato(); // Try to avoid class loading it if Botania isn't present.
		}

		static void addClientReloadListeners() {
			ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "parts"), (ResourceManagerReloadListener) TailsClientPlatformImpl::reloadParts);
			ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "cursors"), (ResourceManagerReloadListener) manager -> {
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
					final int pixel = src.getPixel(x, y);
					dest.put((byte) JavaColor.red(pixel));
					dest.put((byte) JavaColor.green(pixel));
					dest.put((byte) JavaColor.blue(pixel));
					dest.put((byte) JavaColor.alpha(pixel));
				}
		}

		private static void registerFoxtato() {
			BotaniaFoxtatoRenderer.register();
		}

		static void addLayers(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer, RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
			if (entityType != EntityType.PLAYER) return;

			final Minecraft mc = Minecraft.getInstance();

			final AvatarRenderer<? extends AbstractClientPlayer> renderer2 = (AvatarRenderer<? extends AbstractClientPlayer>) entityRenderer;
			registrationHelper.register(new PartLayer<>(renderer2));

			final List<RenderLayer<?, ?>> layers = ((LivingEntityRendererAccess) entityRenderer).tails$layers();
			for (int i = 0; i < layers.size(); i++)
				// If other mods do this exact same thing, let them take precedence.
				// If it's just an ArrowLayer mixin, then sucks for them.
				if (layers.get(i).getClass() == ArrowLayer.class) {
					layers.set(i, new TailsArrowLayer<>(renderer2, context));
					break;
				}
		}

		static void registerPiPs() {
			PictureInPictureRendererRegistry.register(PartPreviewRenderer::new);
		}
	}
}