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

import net.minecraft.client.gui.GuiButton;

import net.minecraftforge.fml.client.config.GuiButtonExt;

import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.forge.client.gui.EditorScreen;
import uk.kihira.tails.forge.client.gui.TailsComponents;

@Internal
public final class ControlsPanel extends Panel {

	public static final int MODE_SWITCH = 100;
	public static final int RESET = 101;
	public static final int SAVE = 102;

	private boolean libraryMode = false;

	private GuiButton modeSwitch;

	public ControlsPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		// Mode Switch
		modeSwitch = addRenderableWidget(new GuiButtonExt(MODE_SWITCH, left + 3, bottom - 25, 46, 20, TailsComponents.LIBRARY_MODE.getFormattedText()));
		// Reset/Save
		addRenderableWidget(new GuiButtonExt(RESET, left + (right - left) / 2 - 23, bottom - 25, 46, 20, TailsComponents.RESET_BUTTON.getFormattedText()));
		addRenderableWidget(new GuiButtonExt(SAVE, right - 49, bottom - 25, 46, 20, TailsComponents.DONE_BUTTON.getFormattedText()));

		parent.getPartPanel().setVisible(!libraryMode);
		parent.getTexturePanel().setVisible(!libraryMode);
		parent.getTintPanel().setVisible(!libraryMode);

		parent.getLibraryInfoPanel().setVisible(libraryMode);
		parent.getLibraryPanel().setVisible(libraryMode);
		parent.getLibraryImportPanel().setVisible(libraryMode);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case MODE_SWITCH:
				switchMode(); break;
			case RESET:
				reset(); break;
			case SAVE:
				parent.close(); break;
		}
	}

	private void switchMode() {
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

		modeSwitch.displayString = (libraryMode ? TailsComponents.EDITOR_MODE : TailsComponents.LIBRARY_MODE).getFormattedText();
	}

	private void reset() {
		final ClientPartInfo partInfo = parent.getOriginalPartInfo().clone();
		parent.getPartPanel().selectDefaultListEntry();
		parent.getLibraryPanel().initList("");
		parent.getLibraryInfoPanel().setEntry(null);
		parent.getTintPanel().setEditingTint(0);
		parent.setPartsInfo(partInfo);
	}
}