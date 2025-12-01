package uk.kihira.tails.common2;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;

import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.part.AttachmentPoint;
import uk.kihira.tails.common2.client.part.Part;
import uk.kihira.tails.common2.client.part.PartLoadingManager;

public interface TailsPlatform {

	public static TailsPlatform get() {
		if (TailsInternal.platform == null)
			TailsInternal.platform = ServiceLoader.load(TailsPlatform.class)
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("Couldn't find TailsPlatform implementation!"));

		return TailsInternal.platform;
	}

	public void logInfo(String msg);
	public void logInfo(String msg, Object arg1);
	public void logInfo(String msg, Object arg1, Object arg2);
	public void logError(String msg);

	public PartLoadingManager createPartLoadingManager(Runnable clear, BiConsumer<List<Part>, Map<AttachmentPoint, List<TResourceLocation>>> onComplete);

	public TResourceLocation newResourceLocation(String path);
	public TResourceLocation parseResourceLocation(String rl);
}