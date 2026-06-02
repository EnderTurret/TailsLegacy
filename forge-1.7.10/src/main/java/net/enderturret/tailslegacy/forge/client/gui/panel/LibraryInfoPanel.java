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
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.common.client.gui.panel.BaseLibraryInfoPanel;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.LibraryListEntry;
import net.enderturret.tailslegacy.forge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.forge.client.gui.widget.IconButton;
import net.enderturret.tailslegacy.forge.client.gui.widget.SimpleGuiTextField;
import net.enderturret.tailslegacy.forge.client.toast.ToastManager;

@Internal
public final class LibraryInfoPanel extends Panel implements BaseLibraryInfoPanel {

	public static final int NAME_FIELD = 300;
	public static final int FAVORITE = 301;
	public static final int DELETE = 302;
	public static final int EXPORT = 303;

	private LibraryListEntry entry;

	private SimpleGuiTextField textField;
	private IconButton.Toggle favButton;
	private IconButton deleteButton;

	public LibraryInfoPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		textField = new SimpleGuiTextField(NAME_FIELD, parent.font(), left + 7, top + 7, right - left - 14, 13);
		textField.setMaxStringLength(19);
		addRenderableWidget(textField);
		textField.setGuiResponder(str -> {
			if (entry != null) {
				entry.data.entryName = str;
				parent.getLibraryPanel().libraryChanged = true;
			}
		});

		addRenderableWidget(favButton = new IconButton.Toggle(FAVORITE, left + 5, bottom - 20, TailsIcons.STAR))
		.setTooltip(TailsComponents.FAVORITE_BUTTON.getFormattedText());

		addRenderableWidget(deleteButton = new IconButton(DELETE, left + 21, bottom - 20, TailsIcons.DELETE))
		.setTooltip(TailsComponents.DELETE_BUTTON.getFormattedText());

		addRenderableWidget(new IconButton(EXPORT, left + 68, bottom - 20, TailsIcons.EXPORT))
		.setTooltip(TailsComponents.SHARE_BUTTON.getFormattedText());

		setEntry(null);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case FAVORITE:
				favButton.onPress();
				entry.data.favourite = favButton.toggled;
				break;
			case DELETE:
				deleteButton.setHover(false);
				parent.getLibraryPanel().removeEntry(entry);
				setEntry(null);
				break;
			case EXPORT:
				final String export = exportString(getEntry().data);

				ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, TailsComponents.EXPORTED_MESSAGE.getFormattedText());
				GuiScreen.setClipboardString(export);
				break;
		}
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		drawRect(left + 3, top + 3, right - 3, bottom - 3, 0xFF000000);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);

		GL11.glColor4f(1F, 1F, 1F, 1F);

		if (entry != null) {
			int index = 0;

			final int xOffset = left;
			final int yOffset = top;
			for (ClientPartInfo partInfo : ((ClientPartsData) entry.data.partsData).getParts()) {
				parent.font().drawString(TailsComponents.getPartName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4),
						0xFFFFFFFF);

				parent.font().drawString(TailsComponents.getSubTypeName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 1),
						0xFFFFFFFF);

				parent.font().drawString(TailsComponents.getTextureName(partInfo),
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 2),
						0xFFFFFFFF);

				for (int i = 1; i < 4; i++)
					drawRect(
							xOffset + (right - left) - 4 - 8 * i,
							yOffset + 32 + (index * 4 + 3) * 8,
							xOffset + (right - left) - 4 + 7 - 8 * i,
							yOffset + 32 + 7 + (index * 4 + 3) * 8,
							0xFF000000 | partInfo.getTints()[i - 1]);

				index++;
			}

			parent.font().drawString(TailsComponents.LIBRARY_ENTRY_CREATOR.getFormattedText(), left + 5, bottom - 59, 0xFFAAAAAA);
			parent.font().drawString(entry.data.creatorName, right - 5 - parent.font().getStringWidth(entry.data.creatorName), bottom - 50, 0xFFAAAAAA);
			parent.font().drawString(TailsComponents.LIBRARY_ENTRY_CREATION_DATE.getFormattedText(), left + 5, bottom - 41, 0xFFAAAAAA);
			final String date = DATE_FORMAT.format(new Date(entry.data.creationDate));
			parent.font().drawString(date, right - 5 - parent.font().getStringWidth(date), bottom - 32, 0xFFAAAAAA);
		}
	}

	public void setEntry(@Nullable LibraryListEntry entry) {
		this.entry = entry;

		final boolean visible = entry != null;

		if (visible) {
			favButton.toggled = entry.data.favourite;
			textField.setText(entry.data.entryName);
		}

		setChildrenVisible(visible);
	}

	@Nullable
	public LibraryListEntry getEntry() {
		return entry;
	}
}