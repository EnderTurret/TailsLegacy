/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.components.Button;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.TailsComponents;

@Internal
public final class ControlsPanel extends Panel {

	private boolean libraryMode = false;

	public ControlsPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		// Mode Switch
		addRenderableWidget(new ExtendedButton(left + 3, bottom - 25, 46, 20, TailsComponents.LIBRARY_MODE, this::switchMode));
		// Reset/Save
		addRenderableWidget(new ExtendedButton(left + (right - left) / 2 - 23, bottom - 25, 46, 20, TailsComponents.RESET_BUTTON, this::reset));
		addRenderableWidget(new ExtendedButton(right - 49, bottom - 25, 46, 20, TailsComponents.DONE_BUTTON, b -> parent.close()));

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