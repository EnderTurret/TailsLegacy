/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.proxy.CommonProxy;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Tails.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

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
		if (Tails.localPartsData != null)
			Tails.PROXY.addPartsData(ClientUtils.getPlayerUUID(), Tails.localPartsData);
	}

	@SubscribeEvent
	static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut e) {
		// TODO: Do we need to defer these?
		sentPartInfoToServer = false;
		clearAllPartInfo = true;

		Tails.reloadConfig(null);
	}

	@SubscribeEvent
	static void onClientTick(TickEvent.ClientTickEvent e) {
		if (e.phase == TickEvent.Phase.START)
			if (clearAllPartInfo) {
				Tails.PROXY.clearAllPartsData();
				clearAllPartInfo = false;
			}
			// World can't be null if we want to send a packet it seems.
			else if (!sentPartInfoToServer && Minecraft.getInstance().level != null) {
				Tails.CHANNEL.sendToServer(new PlayerDataMessage(ClientUtils.getPlayerUUID(), Tails.localPartsData));

				if (CommonProxy.sync != null)
					CommonProxy.sync.upload(ClientUtils.getPlayerUUID(), Tails.localPartsData);

				sentPartInfoToServer = true;
			}
	}

	/*@SubscribeEvent
	static void onRenderWorldLast(RenderLevelStageEvent e) {
		if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)
			return;

		final Player player = Minecraft.getInstance().player;
		if (player == null) return;

		final PoseStack poseStack = e.getPoseStack();

		final Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

		poseStack.pushPose();
		poseStack.translate(-vec.x, -vec.y, -vec.z);

		poseStack.pushPose();
		poseStack.translate(1, 0, 1);

		renderDebugPlayer(player, poseStack, e.getPartialTick());

		poseStack.popPose();

		poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));

		renderDebugPlayer(player, poseStack, e.getPartialTick());

		poseStack.popPose();
	}

	private static void renderDebugPlayer(Player player, PoseStack poseStack, float partialTick) {
		final EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		final MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

		try {
			renderDispatcher.setRenderShadow(false);

			RenderSystem.runAsFancy(() -> {
				renderDispatcher.render(player, 0.5, 5, -0.5, 0f, partialTick, poseStack, buffers, 15728880);
			});

			buffers.endBatch();

			renderDispatcher.setRenderShadow(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/
}
