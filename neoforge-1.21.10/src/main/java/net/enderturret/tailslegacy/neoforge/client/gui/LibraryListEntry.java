/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.client.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.authlib.GameProfile;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.gui.TailsIcons;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.neoforge.client.gui.panel.LibraryPanel;
import net.enderturret.tailslegacy.neoforge.client.gui.widget.IconButton;

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
	public void renderContent(GuiGraphics gui, int mouseX, int mouseY, boolean isHovering, float partialTick) {
		if (data == null) {
			gui.drawString(Minecraft.getInstance().font, CREATE, getX() + 5, getY() + getHeight() / 2 - 4, 0xFFFFFFFF);
			return;
		}

		int width = getWidth();
		if (panel.getList().maxScrollAmount() > 0)
			width -= 6;

		final ClientPartsData partsData = (ClientPartsData) data.partsData;
		final Font font = panel.getParent().font();

		final boolean sel = partsData.equals(panel.getParent().getPartsData());
		final MutableComponent name = Component.literal(data.entryName);
		if (sel) name.withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC);
		gui.drawString(font, name, getX() + 5, getContentY() + 3, 0xFFFFFFFF);

		int index = 0;

		for (ClientPartInfo partInfo : partsData.getParts()) {
			if (index == 4) break;

			final String trans = partInfo.getPart() == null ? partInfo.getPartId().toString() : I18n.get(partInfo.getPart().getTranslationKey());
			gui.drawString(font, trans, getX() + 7, getContentY() + 12 + 8 * index, 0xFFFFFFFF);

			for (int i = 1; i < 4; i++)
				gui.fill(width - 1 - 8 * i, getContentY() + 13 + index * 8,
						width - 1 + 7 - 8 * i, getContentY() + 20 + index * 8,
						0xFF000000 | partInfo.getTints()[i - 1]);

			index++;
		}

		if (data.favourite) {
			gui.pose().pushMatrix();

			gui.pose().translate(getX() + width - 14, getContentY());
			gui.pose().scale(0.8F, 0.8F);

			gui.blit(RenderPipelines.GUI_TEXTURED, IconButton.ICONS_TEXTURE, 0, 0, TailsIcons.STAR.u, TailsIcons.STAR.v + 32, 16, 16, 256, 256);

			gui.pose().popMatrix();
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
		if (data == null) {
			// Create entry and add to library.
			final GameProfile profile = Minecraft.getInstance().player.getGameProfile();
			final LibraryEntryData data = new LibraryEntryData(
					profile.id(),
					profile.name(),
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