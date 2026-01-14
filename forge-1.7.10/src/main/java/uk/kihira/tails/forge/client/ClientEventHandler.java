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
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
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
import uk.kihira.tails.forge.common.TailsConfig;

/**
 * Handles a variety of increasingly-exciting events.
 */
@Internal
public final class ClientEventHandler {

	public static final ThreadLocal<RenderPlayer> ACTIVE_PLAYER_RENDERER = ThreadLocal.withInitial(() -> null);
	public static volatile float partialTick;

	public static void onPreInit(FMLPreInitializationEvent e) {
		TailsConfig.CLIENT_INSTANCE.load(e.getModConfigurationDirectory());

		TailsKeybinds.registerKeys();
		RenderHelperManager.registerRenderHelper(new PlayerRenderHelper());
		RenderHelperManager.registerRenderHelper(new FakeEntityRenderHelper());

		final IReloadableResourceManager _manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();

		_manager.registerReloadListener((IResourceManagerReloadListener) TailsClientPlatformImpl::reloadParts);
		_manager.registerReloadListener((IResourceManagerReloadListener) manager -> {
			maybeDestroyCursor();
			registerCursor(manager);
		});

		if (Loader.isModLoaded("botania"))
			registerFoxtato(); // Try to avoid class loading it if Botania isn't present.
	}

	public static void onPostInit() {
	}

	private static void maybeDestroyCursor() {
		if (TintPanel.pickerCursorHandle != null)
			TintPanel.pickerCursorHandle.destroy();
	}

	private static void registerCursor(IResourceManager manager) {
		final BufferedImage iconImg;

		try {
			final IResource resource = manager.getResource(IconButton.ICONS_TEXTURE);
			try (InputStream is = resource.getInputStream()) {
				iconImg = ImageIO.read(is);
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read icon texture:", e);
		}

		try {
			final IntBuffer data = BufferUtils.createIntBuffer(16 * 16);

			data.put(iconImg.getRGB(TailsIcons.EYEDROPPER.u, TailsIcons.EYEDROPPER.v + 16, 16, 16, null, 0, 16));
			data.flip();

			TintPanel.pickerCursorHandle = new Cursor(16, 16, 0, 14, 1, data, null);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private static void registerFoxtato() {
		MinecraftForge.EVENT_BUS.register(BotaniaFoxtatoRenderer.class);
	}

	private static boolean sentPartInfoToServer = false;
	private static boolean clearAllPartInfo = false;

	private static final int EDITOR_ID = 20240901;

	/*
	 * Tails Editor Button
	 */
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.gui instanceof GuiIngameMenu)
			event.buttonList.add(new GuiButtonExt(EDITOR_ID, event.gui.width / 2 - 35, event.gui.height - 25, 70, 20,
					TailsComponents.EDITOR_BUTTON.getFormattedText()));
	}

	@SubscribeEvent
	public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre e) {
		if (e.gui instanceof GuiIngameMenu && e.button.id == EDITOR_ID) {
			Minecraft.getMinecraft().displayGuiScreen(EditorScreen.openDefault());
			e.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRenderPlayerPre(RenderPlayerEvent.Pre e) {
		partialTick = e.partialRenderTick;
		ACTIVE_PLAYER_RENDERER.set(e.renderer);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderPlayerPost(RenderPlayerEvent.Post e) {
		ACTIVE_PLAYER_RENDERER.set(null);
	}

	public static final class CommonHandlerBus {

		@SubscribeEvent
		@SuppressWarnings("unchecked")
		public void onClientTick(ClientTickEvent e) {
			if (e.phase == TickEvent.Phase.START) {
				if (clearAllPartInfo) {
					ClientPlayerPartManager.get().clear();
					clearAllPartInfo = false;
				}
				// World can't be null if we want to send a packet it seems.
				else if (!sentPartInfoToServer && Minecraft.getMinecraft().theWorld != null) {
					LocalPartManager.syncToServer();

					sentPartInfoToServer = true;
				}
			} else {
				if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().isGamePaused()) return;

				ClientPlayerPartManager.get().tick(Minecraft.getMinecraft().theWorld.playerEntities);
			}
		}

		@SubscribeEvent
		public void onKeyPressed(InputEvent.KeyInputEvent e) {
			TailsKeybinds.onKeyPressed(e);
		}

		/*
		 * Tails Syncing
		 */
		@SubscribeEvent
		public void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
			// Add local player texture to map.
			ClientPlayerPartManager.get().set(TailsClientPlatform.get().getLocalUUID(), LocalPartManager.getLocalPartsData());
		}

		@SubscribeEvent
		public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
			// TODO: Do we need to defer these?
			sentPartInfoToServer = false;
			clearAllPartInfo = true;
		}
	}
}