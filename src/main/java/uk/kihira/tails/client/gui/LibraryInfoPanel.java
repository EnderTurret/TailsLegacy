package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.LibraryEntriesMessage;
import uk.kihira.tails.common.network.LibraryRequestMessage;

import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LibraryInfoPanel extends Panel<GuiEditor> {

    private LibraryListEntry entry;

    private TextFieldWidget textField;
    private GuiIconButton.GuiIconToggleButton favButton;
    private GuiIconButton deleteButton;
    private GuiIconButton downloadButton;
    private GuiIconButton exportButton;

    public LibraryInfoPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
        //Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void init() {
        textField = new TextFieldWidget(font, 6, 6, width - 12, 15, null);
        textField.setMaxStringLength(16);

        addButton(favButton = new GuiIconButton.GuiIconToggleButton(5, height - 20, GuiIconButton.Icons.STAR, b -> {
        	entry.data.favourite = ((GuiIconButton.GuiIconToggleButton) b).toggled;
        }, new TranslationTextComponent("gui.button.favourite")));
        addButton(deleteButton = new GuiIconButton(21, height - 20, GuiIconButton.Icons.DELETE, b -> {
        	if (entry.data.remoteEntry) {
                //Only allow removing if player owns the entry
                if (!entry.data.creatorUUID.equals(minecraft.player.getUniqueID())) {
                    return;
                }
            }
            ((GuiIconButton) b).setHover(false);
            parent.libraryPanel.removeEntry(entry);
            setEntry(null);
        }, new TranslationTextComponent("gui.button.delete")));
        addButton(new GuiIconButton(36, height - 20, GuiIconButton.Icons.UPLOAD, b -> {
            Tails.networkWrapper.sendToServer(new LibraryEntriesMessage(new ArrayList<LibraryEntryData>() {{ add(entry.data); }}, false));
            b.active = false;
        }, new TranslationTextComponent("gui.button.upload")));
        addButton(downloadButton = new GuiIconButton(53, height - 20, GuiIconButton.Icons.DOWNLOAD, b -> {
        	entry.data.remoteEntry = false;
            b.active = false;
        }, new TranslationTextComponent("gui.button.savelocal")));
        addButton(exportButton = new GuiIconButton(68, height - 20, GuiIconButton.Icons.EXPORT, b -> {
        	StringBuilder sb = new StringBuilder();
            LibraryEntryData libData = parent.libraryInfoPanel.getEntry().data;
            sb.append(libData.entryName).append(":");
            sb.append(libData.creatorUUID).append(":");
            sb.append(Tails.gson.toJson(libData.partsData));

            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, new TranslationTextComponent("gui.library.info.toast.export"));
            GLFW.glfwSetClipboardString(minecraft.getMainWindow().getHandle(), sb.toString());
        }, new TranslationTextComponent("gui.button.share")));
        super.init();

        //Only request library if on remote server
        if (!Minecraft.getInstance().isIntegratedServerRunning()) {
            Tails.networkWrapper.sendToServer(new LibraryRequestMessage());
        }

        setEntry(null);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        setBlitOffset(0);
        fillGradient(matrixStack, 0, 0, width, height, 0xCC000000, 0xCC000000);

        RenderSystem.color4f(0F, 0F, 0F, 0F);

        setBlitOffset(10);
        fillGradient(matrixStack, 3, 3, width - 3, height - 3, 0xFF000000, 0xFF000000);

        if (entry != null) {
            textField.render(matrixStack, mouseX, mouseY, partialTicks);

            //font.setUnicodeFlag(true);
            font.drawString(matrixStack, I18n.format("gui.library.info.created") + ":", 5, height - 40, 0xAAAAAA);
            font.drawString(matrixStack, entry.data.creatorName, width - 5 - font.getStringWidth(entry.data.creatorName), height - 40, 0xAAAAAA);
            font.drawString(matrixStack, I18n.format("gui.library.info.createdate") + ":", 5, height - 32, 0xAAAAAA);
            String date = new SimpleDateFormat("dd/MM/YY").format(new Date(entry.data.creationDate));
            font.drawString(matrixStack, date, width - 5 - font.getStringWidth(date), height - 32, 0xAAAAAA);
            //font.drawSplitString(EnumChatFormatting.ITALIC + entry.data.comment, 5, 40, width, 0xFFFFFF);
            //font.setUnicodeFlag(false);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        textField.keyPressed(keyCode, scanCode, modifiers);

        if (textField.isFocused() && entry != null) {
            entry.data.entryName = textField.getText();
            Tails.proxy.getLibraryManager().saveLibrary();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        //Keyboard.enableRepeatEvents(true);
        super.onClose();
    }

    public void setEntry(LibraryListEntry entry) {
        this.entry = entry;
        if (entry == null) {
            textField.setVisible(false);
            for (Object button : buttons) {
                ((Widget) button).visible = false;
            }
        }
        else {
            favButton.toggled = entry.data.favourite;
            textField.setVisible(true);
            textField.setText(entry.data.entryName);
            for (Object obj : buttons) {
                Widget button = (Widget) obj;
                button.visible = true;

                if (button == deleteButton && entry.data.remoteEntry && !entry.data.creatorUUID.equals(minecraft.player.getUniqueID())) {
                    button.visible = false;
                }
                //Download
                else if (button == downloadButton && !entry.data.remoteEntry) {
                    button.visible = false;
                }
                //Upload
                else if (button == exportButton && (entry.data.remoteEntry || minecraft.isSingleplayer() || !Tails.hasRemote)) {
                    button.visible = false;
                }
            }
        }
    }

    public LibraryListEntry getEntry() {
        return this.entry;
    }
}
