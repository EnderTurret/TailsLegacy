package uk.kihira.tails.client.gui;

import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;

import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.io.IOException;
import java.util.UUID;

public class LibraryImportPanel extends Panel<GuiEditor> {

    TextFieldWidget inputField;

    public LibraryImportPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        Button button;

        //Import Skin
        button = new ExtendedButton(3, 3, width - 6, 18, new TranslationTextComponent("gui.library.import.skin"), b -> {
        	TextureHelper.buildPlayerPartsData(minecraft.player);
            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2, new TranslationTextComponent("gui.library.import.toast.skin").mergeStyle(TextFormatting.GREEN));
        });
        button.active = TextureHelper.hasSkinData(minecraft.player);
        addButton(button);

        addButton(new ExtendedButton(3, 21, width - 6, 18, new TranslationTextComponent("gui.library.import.string"), b -> {
        	if (Strings.isNullOrEmpty(inputField.getText()) || inputField.getText().split(":", 3).length != 3) {
                ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
                        new TranslationTextComponent("gui.library.import.toast.invalid").mergeStyle(TextFormatting.RED));
            }
            else {
                String[] strings = inputField.getText().split(":", 4);
                try {
                    LibraryEntryData entryData = new LibraryEntryData(UUID.fromString(strings[1]), strings[2], strings[0], Tails.gson.fromJson(strings[3], PartsData.class));
                    Tails.proxy.getLibraryManager().addEntry(entryData);
                    parent.libraryPanel.initList();

                    ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
                            new TranslationTextComponent("gui.library.import.toast.success", strings[0]).mergeStyle(TextFormatting.GREEN));

                } catch (IllegalArgumentException e) {
                    ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
                            new TranslationTextComponent("gui.library.import.toast.invalid.uuid").mergeStyle(TextFormatting.RED));
                } catch (JsonSyntaxException e) {
                    ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2,
                            new TranslationTextComponent("gui.library.import.toast.invalid.parts").mergeStyle(TextFormatting.RED));
                }
            }
        }));

        inputField = new TextFieldWidget(font, 3, 41, width - 6, 15, null);
        inputField.setMaxStringLength(5000);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        inputField.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        fillGradient(matrixStack, 0, 0, width, height, 0xDE000000, 0xDE000000);
        inputField.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
