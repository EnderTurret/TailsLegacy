/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 EnderTurret
 *
 * See LICENSE for full License
 */

package test.platform;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;
import net.enderturret.tailslegacy.common.gson.ResourceManagerWrapper;

public final class SimpleResourceManager implements ResourceManagerWrapper {

	@Override
	public JsonElement getJson(TResourceLocation path) {
		final Path p = Paths.get("src", "main", "resources", "assets", path.t$getNamespace(), path.t$getPath());
		try {
			return new JsonParser().parse(String.join("\n", Files.readAllLines(p)));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public Map<TResourceLocation, JsonElement> listJsonFiles(String prefix, Predicate<TResourceLocation> filter) {
		final Path p = Paths.get("src", "main", "resources", "assets", "tailslegacy", prefix);

		final Map<TResourceLocation, JsonElement> ret = new HashMap<>();

		try (Stream<Path> stream = Files.walk(p)) {
			stream.filter(Files::isRegularFile).forEach(path -> {
				String name = path.toString().substring("src/main/resources/assets/tailslegacy/".length());
				final TResourceLocation loc = TailsPlatform.get().newResourceLocation(name);
				if (filter.test(loc))
					ret.put(loc, getJson(loc));
			});
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		return ret;
	}
}