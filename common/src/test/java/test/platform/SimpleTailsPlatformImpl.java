package test.platform;

import java.io.PrintStream;
import java.util.UUID;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

public final class SimpleTailsPlatformImpl implements TailsPlatform {

	// "We have Log4J at home."
	// The Log4J at home:
	@Override
	public void logDebug(String msg, Object... args) {
		log(System.out, "DEBUG", msg, args);
	}

	@Override
	public void logInfo(String msg) {
		log(System.out, "INFO", msg);
	}

	@Override
	public void logInfo(String msg, Object arg1) {
		log(System.out, "INFO", msg, arg1);
	}

	@Override
	public void logInfo(String msg, Object arg1, Object arg2) {
		log(System.out, "INFO", msg, arg1, arg2);
	}

	@Override
	public void logInfo(String msg, Object arg1, Object arg2, Object arg3) {
		log(System.out, "INFO", msg, arg1, arg2, arg3);
	}

	@Override
	public void logError(String msg, Object... args) {
		log(System.err, "ERROR", msg, args);
	}

	private static void log(PrintStream out, String level, String message, Object... args) {
		message = message.replace("{}", "%s");

		Throwable t = null;
		if (args.length > 0) {
			if (args[args.length - 1] instanceof Throwable)
				t = (Throwable) args[args.length - 1];

			for (int i = 0; i < args.length; i++) args[i] = String.valueOf(args[i]);
		}

		out.printf("[00xxx0000 00:00:00.000] [main/" + level + "] [Tails Legacy/]: " + message + "\n", args);

		if (t != null) t.printStackTrace(out);

		out.flush();
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