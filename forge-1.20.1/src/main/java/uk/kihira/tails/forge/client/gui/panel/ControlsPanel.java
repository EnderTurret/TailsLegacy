/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.components.Button;

import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.forge.client.gui.EditorScreen;
import uk.kihira.tails.forge.client.gui.TailsComponents;

@Internal
public final class ControlsPanel extends Panel {

	private boolean libraryMode = false;

	public ControlsPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		// Mode Switch
		addRenderableWidget(Button.builder(TailsComponents.LIBRARY_MODE, this::switchMode)
				.bounds(left + 3, bottom - 25, 46, 20)
				.build());
		// Reset/Save
		addRenderableWidget(Button.builder(TailsComponents.RESET_BUTTON, this::reset)
				.bounds(left + (right - left) / 2 - 23, bottom - 25, 46, 20)
				.build());
		addRenderableWidget(Button.builder(TailsComponents.DONE_BUTTON, b -> parent.close())
				.bounds(right - 49, bottom - 25, 46, 20)
				.build());

		parent.getPartPanel().setVisible(!libraryMode);
		parent.getTexturePanel().setVisible(!libraryMode);
		parent.getTintPanel().setVisible(!libraryMode);

		parent.getLibraryInfoPanel().setVisible(libraryMode);
		parent.getLibraryPanel().setVisible(libraryMode);
		parent.getLibraryImportPanel().setVisible(libraryMode);
	}

	private void switchMode(Button b) {
		libraryMode = !libraryMode;

		parent.getPartPanel().setVisible(!libraryMode);
		parent.getTexturePanel().setVisible(!libraryMode);
		parent.getTintPanel().setVisible(!libraryMode);

		parent.getLibraryInfoPanel().setVisible(libraryMode);
		parent.getLibraryPanel().setVisible(libraryMode);
		parent.getLibraryImportPanel().setVisible(libraryMode);

		parent.getPartPanel().selectDefaultListEntry();
		parent.getLibraryPanel().initList("");
		parent.getLibraryInfoPanel().setEntry(null);
		parent.getTintPanel().setEditingTint(0);

		if (!libraryMode)
			parent.getLibraryPanel().save();

		b.setMessage(libraryMode ? TailsComponents.EDITOR_MODE : TailsComponents.LIBRARY_MODE);
	}

	private void reset(Button b) {
		final ClientPartInfo partInfo = parent.getOriginalPartInfo().clone();
		parent.getPartPanel().selectDefaultListEntry();
		parent.getLibraryPanel().initList("");
		parent.getLibraryInfoPanel().setEntry(null);
		parent.getTintPanel().setEditingTint(0);
		parent.setPartsInfo(partInfo);
	}
}