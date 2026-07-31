/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;

import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.forge.client.platform.TailsClientPlatformImpl;
import net.enderturret.tailslegacy.forge.common.TailsLegacy;

public final class TailsKeybinds {

	public static final KeyMapping RELOAD_PARTS = new KeyMapping(TailsLanguage.RELOAD_PARTS_KEY, -1, TailsLanguage.KEY_CATEGORY);

	static void registerKeys() {
		ClientRegistry.registerKeyBinding(RELOAD_PARTS);
	}

	static void onKeyPressed(InputEvent.KeyInputEvent e) {
		if (e.getAction() != InputConstants.PRESS) return;

		if (RELOAD_PARTS.consumeClick() || RELOAD_PARTS.matches(e.getKey(), e.getScanCode())) {
			TailsLegacy.LOGGER.info("Reloading all parts!");
			TailsClientPlatformImpl.reloadParts(Minecraft.getInstance().getResourceManager());
		}
	}
}