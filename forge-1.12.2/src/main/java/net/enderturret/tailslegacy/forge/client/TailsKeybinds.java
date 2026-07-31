/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.forge.client.platform.TailsClientPlatformImpl;
import net.enderturret.tailslegacy.forge.common.TailsLegacy;

public final class TailsKeybinds {

	public static final KeyBinding RELOAD_PARTS = new KeyBinding(TailsLanguage.RELOAD_PARTS_KEY, 0, TailsLanguage.KEY_CATEGORY);

	static void registerKeys() {
		ClientRegistry.registerKeyBinding(RELOAD_PARTS);
	}

	static void onKeyPressed(InputEvent.KeyInputEvent e) {
		if (RELOAD_PARTS.isPressed()) {
			TailsLegacy.LOGGER.info("Reloading all parts!");
			TailsClientPlatformImpl.reloadParts(Minecraft.getMinecraft().getResourceManager(), null);
		}
	}
}