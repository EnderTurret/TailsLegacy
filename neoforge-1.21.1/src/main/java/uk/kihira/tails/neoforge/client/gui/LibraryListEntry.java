/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.authlib.GameProfile;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.TailsLanguage;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.neoforge.client.gui.panel.LibraryPanel;
import uk.kihira.tails.neoforge.client.gui.widget.IconButton;

@Internal
public final class LibraryListEntry extends ObjectSelectionList.Entry<LibraryListEntry> implements Comparable<LibraryListEntry> {

	protected final LibraryPanel panel;
	public final LibraryEntryData data;

	@Internal
	public LibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
		this.panel = panel;
		data = libraryEntryData;
	}

	public static LibraryListEntry makeNewEntryEntry(LibraryPanel panel) {
		return new LibraryListEntry(panel, null);
	}

	private static final Component CREATE = TailsComponents.CREATE_ENTRY;

	@Override
	public void render(GuiGraphics gui, int slotIndex, int rowTop, int rowLeft, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
		if (data == null) {
			gui.drawString(Minecraft.getInstance().font, CREATE, rowLeft + 3, rowTop + slotHeight / 2 - 4, 0xFFFFFF);
			return;
		}

		if (panel.getList().getMaxScroll() > 0)
			listWidth -= 6;

		final ClientPartsData partsData = (ClientPartsData) data.partsData;
		final Font font = panel.getParent().font();

		final boolean sel = partsData.equals(panel.getParent().getPartsData());
		final MutableComponent name = Component.literal(data.entryName);
		if (sel) name.withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC);
		gui.drawString(font, name, 5, rowTop + 3, 0xFFFFFF);

		int index = 0;

		for (ClientPartInfo partInfo : partsData.getParts()) {
			if (index == 4) break;

			final String trans = partInfo.getPart() == null ? partInfo.getPartId().toString() : I18n.get(partInfo.getPart().getTranslationKey());
			gui.drawString(font, trans, rowLeft + 5, rowTop + 12 + 8 * index, 0xFFFFFF);

			for (int i = 1; i < 4; i++)
				gui.fill(listWidth - 1 - 8 * i, rowTop + 13 + index * 8,
						listWidth - 1 + 7 - 8 * i, rowTop + 20 + index * 8,
						0xFF000000 | partInfo.getTints()[i - 1]);

			index++;
		}

		if (data.favourite) {
			gui.pose().pushPose();

			gui.pose().translate(rowLeft + listWidth - 16, rowTop, 0F);
			gui.pose().scale(0.8F, 0.8F, 1F);

			gui.blit(IconButton.ICONS_TEXTURE, 0, 0, 10, TailsIcons.STAR.u, TailsIcons.STAR.v + 32, 16, 16, 256, 256);

			gui.pose().popPose();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (data == null) {
			// Create entry and add to library.
			final GameProfile profile = Minecraft.getInstance().player.getGameProfile();
			final LibraryEntryData data = new LibraryEntryData(
					profile.getId(),
					profile.getName(),
					I18n.get(TailsLanguage.DEFAULT_ENTRY_NAME),
					panel.getParent().getPartsData());

			TailsClientPlatform.get().getLibraryManager().addEntry(data);
			panel.addSelectedEntry(new LibraryListEntry(panel, data));
			return true;
		}

		panel.getList().setSelected(this);
		panel.getParent().getLibraryInfoPanel().setEntry(this);
		panel.getParent().setPartsData(ClientPartsData.clone(data.partsData.deepCopy()));

		return true;
	}

	@Override
	public Component getNarration() {
		return Component.empty();
	}

	@Override
	public int compareTo(LibraryListEntry o) {
		if (data == null) return -1;
		if (o.data == null) return 1;

		// Put favorites at the top.
		if (data.favourite && !o.data.favourite) return -1;
		if (!data.favourite && o.data.favourite) return 1;

		return Long.compare(data.creationDate, o.data.creationDate);
	}
}