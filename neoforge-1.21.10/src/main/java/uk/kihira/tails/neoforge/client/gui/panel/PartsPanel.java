/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix3x2f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.common.client.duck.FakeTailsEntity;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.part.AttachmentPoint;
import uk.kihira.tails.common.client.part.AttachmentPoints;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.common.client.part.PartRegistry;
import uk.kihira.tails.common.client.part.RootAttachmentPoint;
import uk.kihira.tails.common.client.render.part.PartRenderer;
import uk.kihira.tails.common.part.ServerPartInfo;
import uk.kihira.tails.neoforge.client.gui.EditorScreen;
import uk.kihira.tails.neoforge.client.gui.TailsComponents;
import uk.kihira.tails.neoforge.client.gui.widget.ListWidget;
import uk.kihira.tails.neoforge.client.gui.widget.Spinner;
import uk.kihira.tails.neoforge.client.render.PartPreviewRenderState;

@Internal
public final class PartsPanel extends Panel {

	private Spinner<RootAttachmentPoint> rootAttachment;
	private Spinner<AttachmentPoint> attachment;

	private ListWidget<PartEntry> partList;

	private final TailsEntity fakeEntity;
	private final int listTop = 32 + 15;

	public PartsPanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		fakeEntity = FakeTailsEntity.getInstance();
	}

	@Override
	public void init() {
		addRenderableWidget(rootAttachment = new Spinner<>(AttachmentPoints.getRoots(), parent.getAttachmentPoint().root(),
				(right - left) / 2, 16, 108,
				ap -> ap.translationKey(), selection -> {
					parent.setRootAttachmentPoint(selection);
					attachment.setValues(selection.children());
					initPartList();
				}));

		addRenderableWidget(attachment = new Spinner<>(rootAttachment.getSelection().children(), parent.getAttachmentPoint(),
				(right - left) / 2, 32, 108,
				ap -> ap.translationKey(), selection -> {
					parent.setAttachmentPoint(selection);
					initPartList();
				}));

		addRenderableWidget(rootAttachment.left);
		addRenderableWidget(rootAttachment.right);
		addRenderableWidget(attachment.left);
		addRenderableWidget(attachment.right);

		this.partList = new ListWidget<>(
				108 + 6, bottom - top - listTop,
				listTop,
				55, new ArrayList<>()) {
			@Override
			public void onItemSelected(PartEntry item) {
				onEntrySelected(item);
			}
		};

		addRenderableWidget(this.partList);

		initPartList();
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		super.renderWidget(gui, mouseX, mouseY, partialTick);

		gui.drawCenteredString(parent.font(), TailsComponents.PART_SELECT, (right - left) / 2, 5, 0xFFFFFFFF);
	}

	@Override
	public void removed() {
		// Delete textures on close.
		for (PartEntry entry : partList.children())
			entry.partInfo.clearGlTexture();
	}

	public boolean onEntrySelected(PartEntry entry) {
		final ClientPartInfo oldInfo = parent.getEditingPartInfo();

		// Need to keep tints from original part.
		final ClientPartInfo partInfo;
		if (entry.partInfo.isEmpty()) partInfo = entry.partInfo.clone();
		else {
			final Part.SubType subType = oldInfo.getPart() == entry.partInfo.getPart() ? oldInfo.getSubType() : entry.partInfo.getSubType();
			final Part.PartTexture texture = oldInfo.getPart() == entry.partInfo.getPart() ? oldInfo.getPartTexture() : subType.textures().get(0);

			final String subId = subType != null ? subType.id() : oldInfo.getSubTypeId();
			final String textureId = texture != null ? texture.id() : oldInfo.getTextureId();
			final ServerPartInfo spi = new ServerPartInfo(entry.partInfo.getPart().getId(), subId, textureId, oldInfo.getTints().clone());
			partInfo = new ClientPartInfo(spi, PartRegistry.reference(entry.partInfo.getPart().getId()), subId, textureId);
		}

		// Breaks immutability, but it's probably fine, right?
		if (entry.partInfo.isEmpty())
			for (int i = 0; i < partInfo.getTints().length; i++)
				partInfo.getTints()[i] = oldInfo.getTints()[i];

		parent.setPartsInfo(partInfo);

		return true;
	}

	public void initPartList() {
		// Part List
		final List<PartEntry> partList = new ArrayList<>();
		final AttachmentPoint attachment = parent.getAttachmentPoint();

		partList.add(new PartEntry(ClientPartInfo.empty())); // No tail

		// Generate tail preview textures and add to list.
		final List<Part> parts = PartRegistry.getParts(attachment);

		for (int type = 0; type < parts.size(); type++) {
			final Part part = parts.get(type);
			final ClientPartInfo partInfo = part.makeDefaultPartInfo(part.getSubTypes().get(0));
			partList.add(new PartEntry(partInfo));
		}

		if (this.partList != null) {
			// Dispose of textures in old part list.
			for (PartEntry entry : this.partList.children())
				entry.partInfo.clearGlTexture();
		}

		this.partList.replaceEntries(partList);
		this.partList.setScrollAmount(0);

		selectDefaultListEntry();
	}

	void selectDefaultListEntry() {
		// Default selection.
		final ClientPartInfo partInfo = parent.getEditingPartInfo();
		// Don't try to force a different selection for unknown parts.
		if (partInfo.getPart() == null) return;

		for (PartEntry entry : partList.children())
			if (entry.partInfo.isEmpty() && partInfo.isEmpty() || !partInfo.isEmpty() && !entry.partInfo.isEmpty()
					&& entry.partInfo.getPart() == partInfo.getPart()) {
				partList.setSelected(entry);
				//onEntrySelected(partList.children().indexOf(entry), entry);
				break;
			}
	}

	private void renderPart(GuiGraphics gui, int x, int y, int z, int scale, ClientPartInfo partInfo, float partialTick) {
		if (partInfo.isEmpty() || partInfo.isInvalid()) return;

		final PartRenderer renderer = partInfo.getRenderer();

		gui.pose().pushMatrix();
		gui.pose().translate(x, y);

		renderer.compileTextureIfNeeded(fakeEntity, partInfo);

		gui.submitPictureInPictureRenderState(new PartPreviewRenderState(
				fakeEntity, partInfo, partialTick, new Matrix3x2f(gui.pose()),
				-50, -100, 50, 100,
				gui.peekScissorStack()));

		gui.pose().popMatrix();
	}

	class PartEntry extends ObjectSelectionList.Entry<PartEntry> {

		private final ClientPartInfo partInfo;

		PartEntry(ClientPartInfo partInfo) {
			this.partInfo = partInfo;
		}

		@Override
		public void renderContent(GuiGraphics gui, int mouseX, int mouseY, boolean isHovering, float partialTick) {
			if (!partInfo.isEmpty()) {
				final boolean currentPart = partList.getSelected() == this;
				renderPart(gui, getContentRight() - 25, getContentY() - 25, currentPart ? 10 : 1, 50, partInfo, partialTick);
				gui.drawString(parent.font(), I18n.get(partInfo.getPart().getTranslationKey()), getX() + 5, getContentY() + 17, 0xFFFFFFFF);

				if (currentPart && parent.getEditingPartInfo().getPartTexture() != null && parent.getEditingPartInfo().getSubType() != null) {
					final String author;

					if (parent.getEditingPartInfo().getPartTexture().author() != null)
						author = parent.getEditingPartInfo().getPartTexture().author();
					else if (parent.getEditingPartInfo().getSubType().author() != null)
						author = parent.getEditingPartInfo().getSubType().author();
					else author = null;

					if (author != null) {
						// Yeah its not nice but eh, works.
						gui.pose().pushMatrix();
						gui.pose().translate(getX() + 5, getContentY() + 27);
						gui.pose().scale(0.6F, 0.6F);
						gui.drawString(parent.font(), TailsComponents.PART_CREDIT, 0, 0, 0xFFFFFFFF);
						gui.drawString(parent.font(), Component.literal(author).withStyle(ChatFormatting.AQUA), 0, 10, 0xFFFFFFFF);
						gui.pose().popMatrix();
					}
				}
			} else
				gui.drawString(parent.font(), TailsComponents.EMPTY_PART, getX() + 5, getContentY() + partList.getItemHeight() / 2 - 5, 0xFFFFFFFF);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
			partList.setSelected(this);
			return true;
		}

		@Override
		public Component getNarration() {
			return Component.empty();
		}
	}
}