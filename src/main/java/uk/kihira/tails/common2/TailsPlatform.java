package uk.kihira.tails.common2;

import java.util.ServiceLoader;
import java.util.UUID;

import uk.kihira.tails.common2.client.duck.TResourceLocation;

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
	public void logError(String msg, Object... args);

	public TResourceLocation newResourceLocation(String path);
	public TResourceLocation parseResourceLocation(String rl);

	public UUID randomUUID();
}