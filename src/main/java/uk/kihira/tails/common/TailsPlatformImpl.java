package uk.kihira.tails.common;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.client.part.PartLoadingManagerImpl;
import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.part.AttachmentPoint;
import uk.kihira.tails.common2.client.part.Part;
import uk.kihira.tails.common2.client.part.PartLoadingManager;

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
	public PartLoadingManager createPartLoadingManager(Runnable clear, BiConsumer<List<Part>, Map<AttachmentPoint, List<TResourceLocation>>> onComplete) {
		return new PartLoadingManagerImpl(clear, onComplete);
	}

	@Override
	public TResourceLocation newResourceLocation(String path) {
		return (TResourceLocation) (Object) ResourceLocation.fromNamespaceAndPath(Tails.MOD_ID, path);
	}

	@Override
	public TResourceLocation parseResourceLocation(String rl) {
		return (TResourceLocation) (Object) ResourceLocation.parse(rl);
	}
}