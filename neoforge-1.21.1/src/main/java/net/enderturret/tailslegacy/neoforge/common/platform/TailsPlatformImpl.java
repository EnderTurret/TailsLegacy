/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.common.platform;

import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.neoforged.fml.loading.FMLEnvironment;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.neoforge.common.TailsLegacy;

public final class TailsPlatformImpl implements TailsPlatform {

	@Override
	public boolean isDevEnvironment() {
		return !FMLEnvironment.production;
	}

	@Override
	public void logDebug(String msg, Object... args) { TailsLegacy.LOGGER.debug(msg, args); }

	@Override
	public void logInfo(String msg) { TailsLegacy.LOGGER.info(msg); }

	@Override
	public void logInfo(String msg, Object arg1) { TailsLegacy.LOGGER.info(msg, arg1); }

	@Override
	public void logInfo(String msg, Object arg1, Object arg2) { TailsLegacy.LOGGER.info(msg, arg1, arg2); }

	@Override
	public void logInfo(String msg, Object arg1, Object arg2, Object arg3) { TailsLegacy.LOGGER.info(msg, arg1, arg2, arg3); }

	@Override
	public void logError(String msg, Object... args) { TailsLegacy.LOGGER.error(msg, args); }

	@Override
	public TResourceLocation newResourceLocation(String path) {
		return (TResourceLocation) (Object) ResourceLocation.fromNamespaceAndPath(TailsPlatform.MOD_ID, path);
	}

	@Override
	public TResourceLocation parseResourceLocation(String rl) {
		return (TResourceLocation) (Object) ResourceLocation.parse(rl);
	}

	@Override
	public UUID randomUUID() {
		return Mth.createInsecureUUID();
	}

	@Override
	public float lookupSin(float angle) {
		return Mth.sin(angle);
	}

	@Override
	public float lookupCos(float angle) {
		return Mth.cos(angle);
	}
}