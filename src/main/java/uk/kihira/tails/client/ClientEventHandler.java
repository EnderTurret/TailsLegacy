/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

	private boolean sentPartInfoToServer = false;
	private boolean clearAllPartInfo = false;

	/*
	 * Tails Editor Button
	 */
	@SubscribeEvent
	public void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof IngameMenuScreen)
			event.addWidget(new Button(event.getGui().width / 2 - 35, event.getGui().height - 25, 70, 20, new TranslationTextComponent("tails.gui.button.editor"), b -> {
				Minecraft.getInstance().displayGuiScreen(new EditorScreen());
			}));
	}

	/*
	 * Tails Syncing
	 */
	@SubscribeEvent
	public void onConnectToServer(PlayerEvent.PlayerLoggedInEvent event) {
		// Add local player texture to map.
		if (Tails.localPartsData != null)
			Tails.PROXY.addPartsData(ClientUtils.getPlayerUUID(), Tails.localPartsData);
	}

	@SubscribeEvent
	public void onDisconnect(PlayerEvent.PlayerLoggedOutEvent e) {
		Tails.hasRemote = false;
		sentPartInfoToServer = false;
		clearAllPartInfo = true;

		Tails.loadConfig(null);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.phase == TickEvent.Phase.START)
			if (TextureHelper.needsBuild(e.player) && e.player instanceof AbstractClientPlayerEntity)
				TextureHelper.buildPlayerPartsData((AbstractClientPlayerEntity) e.player);
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent e) {
		if (e.phase == TickEvent.Phase.START)
			if (clearAllPartInfo) {
				Tails.PROXY.clearAllPartsData();
				clearAllPartInfo = false;
			}
			// World can't be null if we want to send a packet it seems.
			else if (!sentPartInfoToServer && Minecraft.getInstance().world != null) {
				Tails.CHANNEL.sendToServer(new PlayerDataMessage(ClientUtils.getPlayerUUID(), Tails.localPartsData));
				sentPartInfoToServer = true;
			}
	}
}
