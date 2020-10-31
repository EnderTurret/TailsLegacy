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

	public GuiList<LibraryListEntry> getList() {
		return list;
	}

	@Override
	public void init() {
		initList();

		addButton(new ExtendedButton(3, bottom - top - 18, right - left - 6, 15, new TranslationTextComponent("gui.button.all"), b -> {}));
		addButton(searchField = new TextFieldWidget(font, 5, bottom - top - 31, right - left - 10, 10, null));

		super.init();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		setBlitOffset(-100);
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);

		list.render(matrixStack, mouseX, mouseY, partialTicks);

		setBlitOffset(0);

		super.render(matrixStack, mouseX, mouseY, partialTicks);

		setBlitOffset(30);

		Minecraft.getInstance().getTextureManager().bindTexture(GuiIconButton.iconsTextures);

		matrixStack.push();

		RenderSystem.color4f(1f, 1f, 1f, 1f);
		matrixStack.translate(right - left - 16, bottom - top - 32, 0);
		matrixStack.scale(0.75F, 0.75F, 0F);

		blit(matrixStack, 0, 0, 160, 0, 16, 16);

		matrixStack.pop();
	}

	/*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        list.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        list.mouseReleased(mouseX, mouseY, mouseButton);
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }*/

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		final boolean value = super.keyPressed(keyCode, scanCode, modifiers);

		if (value) {
			List<LibraryListEntry> newEntries = filterListEntries(searchField.getText().toLowerCase());
			newEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));
			list.getEventListeners().clear();
			list.getEventListeners().addAll(newEntries);
		}

		return value;
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
			libraryEntries.add(new LibraryListEntry(this, data));
		}

		//Add in new entry creation
		libraryEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));

		libraryEntries.sort(sorter);

		children.remove(list);
		addListener(list = new GuiList<>(this, right - left, bottom - top - 34, 0, bottom - top - 34, 50, libraryEntries));
	}

	public void addSelectedEntry(LibraryListEntry entry) {
		list.getEventListeners().add(entry);
		list.setSelected(entry);
		parent.libraryInfoPanel.setEntry(entry);
	}

	public void removeEntry(LibraryListEntry entry) {
		Tails.proxy.getLibraryManager().removeEntry(entry.data);
		list.getEventListeners().remove(entry);
	}

	private List<LibraryListEntry> filterListEntries(String filter) {
		ArrayList<LibraryListEntry> filteredEntries = new ArrayList<>();
		List<LibraryListEntry> entries = new ArrayList<>();

		for (LibraryEntryData data : Tails.proxy.getLibraryManager().libraryEntries) {
			entries.add(new LibraryListEntry(this, data));
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
			if (entry1.equals(entry2))
				return 0;

			if (entry1 instanceof LibraryListEntry.NewLibraryListEntry)
				return -1;
			else if (entry2 instanceof LibraryListEntry.NewLibraryListEntry)
				return 1;

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
