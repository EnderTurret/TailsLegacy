/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.gui.panel;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.google.gson.stream.MalformedJsonException;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.part.LocalPartManager;
import net.enderturret.tailslegacy.common.part.PartsData;

public interface BaseLibraryImportPanel {

	static final Pattern PATTERN = Pattern.compile("(^.*):([0-9a-f\\-]+):(\\{.+\\})$");

	public default void importFromString(String input) {
		final Matcher m = PATTERN.matcher(input);

		TailsPlatform.get().logInfo("Importing library entry: {}", input);

		if (!m.matches()) {
			toast(TailsLanguage.IMPORT_INVALID_MESSAGE, null, true);
			TailsPlatform.get().logError("Library entry doesn't match expected pattern!");
			return;
		}

		final String name = m.group(1);
		final String rawCreatorId = m.group(2);
		final String json = m.group(3);

		final UUID creatorId;

		try {
			creatorId = UUID.fromString(rawCreatorId);
		} catch (Exception e) {
			toast(TailsLanguage.IMPORT_INVALID_UUID_MESSAGE, null, true);
			TailsPlatform.get().logError("Exception parsing import UUID \"{}\":\n{}", rawCreatorId, e.toString());
			return;
		}

		final ClientPartsData partData;

		try {
			partData = (ClientPartsData) LocalPartManager.GSON.fromJson(json, PartsData.class);
		} catch (Exception e) {
			toast(TailsLanguage.IMPORT_INVALID_PARTS_MESSAGE, null, true);

			if (e.getCause() instanceof MalformedJsonException)
				TailsPlatform.get().logError("Exception parsing import part data: {}\n{}", json, e.getCause().toString());
			else
				TailsPlatform.get().logError("Exception parsing import part data: {}", json, e);

			return;
		}

		final LibraryEntryData entry = new LibraryEntryData(creatorId, TailsClientPlatform.get().fetchUsername(creatorId), name, partData);
		TailsClientPlatform.get().getLibraryManager().addEntry(entry);

		importPartsData(entry);

		toast(TailsLanguage.IMPORT_SUCCESS, name, false);
	}

	public void importPartsData(LibraryEntryData entry);

	public void toast(String langKey, @Nullable String name, boolean error);
}