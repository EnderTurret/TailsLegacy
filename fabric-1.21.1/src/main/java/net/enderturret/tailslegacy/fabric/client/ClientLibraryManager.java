/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.LibraryManager;
import net.enderturret.tailslegacy.common.client.part.LocalPartManager;
import net.enderturret.tailslegacy.common.gson.GsonLibraryManager;
import net.enderturret.tailslegacy.fabric.client.gui.EditorScreen;

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
				editor.getLibraryPanel().initList("");

			editor.getLibraryInfoPanel().setEntry(null);
		}
	}
}