/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.gson;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import uk.kihira.tails.common.TailsPlatform;

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
		TailsPlatform.get().logError("Attempting to reflectively (de)serialize field {} in class {}!",
				f.getName(), f.getDeclaringClass().getName(), new Throwable("stacktrace"));
		return false;
	}
}