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
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
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
@EventBusSubscriber(modid = TailsPlatform.MOD_ID, value = Side.CLIENT)
public final class ClientEventHandler {

	public static void onPreInit() {
		TailsKeybinds.registerKeys();
		RenderHelperManager.registerRenderHelper(new PlayerRenderHelper());
		RenderHelperManager.registerRenderHelper(new FakeEntityRenderHelper());

		final IReloadableResourceManager _manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();

		_manager.registerReloadListener((ISelectiveResourceReloadListener) TailsClientPlatformImpl::reloadParts);
		_manager.registerReloadListener((ISelectiveResourceReloadListener) (manager, predicate) -> {
			if (predicate.test(VanillaResourceType.TEXTURES)) {
				maybeDestroyCursor();
				registerCursor(manager);
			}
		});

		if (Loader.isModLoaded("botania"))
			registerFoxtato(); // Try to avoid class loading it if Botania isn't present.
	}

	public static void onPostInit() {
		final Minecraft mc = Minecraft.getMinecraft();
		final Map<String, RenderPlayer> skinMap = mc.getRenderManager().getSkinMap();

		for (RenderPlayer renderer : skinMap.values()) {
			renderer.addLayer(new PartLayer<>(renderer));

			final List<LayerRenderer<?>> layers = ((LivingEntityRendererAccess) renderer).tails$layers();
			for (int i = 0; i < layers.size(); i++)
				// If other mods do this exact same thing, let them take precedence.
				// If it's just an ArrowLayer mixin, then sucks for them.
				if (layers.get(i).getClass() == LayerArrow.class) {
					layers.set(i, new TailsArrowLayer(renderer));
					break;
				}
		}
	}

	private static void maybeDestroyCursor() {
		if (TintPanel.pickerCursorHandle != null)
			TintPanel.pickerCursorHandle.destroy();
	}

	private static void registerCursor(IResourceManager manager) {
		final BufferedImage iconImg;

		try (IResource resource = manager.getResource(IconButton.ICONS_TEXTURE); InputStream is = resource.getInputStream()) {
			iconImg = TextureUtil.readBufferedImage(is);
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
	@SubscribeEvent
	static void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiIngameMenu)
			event.getButtonList().add(new GuiButtonExt(EDITOR_ID, event.getGui().width / 2 - 35, event.getGui().height - 25, 70, 20,
					TailsComponents.EDITOR_BUTTON.getFormattedText()));
	}

	@SubscribeEvent
	static void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre e) {
		if (e.getGui() instanceof GuiIngameMenu && e.getButton().id == EDITOR_ID) {
			Minecraft.getMinecraft().displayGuiScreen(EditorScreen.openDefault());
			e.setCanceled(true);
		}
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