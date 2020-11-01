package uk.kihira.tails.client.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;
import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.gui.panel.LibraryPanel;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;

public class LibraryListEntry extends ExtendedList.AbstractListEntry<LibraryListEntry> {

	protected final LibraryPanel panel;
	public final LibraryEntryData data;

	public LibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
		this.panel = panel;
		data = libraryEntryData;
	}

	@Override
	public void render(MatrixStack matrixStack, int slotIndex, int rowTop, int rowLeft, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
		if (data.remoteEntry) {
			Minecraft.getInstance().getTextureManager().bindTexture(IconButton.iconsTextures);
			final IconButton.Icons icon = IconButton.Icons.SERVER;
			matrixStack.push();
			matrixStack.translate(rowLeft + listWidth - 16, rowTop + slotHeight - 12, 0F);
			matrixStack.scale(0.8F, 0.8F, 1F);
			GuiUtils.drawTexturedModalRect(matrixStack, 0, 0, icon.u, icon.v, 16, 16, 10);
			matrixStack.pop();
		}

		final FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		fontRenderer.drawString(matrixStack, (data.partsData.equals(Tails.localPartsData) ? TextFormatting.GREEN + "" + TextFormatting.ITALIC : "") + data.entryName,
				5, rowTop + 3, 0xFFFFFF);

		for (PartsData.PartType type : PartsData.PartType.values())
			if (data.partsData.hasPartInfo(type)) {
				final PartInfo partInfo = data.partsData.getPartInfo(type);
				ClientUtils.drawStringMultiLine(matrixStack, fontRenderer, I18n.format(PartRegistry.getPartRenderer(partInfo.partType, partInfo.typeid).getUnlocalisedName(partInfo.subid)),
						rowLeft + 5, rowTop + 12 + 8 * type.ordinal(), 0xFFFFFF);
				for (int i = 1; i < 4; i++)
					AbstractGui.fill(matrixStack,
							listWidth - 8 * i, rowTop + 13 + type.ordinal() * 8,
							listWidth + 7 - 8 * i, rowTop + 20 + type.ordinal() * 8,
							partInfo.tints[i - 1]);
			}

		if (data.favourite) {
			Minecraft.getInstance().getTextureManager().bindTexture(IconButton.iconsTextures);
			final IconButton.Icons icon = IconButton.Icons.STAR;
			matrixStack.push();
			matrixStack.translate(rowLeft + listWidth - 16, rowTop, 0F);
			matrixStack.scale(0.8F, 0.8F, 1F);
			GuiUtils.drawTexturedModalRect(matrixStack, 0, 0, icon.u, icon.v + 32, 16, 16, 10);
			matrixStack.pop();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		panel.getList().setSelected(this);
		panel.getParent().getLibraryInfoPanel().setEntry(this);
		panel.getParent().setPartsData(data.partsData.deepCopy());
		return true;
	}

	public static class NewLibraryListEntry extends LibraryListEntry {

		public NewLibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
			super(panel, libraryEntryData);
		}

		@Override
		public void render(MatrixStack matrixStack, int slotIndex, int rowTop, int rowLeft, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
			Minecraft.getInstance().fontRenderer.drawString(matrixStack, I18n.format("gui.library.create"), rowLeft + 3, rowTop + slotHeight / 2 - 4, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			// Create entry and add to library.
			final GameProfile profile = Minecraft.getInstance().player.getGameProfile();
			final LibraryEntryData data = new LibraryEntryData(profile.getId(), profile.getName(), I18n.format("gui.library.entry.default"), Tails.localPartsData);
			Tails.PROXY.getLibraryManager().addEntry(data);
			panel.addSelectedEntry(new LibraryListEntry(panel, data));
			return true;
		}
	}
}
