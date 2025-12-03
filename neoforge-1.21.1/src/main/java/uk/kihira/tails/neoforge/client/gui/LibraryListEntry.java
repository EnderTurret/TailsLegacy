/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui;

import java.util.Comparator;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.authlib.GameProfile;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.neoforge.client.RenderHelper;
import uk.kihira.tails.neoforge.client.gui.panel.LibraryPanel;
import uk.kihira.tails.neoforge.client.gui.widget.IconButton;

@Internal
public class LibraryListEntry extends ObjectSelectionList.Entry<LibraryListEntry> {

	protected final LibraryPanel panel;
	public final LibraryEntryData data;

	@Internal
	public LibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
		this.panel = panel;
		data = libraryEntryData;
	}

	@Override
	public void render(GuiGraphics gui, int slotIndex, int rowTop, int rowLeft, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
		if (panel.getList().getMaxScroll() > 0)
			listWidth -= 6;

		final ClientPartsData partsData = (ClientPartsData) data.partsData;

		final Font fontRenderer = Minecraft.getInstance().font;
		final boolean sel = partsData.equals(panel.getParent().getPartsData());
		gui.drawString(fontRenderer, (sel ? ChatFormatting.GREEN + "" + ChatFormatting.ITALIC : "") + data.entryName,
				5, rowTop + 3, 0xFFFFFF);

		int index = 0;

		for (ClientPartInfo partInfo : partsData.getParts()) {
			if (index == 4) break;

			final String trans = partInfo.getPart() == null ? partInfo.getPartId().toString() : I18n.get(partInfo.getPart().getTranslationKey());
			RenderHelper.drawStringMultiLine(gui, fontRenderer, trans,
					rowLeft + 5, rowTop + 12 + 8 * index, 0xFFFFFF);

			for (int i = 1; i < 4; i++)
				gui.fill(listWidth - 1 - 8 * i, rowTop + 13 + index * 8,
						listWidth - 1 + 7 - 8 * i, rowTop + 20 + index * 8,
						0xFF000000 | partInfo.getTints()[i - 1]);

			index++;
		}

		if (data.favourite) {
			final TailsIcons icon = TailsIcons.STAR;

			gui.pose().pushPose();

			gui.pose().translate(rowLeft + listWidth - 16, rowTop, 0F);
			gui.pose().scale(0.8F, 0.8F, 1F);

			gui.blit(IconButton.ICONS_TEXTURE, 0, 0, 10, icon.u, icon.v + 32, 16, 16, 256, 256);

			gui.pose().popPose();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		panel.getList().setSelected(this);
		panel.getParent().getLibraryInfoPanel().setEntry(this);
		panel.getParent().setPartsData(ClientPartsData.clone(data.partsData.deepCopy()));
		panel.getParent().setPartsInfo(panel.getParent().getPartsData().getPartInfo(panel.getParent().getAttachmentPoint()));
		return true;
	}

	@Override
	public Component getNarration() {
		return Component.empty();
	}

	@Internal
	public static class NewLibraryListEntry extends LibraryListEntry {

		@Internal
		public NewLibraryListEntry(LibraryPanel panel) {
			super(panel, null);
		}

		@Override
		public void render(GuiGraphics gui, int slotIndex, int rowTop, int rowLeft, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
			gui.drawString(Minecraft.getInstance().font, I18n.get("tails.gui.library.create"), rowLeft + 3, rowTop + slotHeight / 2 - 4, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			// Create entry and add to library.
			final GameProfile profile = Minecraft.getInstance().player.getGameProfile();
			final LibraryEntryData data = new LibraryEntryData(
					profile.getId(),
					profile.getName(),
					I18n.get("tails.gui.library.entry.default"),
					panel.getParent().getPartsData());

			TailsClientPlatform.get().getLibraryManager().addEntry(data);
			panel.addSelectedEntry(new LibraryListEntry(panel, data));

			return true;
		}
	}

	@Internal
	public static final class LibrarySorter implements Comparator<LibraryListEntry> {

		public static final LibrarySorter INSTANCE = new LibrarySorter();

		@Override
		public int compare(LibraryListEntry entry1, LibraryListEntry entry2) {
			if (entry1.equals(entry2))
				return 0;

			if (entry1 instanceof LibraryListEntry.NewLibraryListEntry)
				return -1;
			if (entry2 instanceof LibraryListEntry.NewLibraryListEntry)
				return 1;

			// Put favorites at the top.
			if (entry1.data.favourite && !entry2.data.favourite)
				return -1;
			if (!entry1.data.favourite && entry2.data.favourite)
				return 1;

			return 0;
		}
	}
}