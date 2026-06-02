package test.platform;

import java.util.UUID;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

public final class SimpleTailsPlatformImpl implements TailsPlatform {

	// "We have Log4J at home."
	// The Log4J at home:
	@Override
	public void logDebug(String msg, Object... args) {
		for (int i = 0; i < args.length; i++) args[i] = String.valueOf(args[i]);
		System.out.printf("[00xxx0000 00:00:00.000] [main/DEBUG] [Tails Legacy/]: " + msg.replace("{}", "%s") + "\n", args);
	}

	@Override
	public void logInfo(String msg) {
		System.out.println("[00xxx0000 00:00:00.000] [main/INFO] [Tails Legacy/]: " + msg);
	}

	@Override
	public void logInfo(String msg, Object arg1) {
		System.out.printf("[00xxx0000 00:00:00.000] [main/INFO] [Tails Legacy/]: " + msg.replace("{}", "%s") + "\n", String.valueOf(arg1));
	}

	@Override
	public void logInfo(String msg, Object arg1, Object arg2) {
		System.out.printf("[00xxx0000 00:00:00.000] [main/INFO] [Tails Legacy/]: " + msg.replace("{}", "%s") + "\n", String.valueOf(arg1), String.valueOf(arg2));
	}

	@Override
	public void logInfo(String msg, Object arg1, Object arg2, Object arg3) {
		System.out.printf("[00xxx0000 00:00:00.000] [main/INFO] [Tails Legacy/]: " + msg.replace("{}", "%s") + "\n", String.valueOf(arg1), String.valueOf(arg2), String.valueOf(arg3));
	}

	@Override
	public void logError(String msg, Object... args) {
		for (int i = 0; i < args.length; i++) args[i] = String.valueOf(args[i]);
		System.err.printf("[00xxx0000 00:00:00.000] [main/ERROR] [Tails Legacy/]: " + msg.replace("{}", "%s") + "\n", args);
	}

	@Override
	public TResourceLocation newResourceLocation(String path) {
		return new SimpleResourceLocation(TailsPlatform.MOD_ID, path);
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
		return (float) Math.sin(angle);
	}

	@Override
	public float lookupCos(float angle) {
		return (float) Math.cos(angle);
	}
}