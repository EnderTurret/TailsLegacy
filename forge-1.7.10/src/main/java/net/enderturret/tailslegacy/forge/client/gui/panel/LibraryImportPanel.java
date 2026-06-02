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
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Strings;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.client.gui.panel.BaseLibraryImportPanel;
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.forge.client.gui.widget.SimpleGuiTextField;
import net.enderturret.tailslegacy.forge.client.toast.ToastManager;

import cpw.mods.fml.client.config.GuiButtonExt;

@Internal
public final class LibraryImportPanel extends Panel implements BaseLibraryImportPanel {

	public static final int IMPORT_STRING = 200;
	public static final int INPUT_FIELD = 201;

	private GuiTextField inputField;

	public LibraryImportPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		addRenderableWidget(new GuiButtonExt(IMPORT_STRING, left + 3, top + 21, right - left - 6, 18, TailsComponents.IMPORT_STRING.getFormattedText()));

		inputField = new SimpleGuiTextField(INPUT_FIELD, parent.font(), left + 4, top + 42, right - left - 8, 13);
		inputField.setMaxStringLength(5000);
		addRenderableWidget(inputField);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case IMPORT_STRING:
				importFromString0();
		}
	}

	private void importFromString0() {
		final String input = inputField.getText();
		if (!Strings.isNullOrEmpty(input)) importFromString(input);
	}

	@Override
	public void importPartsData(LibraryEntryData entry) {
		inputField.setText("");
		parent.getLibraryPanel().libraryChanged = true;
		parent.getLibraryPanel().initList("");
	}

	@Override
	public void toast(String langKey, @Nullable String name, boolean error) {
		final String text = (error ? EnumChatFormatting.RED : EnumChatFormatting.GREEN) + I18n.format(langKey, name == null ? new Object[0] : new Object[] { name });
		ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2, text);
	}
}