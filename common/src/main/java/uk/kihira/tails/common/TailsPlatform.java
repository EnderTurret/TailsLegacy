package uk.kihira.tails.common;

import java.util.ServiceLoader;
import java.util.UUID;

import com.google.gson.Gson;

import uk.kihira.tails.common.api.IPlayerPartManager;
import uk.kihira.tails.common.client.duck.TResourceLocation;

public interface TailsPlatform {

	public static TailsPlatform get() {
		if (TailsInternal.platform == null)
			TailsInternal.platform = ServiceLoader.load(TailsPlatform.class)
					.iterator().next();

		return TailsInternal.platform;
	}

	public void logDebug(String msg, Object... args);
	public void logInfo(String msg);
	public void logInfo(String msg, Object arg1);
	public void logInfo(String msg, Object arg1, Object arg2);
	public void logInfo(String msg, Object arg1, Object arg2, Object arg3);
	public void logError(String msg, Object... args);

	public TResourceLocation newResourceLocation(String path);
	public TResourceLocation parseResourceLocation(String rl);

	public UUID randomUUID();

	public float lookupSin(float angle);
	public float lookupCos(float angle);

	public IPlayerPartManager getPartManager();
	public Gson getSidedGson();
}