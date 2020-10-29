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

import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;


public class LibraryListEntry extends ExtendedList.AbstractListEntry<LibraryListEntry> {

    public final LibraryEntryData data;

    public LibraryListEntry(LibraryEntryData libraryEntryData) {
        this.data = libraryEntryData;
    }

    @Override
    public void render(MatrixStack matrixStack, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        if (data.remoteEntry) {
            Minecraft.getInstance().getTextureManager().bindTexture(GuiIconButton.iconsTextures);
            GuiIconButton.Icons icon = GuiIconButton.Icons.SERVER;
            matrixStack.push();
            matrixStack.translate(x + listWidth - 16, y + slotHeight - 12, 0F);
            matrixStack.scale(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(matrixStack, 0, 0, icon.u, icon.v, 16, 16, 10);
            matrixStack.pop();
        }

        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.drawString(matrixStack, (data.partsData.equals(Tails.localPartsData) ? TextFormatting.GREEN + "" + TextFormatting.ITALIC : "") + data.entryName, 5, y + 3, 0xFFFFFF);

        //fontRenderer.setUnicodeFlag(true);
        for (PartsData.PartType type : PartsData.PartType.values()) {
            if (data.partsData.hasPartInfo(type)) {
                PartInfo partInfo = data.partsData.getPartInfo(type);
                fontRenderer.drawString(matrixStack, I18n.format(PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid).getUnlocalisedName(partInfo.subid)), x + 5, y + 12 + (8 * type.ordinal()), 0xFFFFFF);
                for (int i = 1; i < 4; i++) {
                    AbstractGui.fill(matrixStack, listWidth - (8 * i), y + 13 + (type.ordinal() * 8), listWidth + 7 - (8 * i), y + 20 + (type.ordinal() * 8), partInfo.tints[i - 1]);
                }
            }
        }
        //fontRenderer.setUnicodeFlag(false);

        if (data.favourite) {
            Minecraft.getInstance().getTextureManager().bindTexture(GuiIconButton.iconsTextures);
            GuiIconButton.Icons icon = GuiIconButton.Icons.STAR;
            matrixStack.push();
            matrixStack.translate(x + listWidth - 16, y, 0F);
            matrixStack.scale(0.8F, 0.8F, 1F);
            GuiUtils.drawTexturedModalRect(matrixStack, 0, 0, icon.u, icon.v + 32, 16, 16, 10);
            matrixStack.pop();
        }
    }

    //@Override
    //public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {}

    public static class NewLibraryListEntry extends LibraryListEntry {

        private final LibraryPanel panel;

        public NewLibraryListEntry(LibraryPanel panel, LibraryEntryData libraryEntryData) {
            super(libraryEntryData);
            this.panel = panel;
        }

        @Override
        public void render(MatrixStack matrixStack, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            Minecraft.getInstance().fontRenderer.drawString(matrixStack, I18n.format("gui.library.create"), x + 3, y + (slotHeight / 2) - 4, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            //Create entry and add to library
            GameProfile profile = Minecraft.getInstance().player.getGameProfile();
            LibraryEntryData data = new LibraryEntryData(profile.getId(), profile.getName(), I18n.format("gui.library.entry.default"), Tails.localPartsData);
            Tails.proxy.getLibraryManager().addEntry(data);
            panel.addSelectedEntry(new LibraryListEntry(data));
            return false;
        }
    }
}
