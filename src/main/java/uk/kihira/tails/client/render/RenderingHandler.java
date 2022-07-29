/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import java.util.UUID;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartsData;

public class RenderingHandler {

	public static RenderPlayerEvent.Pre currentEvent = null;
	public static PartsData currentPartsData = null;
	public static ResourceLocation currentPlayerTexture = null;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerRenderTick(RenderPlayerEvent.Pre e) {
		final UUID uuid = e.getEntity().getGameProfile().getId();
		if (Tails.PROXY.hasPartsData(uuid) && !e.getEntity().isInvisible()) {
			currentPartsData = Tails.PROXY.getPartsData(uuid);
			currentPlayerTexture = ((AbstractClientPlayer) e.getEntity()).getSkinTextureLocation();
			currentEvent = e;
		}
	}

	@SubscribeEvent
	public void onPlayerRenderTickPost(RenderPlayerEvent.Post e) {
		// Reset to null after rendering the current tail.
		currentPartsData = null;
		currentPlayerTexture = null;
		currentEvent = null;
	}
}
