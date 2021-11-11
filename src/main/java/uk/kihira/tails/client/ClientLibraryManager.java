package uk.kihira.tails.client;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.LibraryEntriesMessage;

public class ClientLibraryManager extends LibraryManager {

	@Override
	public void addEntries(List<? extends LibraryEntryData> entries) {
		super.addEntries(entries);
		final Screen guiScreen = Minecraft.getInstance().currentScreen;

		if (guiScreen instanceof EditorScreen) {
			final EditorScreen editor = (EditorScreen) guiScreen;
			if (editor.getLibraryPanel() != null && editor.getLibraryInfoPanel() != null)
				((EditorScreen) guiScreen).getLibraryPanel().initList();
			((EditorScreen) guiScreen).getLibraryInfoPanel().setEntry(null);
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