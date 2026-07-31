/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.neoforge.client.platform.TailsClientPlatformImpl;
import net.enderturret.tailslegacy.neoforge.common.TailsLegacy;

public final class TailsKeybinds {

	public static final KeyMapping RELOAD_PARTS = new KeyMapping(TailsLanguage.RELOAD_PARTS_KEY, -1, TailsLanguage.KEY_CATEGORY);

	static void registerKeys(RegisterKeyMappingsEvent e) {
		e.register(RELOAD_PARTS);
	}

	static void onKeyPressed(InputEvent.Key e) {
		if (e.getAction() != InputConstants.PRESS) return;

		if (RELOAD_PARTS.consumeClick() || RELOAD_PARTS.matches(e.getKey(), e.getScanCode())) {
			TailsLegacy.LOGGER.info("Reloading all parts!");
			TailsClientPlatformImpl.reloadParts(Minecraft.getInstance().getResourceManager());
		}
	}
}