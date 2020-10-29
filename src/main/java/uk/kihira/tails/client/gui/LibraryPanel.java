package uk.kihira.tails.client.gui;

import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

public class LibraryPanel extends Panel<GuiEditor> implements IListCallback<LibraryListEntry> {

    private static final LibrarySorter sorter = new LibrarySorter();
    private GuiList<LibraryListEntry> list;
    private TextFieldWidget searchField;

    public LibraryPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    public void init() {
        initList();

        addButton(new ExtendedButton(3, height - 18, width - 6, 15, new TranslationTextComponent("gui.button.all"), b -> {}));
        searchField = new TextFieldWidget(font, 5, height - 31, width - 10, 10, null);
        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        setBlitOffset(-100);
        fillGradient(matrixStack, 0, 0, width, height, 0xCC000000, 0xCC000000);

        searchField.render(matrixStack, mouseX, mouseY, partialTicks);
        list.render(matrixStack, mouseX, mouseY, partialTicks);

        setBlitOffset(0);
        Minecraft.getInstance().getTextureManager().bindTexture(GuiIconButton.iconsTextures);
        matrixStack.push();
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        matrixStack.translate(width - 16, height - 32, 0);
        matrixStack.scale(0.75F, 0.75F, 0F);
        blit(matrixStack, 0, 0, 160, 0, 16, 16);
        matrixStack.pop();

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        list.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        list.mouseReleased(mouseX, mouseY, mouseButton);
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        searchField.keyPressed(keyCode, scanCode, modifiers);
        if (searchField.getVisible() && searchField.isFocused()) {
            List<LibraryListEntry> newEntries = filterListEntries(searchField.getText().toLowerCase());
            newEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));
            list.getEntries().clear();
            list.getEntries().addAll(newEntries);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, LibraryListEntry entry) {
        if (!(entry instanceof LibraryListEntry.NewLibraryListEntry)) {
            parent.libraryInfoPanel.setEntry(entry);
            parent.setPartsData(entry.data.partsData.deepCopy());
        }
        return true;
    }

    public void initList() {
        List<LibraryListEntry> libraryEntries = new ArrayList<>();
        for (LibraryEntryData data : Tails.proxy.getLibraryManager().libraryEntries) {
            libraryEntries.add(new LibraryListEntry(data));
        }

        //Add in new entry creation
        libraryEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));

        libraryEntries.sort(sorter);

        list = new GuiList<>(this, width, height - 34, 0, height - 34, 50, libraryEntries);
    }

    public void addSelectedEntry(LibraryListEntry entry) {
        list.getEntries().add(entry);
        list.setCurrentIndex(list.getEntries().size() - 1);
        parent.libraryInfoPanel.setEntry(entry);
    }

    public void removeEntry(LibraryListEntry entry) {
        Tails.proxy.getLibraryManager().removeEntry(entry.data);
        list.getEntries().remove(entry);
    }

    private List<LibraryListEntry> filterListEntries(String filter) {
        ArrayList<LibraryListEntry> filteredEntries = new ArrayList<>();
        List<LibraryListEntry> entries = new ArrayList<>();

        for (LibraryEntryData data : Tails.proxy.getLibraryManager().libraryEntries) {
            entries.add(new LibraryListEntry(data));
        }

        for (LibraryListEntry entry : entries) {
            if (entry instanceof LibraryListEntry.NewLibraryListEntry || entry.data.entryName.toLowerCase().contains(filter)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    @Override
    public void onClose() {
        Tails.proxy.getLibraryManager().removeRemoteEntries();
        super.onClose();
    }

    private static class LibrarySorter implements Comparator<LibraryListEntry> {

        @Override
        public int compare(LibraryListEntry entry1, LibraryListEntry entry2) {
            if (entry1.equals(entry2)) {
                return 0;
            }

            if (entry1 instanceof LibraryListEntry.NewLibraryListEntry) {
                return Integer.MIN_VALUE;
            } else if (entry2 instanceof LibraryListEntry.NewLibraryListEntry) {
                return Integer.MAX_VALUE;
            }

            //Put favourites at the top
            if (entry1.data.favourite && !entry2.data.favourite) {
                return -1;
            } else if (!entry1.data.favourite && entry2.data.favourite) {
                return 1;
            }

            return 0;
        }
    }
}
