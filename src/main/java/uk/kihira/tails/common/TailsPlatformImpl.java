package uk.kihira.tails.common;

import net.minecraft.resources.ResourceLocation;

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
	public void logError(String msg) { Tails.LOGGER.error(msg); }

	@Override
	public TResourceLocation newResourceLocation(String path) {
		return (TResourceLocation) (Object) ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, path);
	}

	@Override
	public TResourceLocation parseResourceLocation(String rl) {
		return (TResourceLocation) (Object) ResourceLocation.parse(rl);
	}
}