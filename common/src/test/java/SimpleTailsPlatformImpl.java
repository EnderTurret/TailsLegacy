import java.util.UUID;

import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.api.IPlayerPartManager;
import uk.kihira.tails.common2.client.duck.TResourceLocation;

public final class SimpleTailsPlatformImpl implements TailsPlatform {

	@Override
	public void logDebug(String msg, Object... args) {
		System.out.println(msg);
	}

	@Override
	public void logInfo(String msg) {
		System.out.println(msg);
	}

	@Override
	public void logInfo(String msg, Object arg1) {
		System.out.println(msg);
	}

	@Override
	public void logInfo(String msg, Object arg1, Object arg2) {
		System.out.println(msg);
	}

	@Override
	public void logInfo(String msg, Object arg1, Object arg2, Object arg3) {
		System.out.println(msg);
	}

	@Override
	public void logError(String msg, Object... args) {
		System.err.println(msg);
	}

	@Override
	public TResourceLocation newResourceLocation(String path) {
		return new SimpleResourceLocation("tails", path);
	}

	@Override
	public TResourceLocation parseResourceLocation(String rl) {
		return SimpleResourceLocation.parse(rl);
	}

	@Override
	public UUID randomUUID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public float lookupSin(float angle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float lookupCos(float angle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPlayerPartManager getPartManager() {
		throw new UnsupportedOperationException();
	}
}