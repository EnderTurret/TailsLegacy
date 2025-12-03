/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.panel;

import java.util.Date;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.common.client.gui.TailsIcons;
import uk.kihira.tails.common.client.gui.panel.BaseLibraryInfoPanel;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.neoforge.client.RenderHelper;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;
import uk.kihira.tails.neoforge.client.gui.LibraryListEntry;
import uk.kihira.tails.neoforge.client.gui.TailsComponents;
import uk.kihira.tails.neoforge.client.gui.widget.IconButton;
import uk.kihira.tails.neoforge.client.toast.ToastManager;

@Internal
public final class LibraryInfoPanel extends Panel implements BaseLibraryInfoPanel {

	private LibraryListEntry entry;

	private EditBox textField;
	private IconButton.Toggle favButton;
	private IconButton deleteButton;

	public LibraryInfoPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		textField = new EditBox(parent.font(), left + 6, top + 6, right - left - 12, 15, Component.empty());
		textField.setMaxLength(19);
		addRenderableWidget(textField);
		textField.setResponder(str -> {
			if (entry != null) {
				entry.data.entryName = str;
				parent.getLibraryPanel().libraryChanged = true;
			}
		});

		addRenderableWidget(favButton = new IconButton.Toggle(left + 5, bottom - 20, TailsIcons.STAR, b -> {
			entry.data.favourite = favButton.toggled;
		})).setTooltip(Tooltip.create(TailsComponents.FAVORITE_BUTTON));

		addRenderableWidget(deleteButton = new IconButton(left + 21, bottom - 20, TailsIcons.DELETE, b -> {
			deleteButton.setHover(false);
			parent.getLibraryPanel().removeEntry(entry);
			setEntry(null);
		})).setTooltip(Tooltip.create(TailsComponents.DELETE_BUTTON));

		addRenderableWidget(new IconButton(left + 68, bottom - 20, TailsIcons.EXPORT, b -> {
			final String export = exportString(getEntry().data);

			ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height / 2, parent.width / 2, TailsComponents.EXPORTED_MESSAGE);
			GLFW.glfwSetClipboardString(parent.getMinecraft().getWindow().getWindow(), export);
		})).setTooltip(Tooltip.create(TailsComponents.SHARE_BUTTON));

		setEntry(null);
	}

	@Override
	public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(gui, mouseX, mouseY, partialTick);
		gui.fillGradient(left + 3, top + 3, right - 3, bottom - 3, 0, 0xFF000000, 0xFF000000);
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		super.renderWidget(gui, mouseX, mouseY, partialTick);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		if (entry != null) {
			int index = 0;

			final int xOffset = left;
			final int yOffset = top;
			for (ClientPartInfo partInfo : ((ClientPartsData) entry.data.partsData).getParts()) {
				String trans = partInfo.getPart() == null ? partInfo.getPartId().toString() : I18n.get(partInfo.getPart().getTranslationKey());
				RenderHelper.drawStringMultiLine(gui, parent.font(), trans,
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4),
						0xFFFFFF);

				trans = partInfo.getSubType() == null ? partInfo.getSubTypeId().toString() : I18n.get(partInfo.getSubTypeTranslationKey());
				RenderHelper.drawStringMultiLine(gui, parent.font(), trans,
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 1),
						0xFFFFFF);

				trans = partInfo.getPartTexture() == null ? partInfo.getTextureId().toString() : I18n.get(partInfo.getTextureTranslationKey());
				RenderHelper.drawStringMultiLine(gui, parent.font(), trans,
						xOffset + 5,
						yOffset + 32 + 8 * (index * 4 + 2),
						0xFFFFFF);

				for (int i = 1; i < 4; i++)
					gui.fill(
							xOffset + (right - left) - 4 - 8 * i,
							yOffset + 32 + (index * 4 + 3) * 8,
							xOffset + (right - left) - 4 + 7 - 8 * i,
							yOffset + 32 + 7 + (index * 4 + 3) * 8,
							0xFF000000 | partInfo.getTints()[i - 1]);

				index++;
			}

			gui.drawString(parent.font(), TailsComponents.LIBRARY_ENTRY_CREATOR, left + 5, bottom - 59, 0xAAAAAA);
			gui.drawString(parent.font(), entry.data.creatorName, right - 5 - parent.font().width(entry.data.creatorName), bottom - 50, 0xAAAAAA);
			gui.drawString(parent.font(), TailsComponents.LIBRARY_ENTRY_CREATION_DATE, left + 5, bottom - 41, 0xAAAAAA);
			final String date = DATE_FORMAT.format(new Date(entry.data.creationDate));
			gui.drawString(parent.font(), date, right - 5 - parent.font().width(date), bottom - 32, 0xAAAAAA);
		}
	}

	public void setEntry(@Nullable LibraryListEntry entry) {
		this.entry = entry;

		final boolean visible = entry != null;

		if (visible) {
			favButton.toggled = entry.data.favourite;
			textField.setValue(entry.data.entryName);
		}

		for (AbstractWidget widget : renderables)
			widget.visible = visible;
	}

	@Nullable
	public LibraryListEntry getEntry() {
		return entry;
	}
}