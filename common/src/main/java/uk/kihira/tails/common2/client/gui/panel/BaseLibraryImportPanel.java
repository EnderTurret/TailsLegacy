package uk.kihira.tails.common2.client.gui.panel;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import uk.kihira.tails.common2.LibraryEntryData;
import uk.kihira.tails.common2.TailsPlatform;
import uk.kihira.tails.common2.client.TailsClientPlatform;
import uk.kihira.tails.common2.client.part.ClientPartsData;
import uk.kihira.tails.common2.client.part.LocalPartManager;
import uk.kihira.tails.common2.part.PartsData;

public interface BaseLibraryImportPanel {

	static final Pattern PATTERN = Pattern.compile("(^.*):([0-9a-f\\-]+):(\\{.+\\})$");

	public default void importFromString(String input) {
		final Matcher m = PATTERN.matcher(input);

		if (!m.matches()) {
			toast("tails.gui.library.import.toast.invalid", null, true);
			return;
		}

		final String name = m.group(1);
		final String rawCreatorId = m.group(2);
		final String json = m.group(3);

		final UUID creatorId;

		try {
			creatorId = UUID.fromString(rawCreatorId);
		} catch (Exception e) {
			toast("tails.gui.library.import.toast.invalid.uuid", null, true);
			TailsPlatform.get().logError("Exception parsing import UUID \"{}\":", rawCreatorId, e);
			return;
		}

		final ClientPartsData partData;

		try {
			partData = (ClientPartsData) LocalPartManager.GSON.fromJson(json, PartsData.class);
		} catch (Exception e) {
			toast("tails.gui.library.import.toast.invalid.parts", null, true);
			TailsPlatform.get().logError("Exception parsing import part data:", e);
			return;
		}

		final LibraryEntryData entry = new LibraryEntryData(creatorId, TailsClientPlatform.get().fetchUsername(creatorId), name, partData);
		TailsClientPlatform.get().getLibraryManager().addEntry(entry);

		importPartsData(entry);

		toast("tails.gui.library.import.toast.success", name, false);
	}

	public void importPartsData(LibraryEntryData entry);

	public void toast(String langKey, @Nullable String name, boolean error);
}