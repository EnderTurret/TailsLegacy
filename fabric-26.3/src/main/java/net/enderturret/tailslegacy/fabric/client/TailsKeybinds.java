/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.fabric.client.platform.TailsClientPlatformImpl;
import net.enderturret.tailslegacy.fabric.common.TailsLegacy;

public final class TailsKeybinds {

	public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "category"));
	public static final KeyMapping RELOAD_PARTS = new KeyMapping(TailsLanguage.RELOAD_PARTS_KEY, InputConstants.UNKNOWN.getValue(), CATEGORY);

	static void registerKeys() {
		KeyMappingHelper.registerKeyMapping(RELOAD_PARTS);
	}

	static void checkKeys() {
		if (RELOAD_PARTS.consumeClick()) {
			TailsLegacy.LOGGER.info("Reloading all parts!");
			TailsClientPlatformImpl.reloadParts(Minecraft.getInstance().getResourceManager());
		}
	}
}