import java.util.UUID;

import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.api.IPlayerPartManager;
import uk.kihira.tails.common2.client.duck.TResourceLocation;

public final class SimpleTailsPlatformImpl implements TailsPlatform {

	// "We have Log4J at home."
	// The Log4J at home:
	@Override
	public void logDebug(String msg, Object... args) {
		for (int i = 0; i < args.length; i++) args[i] = String.valueOf(args[i]);
		System.out.printf("[00xxx0000 00:00:00.000] [main/DEBUG] [Tails/]: " + msg.replace("{}", "%s") + "\n", args);
	}

	@Override
	public void logInfo(String msg) {
		System.out.println("[00xxx0000 00:00:00.000] [main/INFO] [Tails/]: " + msg);
	}

	@Override
	public void logInfo(String msg, Object arg1) {
		System.out.printf("[00xxx0000 00:00:00.000] [main/INFO] [Tails/]: " + msg.replace("{}", "%s") + "\n", String.valueOf(arg1));
	}

	@Override
	public void logInfo(String msg, Object arg1, Object arg2) {
		System.out.printf("[00xxx0000 00:00:00.000] [main/INFO] [Tails/]: " + msg.replace("{}", "%s") + "\n", String.valueOf(arg1), String.valueOf(arg2));
	}

	@Override
	public void logInfo(String msg, Object arg1, Object arg2, Object arg3) {
		System.out.printf("[00xxx0000 00:00:00.000] [main/INFO] [Tails/]: " + msg.replace("{}", "%s") + "\n", String.valueOf(arg1), String.valueOf(arg2), String.valueOf(arg3));
	}

	@Override
	public void logError(String msg, Object... args) {
		for (int i = 0; i < args.length; i++) args[i] = String.valueOf(args[i]);
		System.err.printf("[00xxx0000 00:00:00.000] [main/ERROR] [Tails/]: " + msg.replace("{}", "%s") + "\n", args);
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