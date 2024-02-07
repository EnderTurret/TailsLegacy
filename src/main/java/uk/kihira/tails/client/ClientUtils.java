/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;

/**
 * Various client-side utilities.
 */
public final class ClientUtils {

	/**
	 * @return The {@link UUID} of the local player.
	 */
	public static UUID getPlayerUUID() {
		final Minecraft mc = Minecraft.getInstance();
		/*if (mc.player != null && mc.player.getUniqueID() != null)
			return mc.player.getUniqueID();*/
		return UUIDUtil.getOrCreatePlayerUUID(mc.getUser().getGameProfile());
	}
}