/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.proxy.CommonProxy;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

	private boolean sentPartInfoToServer = false;
	private boolean clearAllPartInfo = false;

	/*
	 * Tails Editor Button
	 */
	@SubscribeEvent
	public void onScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof PauseScreen)
			event.addWidget(new Button(event.getGui().width / 2 - 35, event.getGui().height - 25, 70, 20, new TranslatableComponent("tails.gui.button.editor"), b -> {
				Minecraft.getInstance().setScreen(new EditorScreen());
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
	public void onClientTick(TickEvent.ClientTickEvent e) {
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
	public void onRenderWorldLast(RenderWorldLastEvent e) {
		final PlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;

		final MatrixStack matrixStack = e.getMatrixStack();

		final Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

		matrixStack.push();
		matrixStack.translate(-vec.x, -vec.y, -vec.z);

		matrixStack.push();
		matrixStack.translate(1, 0, 1);

		renderDebugPlayer(player, matrixStack, e.getPartialTicks());

		matrixStack.pop();

		matrixStack.rotate(Vector3f.YP.rotationDegrees(180F));

		renderDebugPlayer(player, matrixStack, e.getPartialTicks());

		matrixStack.pop();
	}

	private static void renderDebugPlayer(PlayerEntity player, MatrixStack matrixStack, float partialTicks) {
		final EntityRendererManager rendererManager = Minecraft.getInstance().getRenderManager();
		final IRenderTypeBuffer.Impl impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

		try {
			rendererManager.setRenderShadow(false);

			RenderSystem.runAsFancy(() -> {
				rendererManager.renderEntityStatic(player, 0.5, 5, -0.5, 0f, partialTicks, matrixStack, impl, 15728880);
			});

			impl.finish();

			rendererManager.setRenderShadow(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/
}
