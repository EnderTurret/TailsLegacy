/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.fabric.client.platform.TailsClientPlatformImpl;
import net.enderturret.tailslegacy.fabric.common.TailsLegacy;

public final class TailsKeybinds {

	public static final KeyMapping RELOAD_PARTS = new KeyMapping(TailsLanguage.RELOAD_PARTS_KEY, -1, TailsLanguage.KEY_CATEGORY);

	static void registerKeys() {
		KeyBindingHelper.registerKeyBinding(RELOAD_PARTS);
	}

	static void checkKeys() {
		if (RELOAD_PARTS.consumeClick()) {
			TailsLegacy.LOGGER.info("Reloading all parts!");
			TailsClientPlatformImpl.reloadParts(Minecraft.getInstance().getResourceManager());
		}
	}
}