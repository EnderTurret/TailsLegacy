/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */
package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class ControlsPanel extends Panel<GuiEditor> {

    public ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        //Mode Switch
        addButton(new Button(3, height - 25, 46, 20, new TranslationTextComponent("gui.button.mode.library"), b -> {
            //TODO change parts data when switching? clear libraryinfo panel?
            boolean libraryMode = b.getMessage().getUnformattedComponentText().equals("gui.button.mode.library");
            parent.partsPanel.enabled = !libraryMode;
            parent.texturePanel.enabled = !libraryMode;
            parent.tintPanel.enabled = !libraryMode;

            parent.libraryInfoPanel.enabled = libraryMode;
            parent.libraryPanel.enabled = libraryMode;
            parent.libraryImportPanel.enabled = libraryMode;

            parent.partsPanel.selectDefaultListEntry();
            parent.libraryPanel.initList();
            parent.libraryInfoPanel.setEntry(null);
            parent.clearCurrTintEdit();
            parent.refreshTintPane();

            if (!libraryMode) {
                Tails.setLocalPartsData(parent.getPartsData());
            }
            parent.setPartsData(Tails.localPartsData);

            b.setMessage(libraryMode ? new TranslationTextComponent("gui.button.mode.editor") : new TranslationTextComponent("gui.button.mode.library"));
        }));
        //Reset/Save
        addButton(new Button(width/2 - 23, height - 25, 46, 20, new TranslationTextComponent("gui.button.reset"), b -> {
            PartInfo partInfo = parent.originalPartInfo.deepCopy();
            parent.partsPanel.selectDefaultListEntry();
            parent.libraryPanel.initList();
            parent.libraryInfoPanel.setEntry(null);
            parent.clearCurrTintEdit();
            parent.refreshTintPane();
            parent.setPartsInfo(partInfo);
        }));
        addButton(new Button(width - 49, height - 25, 46, 20, new TranslationTextComponent("gui.done"), b -> {
            //Update part info, set local and send it to the server
        	final PartsData partsData = parent.getPartsData();
            Tails.setLocalPartsData(partsData);
            Tails.proxy.addPartsData(minecraft.player.getUniqueID(), partsData);
            Tails.networkWrapper.sendToServer(new PlayerDataMessage(minecraft.getSession().getProfile().getId(), partsData, false));
            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, new StringTextComponent("Saved!").mergeStyle(TextFormatting.GREEN));
            this.minecraft.displayGuiScreen(null);
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        fill(matrixStack, 0, 0, width, height, 0xDD000000);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
