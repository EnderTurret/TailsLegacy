/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.common;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;

import net.enderturret.tailslegacy.common.TailsPlatform;

/**
 * The Tails config, for all your configuration needs.
 * Configuration sold separately.
 * @author EnderTurret
 */
@Internal
public final class TailsConfig {

	@Internal
	public static final TailsConfig CLIENT_INSTANCE = new TailsConfig();

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	@Internal
	public String localPlayerData = "";
	@Internal
	public boolean hidePreviewInThirdPerson = true;

	private TailsConfig() {}

	public void load() {
		final Path path = FabricLoader.getInstance().getConfigDir().resolve("tailslegacy-client.json");
		try {
			final String rawJson = Files.readString(path);
			final JsonElement json = JsonParser.parseString(rawJson);
			final JsonObject obj = json.getAsJsonObject();

			localPlayerData = obj.has("localPlayerData") ? obj.get("localPlayerData").getAsString() : "";
			hidePreviewInThirdPerson = !obj.has("hidePreviewInThirdPerson") || obj.get("hidePreviewInThirdPerson").getAsBoolean();
		} catch (NoSuchFileException e) {
			return;
		} catch (Exception e) {
			TailsPlatform.get().logError("Exception reading config file:", e);
		}
	}

	public void save() {
		final Path path = FabricLoader.getInstance().getConfigDir().resolve("tailslegacy-client.json");

		final JsonObject cfg = new JsonObject();
		cfg.addProperty("localPlayerData", localPlayerData);
		cfg.addProperty("hidePreviewInThirdPerson", hidePreviewInThirdPerson);

		final String json = GSON.toJson(cfg);

		try {
			Files.writeString(path, json, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (Exception e) {
			TailsPlatform.get().logError("Exception writing config file:", e);
		}
	}
}