/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.util.UUID;

import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.RelativeTextBox;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartsData;

public final class LibraryImportPanel extends Panel<EditorScreen> {

	private EditBox inputField;

	public LibraryImportPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		addRenderableWidget(new ExtendedButton(3, 21, right - left - 6, 18, Component.translatable("tails.gui.library.import.string"), b -> {
			if (Strings.isNullOrEmpty(inputField.getValue()) || inputField.getValue().split(":", 3).length != 3)
				ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
						Component.translatable("tails.gui.library.import.toast.invalid").withStyle(ChatFormatting.RED));
			else {
				final String[] strings = inputField.getValue().split(":", 4);
				try {
					final LibraryEntryData entryData = new LibraryEntryData(UUID.fromString(strings[1]), strings[2], strings[0], LocalPartManager.GSON.fromJson(strings[3], PartsData.class));
					Tails.PROXY.getLibraryManager().addEntry(entryData);
					parent.getLibraryPanel().initList();

					ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
							Component.translatable("tails.gui.library.import.toast.success", strings[0]).withStyle(ChatFormatting.GREEN));
				} catch (IllegalArgumentException e) {
					ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
							Component.translatable("tails.gui.library.import.toast.invalid.uuid").withStyle(ChatFormatting.RED));
				} catch (JsonSyntaxException e) {
					ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
							Component.translatable("tails.gui.library.import.toast.invalid.parts").withStyle(ChatFormatting.RED));
				}
			}
		}));

		inputField = new RelativeTextBox(font, 3, 41, right - left - 6, 15, null);
		inputField.setMaxLength(5000);
		addRenderableWidget(inputField);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		fillGradient(poseStack, 0, 0, right - left, bottom - top, 0xDE000000, 0xDE000000);

		super.render(poseStack, mouseX, mouseY, partialTick);
	}
}
