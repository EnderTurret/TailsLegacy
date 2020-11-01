package uk.kihira.tails.client.gui.panel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.gui.widget.ListWidget;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.LibraryListEntry;
import uk.kihira.tails.client.gui.LibraryListEntry.NewLibraryListEntry;
import uk.kihira.tails.client.gui.widget.IListCallback;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;

public class LibraryPanel extends Panel<EditorScreen> implements IListCallback<LibraryListEntry> {

	private static final LibrarySorter sorter = new LibrarySorter();
	private ListWidget<LibraryListEntry> list;
	private TextFieldWidget searchField;

	public LibraryPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	public ListWidget<LibraryListEntry> getList() {
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

		Minecraft.getInstance().getTextureManager().bindTexture(IconButton.iconsTextures);

		matrixStack.push();

		RenderSystem.color4f(1f, 1f, 1f, 1f);
		matrixStack.translate(right - left - 16, bottom - top - 32, 0);
		matrixStack.scale(0.75F, 0.75F, 0F);

		blit(matrixStack, 0, 0, 160, 0, 16, 16);

		matrixStack.pop();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		final boolean value = super.keyPressed(keyCode, scanCode, modifiers);

		if (value) {
			final List<LibraryListEntry> newEntries = filterListEntries(searchField.getText().toLowerCase());
			newEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));
			list.getEventListeners().clear();
			list.getEventListeners().addAll(newEntries);
		}

		return value;
	}

	@Override
	public boolean onEntrySelected(ListWidget guiList, int index, LibraryListEntry entry) {
		if (!(entry instanceof LibraryListEntry.NewLibraryListEntry)) {
			parent.getLibraryInfoPanel().setEntry(entry);
			parent.setPartsData(entry.data.partsData.deepCopy());
		}
		return true;
	}

	public void initList() {
		final List<LibraryListEntry> libraryEntries = new ArrayList<>();
		for (LibraryEntryData data : Tails.PROXY.getLibraryManager().libraryEntries)
			libraryEntries.add(new LibraryListEntry(this, data));

		// Add in new entry creation.
		libraryEntries.add(0, new LibraryListEntry.NewLibraryListEntry(this, null));

		libraryEntries.sort(sorter);

		children.remove(list);
		addListener(list = new ListWidget<>(this, right - left, bottom - top - 34, 0, bottom - top - 34, 50, libraryEntries));
	}

	public void addSelectedEntry(LibraryListEntry entry) {
		list.getEventListeners().add(entry);
		list.setSelected(entry);
		parent.getLibraryInfoPanel().setEntry(entry);
	}

	public void removeEntry(LibraryListEntry entry) {
		Tails.PROXY.getLibraryManager().removeEntry(entry.data);
		list.getEventListeners().remove(entry);
	}

	private List<LibraryListEntry> filterListEntries(String filter) {
		final ArrayList<LibraryListEntry> filteredEntries = new ArrayList<>();
		final List<LibraryListEntry> entries = new ArrayList<>();

		for (LibraryEntryData data : Tails.PROXY.getLibraryManager().libraryEntries)
			entries.add(new LibraryListEntry(this, data));

		for (LibraryListEntry entry : entries)
			if (entry instanceof LibraryListEntry.NewLibraryListEntry || entry.data.entryName.toLowerCase().contains(filter))
				filteredEntries.add(entry);
		return filteredEntries;
	}

	@Override
	public void onClose() {
		Tails.PROXY.getLibraryManager().removeRemoteEntries();
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

			// Put favorites at the top.
			if (entry1.data.favourite && !entry2.data.favourite)
				return -1;
			else if (!entry1.data.favourite && entry2.data.favourite)
				return 1;

			return 0;
		}
	}
}
