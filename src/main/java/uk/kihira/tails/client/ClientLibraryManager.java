/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.LibraryEntriesMessage;

public class ClientLibraryManager extends LibraryManager {

	@Override
	public void addEntries(List<? extends LibraryEntryData> entries) {
		super.addEntries(entries);

		final Screen screen = Minecraft.getInstance().screen;

		if (screen instanceof EditorScreen) {
			final EditorScreen editor = (EditorScreen) screen;

			if (editor.getLibraryPanel() != null && editor.getLibraryInfoPanel() != null)
				editor.getLibraryPanel().initList();

			editor.getLibraryInfoPanel().setEntry(null);
		}
	}

	@Override
	public void removeEntry(final LibraryEntryData data) {
		if (data.remoteEntry)
			Tails.CHANNEL.sendToServer(new LibraryEntriesMessage(Collections.singletonList(data), true));
		else
			super.removeEntry(data);
	}
}