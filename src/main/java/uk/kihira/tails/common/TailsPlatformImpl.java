package uk.kihira.tails.common;

import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.client.duck.TResourceLocation;

public final class TailsPlatformImpl implements TailsPlatform {

	@Override
	public void logInfo(String msg) { Tails.LOGGER.info(msg); }

	@Override
	public void logInfo(String msg, Object arg1) { Tails.LOGGER.info(msg, arg1); }

	@Override
	public void logInfo(String msg, Object arg1, Object arg2) { Tails.LOGGER.info(msg, arg1, arg2); }

	@Override
	public void logError(String msg, Object... args) { Tails.LOGGER.error(msg, args); }

	@Override
	public TResourceLocation newResourceLocation(String path) {
		return (TResourceLocation) (Object) ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, path);
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