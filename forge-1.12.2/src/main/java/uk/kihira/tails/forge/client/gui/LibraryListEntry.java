/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.TailsLanguage;
import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.forge.client.gui.panel.LibraryPanel;
import uk.kihira.tails.forge.client.gui.widget.IconButton;

@Internal
public final class LibraryListEntry implements GuiListExtended.IGuiListEntry, Comparable<LibraryListEntry> {

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

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
		if (data == null) {
			Minecraft.getMinecraft().fontRenderer.drawString(TailsComponents.CREATE_ENTRY.getFormattedText(), x + 3, y + slotHeight / 2 - 4, 0xFFFFFF);
			return;
		}

		if (panel.getList().getMaxScroll() > 0)
			listWidth -= 6;

		final ClientPartsData partsData = (ClientPartsData) data.partsData;
		final FontRenderer font = panel.getParent().font();

		final boolean sel = partsData.equals(panel.getParent().getPartsData());
		font.drawString((sel ? TextFormatting.GREEN + "" + TextFormatting.ITALIC : "") + data.entryName, 5, y + 3, 0xFFFFFF);

		int index = 0;

		for (ClientPartInfo partInfo : partsData.getParts()) {
			if (index == 4) break;

			final String trans = partInfo.getPart() == null ? partInfo.getPartId().toString() : I18n.format(partInfo.getPart().getTranslationKey());
			font.drawString(trans, x + 5, y + 12 + 8 * index, 0xFFFFFF);

			for (int i = 1; i < 4; i++)
				Gui.drawRect(listWidth - 1 - 8 * i, y + 13 + index * 8,
						listWidth - 1 + 7 - 8 * i, y + 20 + index * 8,
						0xFF000000 | partInfo.getTints()[i - 1]);

			index++;
		}

		if (data.favourite) {
			GlStateManager.pushMatrix();

			GlStateManager.translate(x + listWidth - 16, y, 10F);
			GlStateManager.scale(0.8F, 0.8F, 1F);

			panel.getParent().mc.getTextureManager().bindTexture(IconButton.ICONS_TEXTURE);
			Gui.drawModalRectWithCustomSizedTexture(0, 0, TailsIcons.STAR.u, TailsIcons.STAR.v + 32, 16, 16, 256, 256);

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
		if (data == null) {
			// Create entry and add to library.
			final GameProfile profile = Minecraft.getMinecraft().player.getGameProfile();
			final LibraryEntryData data = new LibraryEntryData(
					profile.getId(),
					profile.getName(),
					I18n.format(TailsLanguage.DEFAULT_ENTRY_NAME),
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
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}

	@Override
	public void updatePosition(int slotIndex, int x, int y, float partialTick) {}

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