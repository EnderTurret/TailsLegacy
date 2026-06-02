/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.panel;

import java.util.Date;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.common.client.gui.panel.BaseLibraryInfoPanel;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.LibraryListEntry;
import net.enderturret.tailslegacy.forge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.forge.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.forge.client.toast.ToastManager;

@Internal
public final class LibraryInfoPanel extends Panel implements BaseLibraryInfoPanel {

	private LibraryListEntry entry;

	private EditBox textField;
	private IconButton.Toggle favButton;
	private IconButton deleteButton;

	public LibraryInfoPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		textField = new EditBox(parent.font(), left + 7, top + 7, right - left - 14, 13, Component.empty());
		textField.setMaxLength(19);
		addRenderableWidget(textField);
		textField.setResponder(str -> {
			if (entry != null) {
				entry.data.entryName = str;
				parent.getLibraryPanel().libraryChanged = true;
			}
		});

		addRenderableWidget(favButton = new IconButton.Toggle(left + 5, bottom - 20, TailsIcons.STAR, b -> {
			entry.data.favourite = favButton.toggled;
		})).setTooltip(Tooltip.create(TailsComponents.FAVORITE_BUTTON));

		addRenderableWidget(deleteButton = new IconButton(left + 21, bottom - 20, TailsIcons.DELETE, b -> {
			deleteButton.setHover(false);
			parent.getLibraryPanel().removeEntry(entry);
			setEntry(null);
		})).setTooltip(Tooltip.create(TailsComponents.DELETE_BUTTON));

		addRenderableWidget(new IconButton(left + 68, bottom - 20, TailsIcons.EXPORT, b -> {
			final String export = exportString(getEntry().data);

			ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, TailsComponents.EXPORTED_MESSAGE);
			GLFW.glfwSetClipboardString(parent.getMinecraft().getWindow().getWindow(), export);
		})).setTooltip(Tooltip.create(TailsComponents.SHARE_BUTTON));

		setEntry(null);
	}

	@Override
	public void renderBackground(GuiGraphics gui) {
		super.renderBackground(gui);
		gui.fill(left + 3, top + 3, right - 3, bottom - 3, 0, 0xFF000000);
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		super.renderWidget(gui, mouseX, mouseY, partialTick);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		if (entry != null) {
			int index = 0;

			final int xOffset = left;
			final int yOffset = top;
			for (ClientPartInfo partInfo : ((ClientPartsData) entry.data.partsData).getParts()) {
				gui.drawString(parent.font(), TailsComponents.getPartName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4),
						0xFFFFFFFF);

				gui.drawString(parent.font(), TailsComponents.getSubTypeName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 1),
						0xFFFFFFFF);

				gui.drawString(parent.font(), TailsComponents.getTextureName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 2),
						0xFFFFFFFF);

				for (int i = 1; i < 4; i++)
					gui.fill(
							xOffset + (right - left) - 4 - 8 * i,
							yOffset + 32 + (index * 4 + 3) * 8,
							xOffset + (right - left) - 4 + 7 - 8 * i,
							yOffset + 32 + 7 + (index * 4 + 3) * 8,
							0xFF000000 | partInfo.getTints()[i - 1]);

				index++;
			}

			gui.drawString(parent.font(), TailsComponents.LIBRARY_ENTRY_CREATOR, left + 5, bottom - 59, 0xFFAAAAAA);
			gui.drawString(parent.font(), entry.data.creatorName, right - 5 - parent.font().width(entry.data.creatorName), bottom - 50, 0xFFAAAAAA);
			gui.drawString(parent.font(), TailsComponents.LIBRARY_ENTRY_CREATION_DATE, left + 5, bottom - 41, 0xFFAAAAAA);
			final String date = DATE_FORMAT.format(new Date(entry.data.creationDate));
			gui.drawString(parent.font(), date, right - 5 - parent.font().width(date), bottom - 32, 0xFFAAAAAA);
		}
	}

	public void setEntry(@Nullable LibraryListEntry entry) {
		this.entry = entry;

		final boolean visible = entry != null;

		if (visible) {
			favButton.toggled = entry.data.favourite;
			textField.setValue(entry.data.entryName);
		}

		setChildrenVisible(visible);
	}

	@Nullable
	public LibraryListEntry getEntry() {
		return entry;
	}
}