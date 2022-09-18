/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;

public final class ClientUtils {

	public static UUID getPlayerUUID() {
		final Minecraft mc = Minecraft.getInstance();
		/*if (mc.player != null && mc.player.getUniqueID() != null)
			return mc.player.getUniqueID();*/
		return UUIDUtil.getOrCreatePlayerUUID(mc.getUser().getGameProfile());
	}
}