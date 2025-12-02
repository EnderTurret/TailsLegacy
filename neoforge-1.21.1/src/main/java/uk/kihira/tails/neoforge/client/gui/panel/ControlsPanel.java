/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;

@Internal
public final class ControlsPanel extends Panel<EditorScreen> {

	private boolean libraryMode = false;

	public ControlsPanel(EditorScreen parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
	}

	@Override
	public void init() {
		// Mode Switch
		addRenderableWidget(Button.builder(Component.translatable("tails.gui.button.mode.library"), this::switchMode)
				.bounds(3, bottom - top - 25, 46, 20)
				.build());
		// Reset/Save
		addRenderableWidget(Button.builder(Component.translatable("tails.gui.button.reset"), this::reset)
				.bounds((right - left) / 2 - 23, bottom - top - 25, 46, 20)
				.build());
		addRenderableWidget(Button.builder(Component.translatable("tails.gui.done"), b -> parent.close())
				.bounds(right - left - 49, bottom - top - 25, 46, 20)
				.build());
	}

	private void switchMode(Button b) {
		libraryMode = !libraryMode;
		parent.getPartPanel().enabled = !libraryMode;
		parent.getTexturePanel().enabled = !libraryMode;
		parent.getTintPanel().enabled = !libraryMode;

		parent.getLibraryInfoPanel().enabled = libraryMode;
		parent.getLibraryPanel().enabled = libraryMode;
		parent.getLibraryImportPanel().enabled = libraryMode;

		parent.getPartPanel().selectDefaultListEntry();
		parent.getLibraryPanel().initList();
		parent.getLibraryInfoPanel().setEntry(null);
		parent.getTintPanel().setEditingTint(0);

		if (!libraryMode)
			parent.getLibraryPanel().save();

		b.setMessage(libraryMode ? Component.translatable("tails.gui.button.mode.editor") : Component.translatable("tails.gui.button.mode.library"));
	}

	private void reset(Button b) {
		final ClientPartInfo partInfo = parent.getOriginalPartInfo().clone();
		parent.getPartPanel().selectDefaultListEntry();
		parent.getLibraryPanel().initList();
		parent.getLibraryInfoPanel().setEntry(null);
		parent.getTintPanel().setEditingTint(0);
		parent.setPartsInfo(partInfo);
	}

	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		gui.fill(0, 0, right - left, bottom - top, -400, 0xDD000000);

		super.render(gui, mouseX, mouseY, partialTick);
	}
}