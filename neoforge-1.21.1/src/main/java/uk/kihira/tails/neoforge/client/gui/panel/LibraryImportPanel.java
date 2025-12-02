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
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Strings;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.common2.LibraryEntryData;
import uk.kihira.tails.common2.client.gui.panel.BaseLibraryImportPanel;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;
import uk.kihira.tails.neoforge.client.gui.widget.RelativeTextBox;
import uk.kihira.tails.neoforge.client.toast.ToastManager;

@Internal
public final class LibraryImportPanel extends Panel<EditorScreen> implements BaseLibraryImportPanel {

	private EditBox inputField;

	public LibraryImportPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		addRenderableWidget(new ExtendedButton(3, 21, right - left - 6, 18, Component.translatable("tails.gui.library.import.string"), this::importFromString0));

		inputField = new RelativeTextBox(this, font, 3, 41, right - left - 6, 15, null);
		inputField.setMaxLength(5000);
		addRenderableWidget(inputField);
	}

	private void importFromString0(Button b) {
		final String input = inputField.getValue();
		if (!Strings.isNullOrEmpty(input)) importFromString(input);
	}

	@Override
	public void importPartsData(LibraryEntryData entry) {
		inputField.setValue("");
		parent.getLibraryPanel().libraryChanged = true;
		parent.getLibraryPanel().initList();
	}

	@Override
	public void toast(String langKey, @Nullable String name, boolean error) {
		final Component text = Component.translatable(langKey, name == null ? new Object[0] : new Object[] { name })
				.withStyle(error ? ChatFormatting.RED : ChatFormatting.GREEN);
		ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2, text);
	}

	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		gui.fillGradient(0, 0, right - left, bottom - top, 0xDE000000, 0xDE000000);

		super.render(gui, mouseX, mouseY, partialTick);
	}
}