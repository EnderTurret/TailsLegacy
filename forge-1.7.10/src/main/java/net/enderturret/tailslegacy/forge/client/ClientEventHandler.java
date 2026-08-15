/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

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
import net.enderturret.tailslegacy.forge.common.TailsConfig;
import net.enderturret.tailslegacy.forge.mixin.client.SimpleResourceAccess;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

/**
 * Handles a variety of increasingly-exciting events.
 */
@Internal
public final class ClientEventHandler {

	private static boolean originalTailsLoaded;

	public static final ThreadLocal<RenderPlayer> ACTIVE_PLAYER_RENDERER = ThreadLocal.withInitial(() -> null);
	public static volatile float partialTick;

	private static final FloatBuffer CURRENT_COLOR_BUFFER = BufferUtils.createFloatBuffer(/* 4 */ 16);
	private static final float[] CURRENT_COLOR_ARRAY = new float[4];

	public static float[] captureCurrentColor() {
		CURRENT_COLOR_BUFFER.clear();
		GL11.glGetFloat(GL11.GL_CURRENT_COLOR, CURRENT_COLOR_BUFFER);
		CURRENT_COLOR_BUFFER.rewind();
		CURRENT_COLOR_BUFFER.get(CURRENT_COLOR_ARRAY);
		return CURRENT_COLOR_ARRAY;
	}

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

		originalTailsLoaded = Loader.isModLoaded("tails");

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
	private static final int ORIGINAL_TAILS_ID = 1234;

	/*
	 * Tails Editor Button
	 */
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
		if (!(event.gui instanceof GuiIngameMenu)) return;

		int offset = 0;
		if (originalTailsLoaded)
			for (Object button : event.buttonList)
				if (button instanceof GuiButton && ((GuiButton) button).id == ORIGINAL_TAILS_ID) {
					offset = -22;
					break;
				}

		event.buttonList.add(new GuiButtonExt(EDITOR_ID,
				event.gui.width / 2 - 35,
				event.gui.height - 25 + offset,
				70, 20,
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

	@Internal
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

	public static void handleLoadingLangFile(SimpleResourceAccess resource, Map<String, String> properties) {
		final ResourceLocation rl = resource.tails$srResourceLocation();
		if (!TailsPlatform.MOD_ID.equals(rl.getResourceDomain())) return;

		final String modernFile = rl.getResourcePath()
				.toLowerCase(java.util.Locale.ENGLISH) // Before 1.11(?), lang files were en_US.lang etc.
				.replace(".lang", ".json");

		final ResourceLocation loc = new ResourceLocation(TailsPlatform.MOD_ID, modernFile);
		final IResourcePack pack = FMLClientHandler.instance().getResourcePackFor(TailsPlatform.MOD_ID);
		if (pack == null) return; // Account for mods like Angelica loading vanilla resources early.

		try (InputStream is = pack.getInputStream(loc); InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr)) {
			final JsonElement elem = new JsonParser().parse(br);
			loadModernLangFile(elem, properties);
		} catch (Exception e) {
			TailsPlatform.get().logError("Exception reading resource {}:", loc, e);
		}
	}

	private static void loadModernLangFile(JsonElement elem, Map<String, String> properties) {
		final JsonObject obj = elem.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : obj.entrySet())
			properties.put(entry.getKey(), entry.getValue().getAsString());
	}
}