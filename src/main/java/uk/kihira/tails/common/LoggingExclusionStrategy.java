package uk.kihira.tails.common;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * <p>An {@link ExclusionStrategy} that logs a message when used.
 * This is intended to catch any surprise uses of Gson reflection.</p>
 * <p>Ideally this would be a {@code ReflectionAccessFilter}, but unfortunately this version of Gson is too old.</p>
 * @author EnderTurret
 */
@Internal
public final class LoggingExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		if (f.getDeclaringClass() == LibraryEntryData.class) return false;

		Tails.LOGGER.warn("Attempting to reflectively (de)serialize field {} in class {}!", f.getName(), f.getDeclaringClass().getName(), new Throwable("stacktrace"));

		return false;
	}
}