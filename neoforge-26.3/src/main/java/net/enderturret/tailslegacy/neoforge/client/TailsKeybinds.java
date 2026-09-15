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
import net.minecraft.resources.Identifier;

import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.neoforge.client.platform.TailsClientPlatformImpl;
import net.enderturret.tailslegacy.neoforge.common.TailsLegacy;

public final class TailsKeybinds {

	public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "category"));
	public static final KeyMapping RELOAD_PARTS = new KeyMapping(TailsLanguage.RELOAD_PARTS_KEY, InputConstants.UNKNOWN.getValue(), CATEGORY);

	static void registerKeys(RegisterKeyMappingsEvent e) {
		e.registerCategory(CATEGORY);
		e.register(RELOAD_PARTS);
	}

	static void onKeyPressed(InputEvent.Key e) {
		if (e.getAction() != InputConstants.PRESS) return;

		if (RELOAD_PARTS.consumeClick() || RELOAD_PARTS.matches(e.getKeyEvent())) {
			TailsLegacy.LOGGER.info("Reloading all parts!");
			TailsClientPlatformImpl.reloadParts(Minecraft.getInstance().getResourceManager());
		}
	}
}