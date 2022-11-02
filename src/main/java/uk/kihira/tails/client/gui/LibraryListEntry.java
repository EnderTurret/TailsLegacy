/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.client.RenderHelper;
import uk.kihira.tails.client.gui.panel.LibraryPanel;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartType;

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
	public void render(PoseStack poseStack, int slotIndex, int rowTop, int rowLeft, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
		final Font fontRenderer = Minecraft.getInstance().font;
		fontRenderer.draw(poseStack, (data.partsData.equals(LocalPartManager.getLocalPartsData()) ? ChatFormatting.GREEN + "" + ChatFormatting.ITALIC : "") + data.entryName,
				5, rowTop + 3, 0xFFFFFF);

		for (PartType type : PartType.values())
			if (data.partsData.hasPartInfo(type)) {
				final ClientPartInfo partInfo = (ClientPartInfo) data.partsData.getPartInfo(type);
				final String trans = partInfo.isInvalid() ? partInfo.getPartId().toString() : I18n.get(partInfo.getPart().getTranslationKey());
				RenderHelper.drawStringMultiLine(poseStack, fontRenderer, trans,
						rowLeft + 5, rowTop + 12 + 8 * type.ordinal(), 0xFFFFFF);
				for (int i = 1; i < 4; i++)
					GuiComponent.fill(poseStack,
							listWidth - 8 * i, rowTop + 13 + type.ordinal() * 8,
							listWidth + 7 - 8 * i, rowTop + 20 + type.ordinal() * 8,
							partInfo.getTints()[i - 1]);
			}

		if (data.favourite) {
			RenderSystem.setShaderTexture(0, IconButton.iconsTextures);

			final IconButton.Icons icon = IconButton.Icons.STAR;

			poseStack.pushPose();

			poseStack.translate(rowLeft + listWidth - 16, rowTop, 0F);
			poseStack.scale(0.8F, 0.8F, 1F);

			GuiComponent.blit(poseStack, 0, 0, 10, icon.u, icon.v + 32, 16, 16, 256, 256);

			poseStack.popPose();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		panel.getList().setSelected(this);
		panel.getParent().getLibraryInfoPanel().setEntry(this);
		panel.getParent().setPartsData(data.partsData.deepCopy());
		return true;
	}

	@Internal
	public static class NewLibraryListEntry extends LibraryListEntry {

		@Internal
		public NewLibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
			super(panel, libraryEntryData);
		}

		@Override
		public void render(PoseStack poseStack, int slotIndex, int rowTop, int rowLeft, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
			Minecraft.getInstance().font.draw(poseStack, I18n.get("tails.gui.library.create"), rowLeft + 3, rowTop + slotHeight / 2 - 4, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			// Create entry and add to library.
			final GameProfile profile = Minecraft.getInstance().player.getGameProfile();
			final LibraryEntryData data = new LibraryEntryData(profile.getId(), profile.getName(), I18n.get("tails.gui.library.entry.default"), LocalPartManager.getLocalPartsData());
			Tails.PROXY.getLibraryManager().addEntry(data);
			panel.addSelectedEntry(new LibraryListEntry(panel, data));
			return true;
		}
	}

	@Override
	public Component getNarration() {
		return Component.empty();
	}
}
