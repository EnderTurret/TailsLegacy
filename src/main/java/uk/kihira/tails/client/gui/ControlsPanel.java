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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class ControlsPanel extends Panel<GuiEditor> {

	private boolean libraryMode = false;

	public ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
	}

	@Override
	public void init() {
		//Mode Switch
		addButton(new Button(3 + 10, bottom - top - 25, 46, 20, new TranslationTextComponent("gui.button.mode.library"), b -> {
			//TODO change parts data when switching? clear libraryinfo panel?
			libraryMode = !libraryMode;
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

			if (!libraryMode)
				Tails.setLocalPartsData(parent.getPartsData());
			parent.setPartsData(Tails.localPartsData);

			b.setMessage(libraryMode ? new TranslationTextComponent("gui.button.mode.editor") : new TranslationTextComponent("gui.button.mode.library"));
		}));
		//Reset/Save
		addButton(new Button((right - left) / 2 - 23, bottom - top - 25, 46, 20, new TranslationTextComponent("gui.button.reset"), b -> {
			PartInfo partInfo = parent.originalPartInfo.deepCopy();
			parent.partsPanel.selectDefaultListEntry();
			parent.libraryPanel.initList();
			parent.libraryInfoPanel.setEntry(null);
			parent.clearCurrTintEdit();
			parent.refreshTintPane();
			parent.setPartsInfo(partInfo);
		}));
		addButton(new Button(right - left - 49, bottom - top - 25, 46, 20, new TranslationTextComponent("gui.done"), b -> {
			//Update part info, set local and send it to the server
			final PartsData partsData = parent.getPartsData();
			Tails.setLocalPartsData(partsData);
			Tails.proxy.addPartsData(minecraft.player.getUniqueID(), partsData);
			Tails.networkWrapper.sendToServer(new PlayerDataMessage(minecraft.getSession().getProfile().getId(), partsData, false));
			ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, new StringTextComponent("Saved!").mergeStyle(TextFormatting.GREEN));
			minecraft.displayGuiScreen(null);
		}));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		fill(matrixStack, 0, 0, right - left, bottom - top, 0xDD000000);

		super.render(matrixStack, mouseX, mouseY, partialTicks);

		font.drawString(matrixStack, "Yes this is pog", left, top, 0xFFFFFF);
	}
}
