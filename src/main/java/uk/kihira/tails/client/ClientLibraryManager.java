/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.common2.LibraryEntryData;
import uk.kihira.tails.common2.LibraryManager;
import uk.kihira.tails.common2.gson.GsonLibraryManager;

/**
 * The client-side implementation of the {@link LibraryManager}.
 * @author EnderTurret
 */
@Internal
public final class ClientLibraryManager extends GsonLibraryManager {

	@Override
	protected Gson getGson() {
		return LocalPartManager.GSON;
	}

	@Override
	public void addEntries(List<? extends LibraryEntryData> entries) {
		super.addEntries(entries);

		final Screen screen = Minecraft.getInstance().screen;

		if (screen instanceof EditorScreen editor) {
			if (editor.getLibraryPanel() != null && editor.getLibraryInfoPanel() != null)
				editor.getLibraryPanel().initList();

			editor.getLibraryInfoPanel().setEntry(null);
		}
	}
}