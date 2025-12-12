/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.gameevent.InputEvent;
import uk.kihira.tails.common.TailsLanguage;
import uk.kihira.tails.forge.client.platform.TailsClientPlatformImpl;
import uk.kihira.tails.forge.common.Tails;

public final class TailsKeybinds {

	public static final KeyBinding RELOAD_PARTS = new KeyBinding(TailsLanguage.RELOAD_PARTS_KEY, 0, TailsLanguage.KEY_CATEGORY);

	static void registerKeys() {
		ClientRegistry.registerKeyBinding(RELOAD_PARTS);
	}

	static void onKeyPressed(InputEvent.KeyInputEvent e) {
		if (RELOAD_PARTS.isPressed()) {
			Tails.LOGGER.info("Reloading all parts!");
			TailsClientPlatformImpl.reloadParts(Minecraft.getMinecraft().getResourceManager());
		}
	}
}