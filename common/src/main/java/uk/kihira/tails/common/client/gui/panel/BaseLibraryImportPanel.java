package uk.kihira.tails.common.client.gui.panel;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.TailsLanguage;
import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.LocalPartManager;
import uk.kihira.tails.common.part.PartsData;

public interface BaseLibraryImportPanel {

	static final Pattern PATTERN = Pattern.compile("(^.*):([0-9a-f\\-]+):(\\{.+\\})$");

	public default void importFromString(String input) {
		final Matcher m = PATTERN.matcher(input);

		if (!m.matches()) {
			toast(TailsLanguage.IMPORT_INVALID_MESSAGE, null, true);
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
			TailsPlatform.get().logError("Exception parsing import UUID \"{}\":", rawCreatorId, e);
			return;
		}

		final ClientPartsData partData;

		try {
			partData = (ClientPartsData) LocalPartManager.GSON.fromJson(json, PartsData.class);
		} catch (Exception e) {
			toast(TailsLanguage.IMPORT_INVALID_PARTS_MESSAGE, null, true);
			TailsPlatform.get().logError("Exception parsing import part data:", e);
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