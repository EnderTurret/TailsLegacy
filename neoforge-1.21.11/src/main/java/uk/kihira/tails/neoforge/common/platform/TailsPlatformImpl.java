/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.common.platform;

import java.util.UUID;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.duck.TResourceLocation;
import uk.kihira.tails.neoforge.common.Tails;

public final class TailsPlatformImpl implements TailsPlatform {

	@Override
	public void logDebug(String msg, Object... args) { Tails.LOGGER.debug(msg, args); }

	@Override
	public void logInfo(String msg) { Tails.LOGGER.info(msg); }

	@Override
	public void logInfo(String msg, Object arg1) { Tails.LOGGER.info(msg, arg1); }

	@Override
	public void logInfo(String msg, Object arg1, Object arg2) { Tails.LOGGER.info(msg, arg1, arg2); }

	@Override
	public void logInfo(String msg, Object arg1, Object arg2, Object arg3) { Tails.LOGGER.info(msg, arg1, arg2, arg3); }

	@Override
	public void logError(String msg, Object... args) { Tails.LOGGER.error(msg, args); }

	@Override
	public TResourceLocation newResourceLocation(String path) {
		return (TResourceLocation) (Object) Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, path);
	}

	@Override
	public TResourceLocation parseResourceLocation(String rl) {
		return (TResourceLocation) (Object) Identifier.parse(rl);
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