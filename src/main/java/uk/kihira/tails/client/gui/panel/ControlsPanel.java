/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.proxy.CommonProxy;

public class ControlsPanel extends Panel<EditorScreen> {

	private boolean libraryMode = false;

	public ControlsPanel(EditorScreen parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
	}

	@Override
	public void init() {
		// Mode Switch
		addRenderableWidget(new Button(3, bottom - top - 25, 46, 20, Component.translatable("tails.gui.button.mode.library"), b -> {
			libraryMode = !libraryMode;
			parent.getPartPanel().enabled = !libraryMode;
			parent.getTexturePanel().enabled = !libraryMode;
			parent.getTintPanel().enabled = !libraryMode;

			parent.getLibraryInfoPanel().enabled = libraryMode;
			parent.getLibraryPanel().enabled = libraryMode;
			parent.getLibraryImportPanel().enabled = libraryMode;

			parent.getPartPanel().selectDefaultListEntry();
			parent.getLibraryPanel().initList();
			parent.getLibraryInfoPanel().setEntry(null);
			parent.getTintPanel().setEditingTint(0);
			parent.refreshTintPane();

			if (!libraryMode)
				Tails.setLocalPartsData(parent.getPartsData(), null);

			parent.setPartsData(Tails.localPartsData);

			b.setMessage(libraryMode ? Component.translatable("tails.gui.button.mode.editor") : Component.translatable("tails.gui.button.mode.library"));
		}));
		// Reset/Save
		addRenderableWidget(new Button((right - left) / 2 - 23, bottom - top - 25, 46, 20, Component.translatable("tails.gui.button.reset"), b -> {
			final PartInfo partInfo = parent.getOriginalPartInfo().deepCopy();
			parent.getPartPanel().selectDefaultListEntry();
			parent.getLibraryPanel().initList();
			parent.getLibraryInfoPanel().setEntry(null);
			parent.getTintPanel().setEditingTint(0);
			parent.refreshTintPane();
			parent.setPartsInfo(partInfo);
		}));
		addRenderableWidget(new Button(right - left - 49, bottom - top - 25, 46, 20, Component.translatable("tails.gui.done"), b -> {
			// Update part info, set local and send it to the server.
			final PartsData partsData = parent.getPartsData();

			Tails.setLocalPartsData(partsData, null);
			Tails.PROXY.addPartsData(ClientUtils.getPlayerUUID(), partsData);

			Tails.CHANNEL.sendToServer(new PlayerDataMessage(ClientUtils.getPlayerUUID(), partsData));

			if (CommonProxy.sync != null)
				CommonProxy.sync.upload(ClientUtils.getPlayerUUID(), Tails.localPartsData);

			ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, Component.translatable("tails.gui.saved").withStyle(ChatFormatting.GREEN));

			minecraft.setScreen(null);
		}));
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		poseStack.pushPose();

		poseStack.translate(0, 0, -400);

		fill(poseStack, 0, 0, right - left, bottom - top, 0xDD000000);

		super.render(poseStack, mouseX, mouseY, partialTicks);

		poseStack.popPose();
	}
}