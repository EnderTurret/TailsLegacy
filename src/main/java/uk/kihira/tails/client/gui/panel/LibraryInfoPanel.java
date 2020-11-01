package uk.kihira.tails.client.gui.panel;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.LibraryListEntry;
import uk.kihira.tails.client.gui.widget.IconButton;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.LibraryEntriesMessage;
import uk.kihira.tails.common.network.LibraryRequestMessage;

public class LibraryInfoPanel extends Panel<EditorScreen> {

	private LibraryListEntry entry;

	private TextFieldWidget textField;
	private IconButton.GuiIconToggleButton favButton;
	private IconButton deleteButton;
	private IconButton downloadButton;
	private IconButton uploadButton;

	public LibraryInfoPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		textField = new TextFieldWidget(font, 6, 6, right - left - 12, 15, null);
		textField.setMaxStringLength(16);
		addListener(textField);

		addButton(favButton = new IconButton.GuiIconToggleButton(5, bottom - top - 20, IconButton.Icons.STAR, b -> {
			entry.data.favourite = ((IconButton.GuiIconToggleButton) b).toggled;
		}, new TranslationTextComponent("gui.button.favourite")));
		addButton(deleteButton = new IconButton(21, bottom - top - 20, IconButton.Icons.DELETE, b -> {
			// Only allow removing if player owns the entry.
			if (entry.data.remoteEntry && !entry.data.creatorUUID.equals(minecraft.player.getUniqueID()))
				return;
			((IconButton) b).setHover(false);
			parent.getLibraryPanel().removeEntry(entry);
			setEntry(null);
		}, new TranslationTextComponent("gui.button.delete")));
		addButton(uploadButton = new IconButton(36, bottom - top - 20, IconButton.Icons.UPLOAD, b -> {
			Tails.CHANNEL.sendToServer(new LibraryEntriesMessage(new ArrayList<LibraryEntryData>() {{ add(entry.data); }}, false));
			b.active = false;
		}, new TranslationTextComponent("gui.button.upload")));
		addButton(downloadButton = new IconButton(53, bottom - top - 20, IconButton.Icons.DOWNLOAD, b -> {
			entry.data.remoteEntry = false;
			b.active = false;
		}, new TranslationTextComponent("gui.button.savelocal")));
		addButton(new IconButton(68, bottom - top - 20, IconButton.Icons.EXPORT, b -> {
			final StringBuilder sb = new StringBuilder();
			final LibraryEntryData libData = getEntry().data;
			sb.append(libData.entryName).append(":");
			sb.append(libData.creatorUUID).append(":");
			sb.append(Tails.GSON.toJson(libData.partsData));

			ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, new TranslationTextComponent("gui.library.info.toast.export"));
			GLFW.glfwSetClipboardString(minecraft.getMainWindow().getHandle(), sb.toString());
		}, new TranslationTextComponent("gui.button.share")));

		super.init();

		// Only request library if on remote server.
		if (!Minecraft.getInstance().isIntegratedServerRunning())
			Tails.CHANNEL.sendToServer(new LibraryRequestMessage());

		setEntry(null);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		setBlitOffset(0);
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);

		RenderSystem.color4f(0F, 0F, 0F, 0F);

		setBlitOffset(10);
		fillGradient(matrixStack, 3, 3, right - left - 3, bottom - top - 3, 0xFF000000, 0xFF000000);

		if (entry != null) {
			textField.render(matrixStack, mouseX, mouseY, partialTicks);

			font.drawString(matrixStack, I18n.format("gui.library.info.created") + ":", 5, bottom - top - 59, 0xAAAAAA);
			font.drawString(matrixStack, entry.data.creatorName, right - left - 5 - font.getStringWidth(entry.data.creatorName), bottom - top - 50, 0xAAAAAA);
			font.drawString(matrixStack, I18n.format("gui.library.info.createdate") + ":", 5, bottom - top - 41, 0xAAAAAA);
			final String date = new SimpleDateFormat("dd/MM/YY").format(new Date(entry.data.creationDate));
			font.drawString(matrixStack, date, right - left - 5 - font.getStringWidth(date), bottom - top - 32, 0xAAAAAA);
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		final boolean handled = super.keyPressed(keyCode, scanCode, modifiers);

		if (handled && entry != null) {
			entry.data.entryName = textField.getText();
			Tails.PROXY.getLibraryManager().saveLibrary();
		}

		return handled || textField.canWrite();
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		final boolean handled = super.charTyped(codePoint, modifiers);

		if (handled && entry != null) {
			entry.data.entryName = textField.getText();
			Tails.PROXY.getLibraryManager().saveLibrary();
		}

		return handled;
	}

	public void setEntry(LibraryListEntry entry) {
		this.entry = entry;
		if (entry == null) {
			textField.setVisible(false);
			for (Widget button : buttons)
				button.visible = false;
		}
		else {
			favButton.toggled = entry.data.favourite;
			textField.setVisible(true);
			textField.setText(entry.data.entryName);
			for (Widget button : buttons) {
				button.visible = true;

				if (button == deleteButton && entry.data.remoteEntry && !entry.data.creatorUUID.equals(minecraft.player.getUniqueID()))
					button.visible = false;
				// Download
				else if (button == downloadButton && !entry.data.remoteEntry)
					button.visible = false;
				// Upload
				else if (button == uploadButton && (entry.data.remoteEntry || minecraft.isSingleplayer() || !Tails.hasRemote))
					button.visible = false;
			}
		}
	}

	public LibraryListEntry getEntry() {
		return entry;
	}
}
