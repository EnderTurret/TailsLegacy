/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.gui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix3x2f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import net.enderturret.tailslegacy.common.client.duck.FakeTailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.part.AttachmentPoint;
import net.enderturret.tailslegacy.common.client.part.AttachmentPoints;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.Part;
import net.enderturret.tailslegacy.common.client.part.PartRegistry;
import net.enderturret.tailslegacy.common.client.part.PartTexture;
import net.enderturret.tailslegacy.common.client.part.RootAttachmentPoint;
import net.enderturret.tailslegacy.common.client.part.SubType;
import net.enderturret.tailslegacy.common.client.render.part.PartRenderer;
import net.enderturret.tailslegacy.common.part.ServerPartInfo;
import net.enderturret.tailslegacy.fabric.client.RenderHelper;
import net.enderturret.tailslegacy.fabric.client.gui.EditorScreen;
import net.enderturret.tailslegacy.fabric.client.gui.TailsComponents;
import net.enderturret.tailslegacy.fabric.client.gui.widget.ListWidget;
import net.enderturret.tailslegacy.fabric.client.gui.widget.Spinner;
import net.enderturret.tailslegacy.fabric.client.render.PartPreviewRenderState;

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
				55) {
			@Override
			public void onItemSelected(PartEntry item) {
				onEntrySelected(item);
			}
		};

		addRenderableWidget(this.partList);

		initPartList();
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		super.extractWidgetRenderState(gui, mouseX, mouseY, partialTick);

		gui.centeredText(parent.font(), TailsComponents.PART_SELECT, (right - left) / 2, 5, 0xFFFFFFFF);
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
			final SubType subType = oldInfo.getPart() == entry.partInfo.getPart() ? oldInfo.getSubType() : entry.partInfo.getSubType();
			final PartTexture texture = oldInfo.getPart() == entry.partInfo.getPart() ? oldInfo.getPartTexture() : subType.textures().get(0);

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

	private void renderPart(GuiGraphicsExtractor gui, int x, int y, int z, int scale, ClientPartInfo partInfo, float partialTick) {
		if (partInfo.isEmpty() || partInfo.isInvalid()) return;

		final PartRenderer renderer = partInfo.getRenderer();

		gui.pose().pushMatrix();
		gui.pose().translate(x, y);

		renderer.compileTextureIfNeeded(fakeEntity, partInfo);

		gui.guiRenderState.addPicturesInPictureState(new PartPreviewRenderState(
				fakeEntity, partInfo, partialTick, new Matrix3x2f(gui.pose()),
				-50, -100, 50, 100,
				gui.scissorStack.peek()));

		gui.pose().popMatrix();
	}

	public void tick() {
		for (PartEntry entry : partList.children())
			entry.partInfo.tickAnimator(fakeEntity);
	}

	class PartEntry extends ObjectSelectionList.Entry<PartEntry> {

		private final ClientPartInfo partInfo;

		PartEntry(ClientPartInfo partInfo) {
			this.partInfo = partInfo;
		}

		@Override
		public void extractContent(GuiGraphicsExtractor gui, int mouseX, int mouseY, boolean isHovering, float partialTick) {
			if (partInfo.isEmpty()) {
				gui.text(parent.font(), TailsComponents.EMPTY_PART, getX() + 5, getContentY() + partList.getItemHeight() / 2 - 5, 0xFFFFFFFF);
				return;
			}

			final boolean currentPart = partList.getSelected() == this;
			renderPart(gui, getContentRight() - 25, getContentY() - 25, currentPart ? 10 : 1, 50, partInfo, partialTick);

			int nameOffset = 0;

			final ClientPartInfo editingInfo = parent.getEditingPartInfo();
			if (currentPart && editingInfo.getPartTexture() != null && editingInfo.getSubType() != null) {
				String textureAuthor = null;
				String modelAuthor = null;

				if (editingInfo.getPartTexture().author() != null)
					textureAuthor = editingInfo.getPartTexture().author();
				if (editingInfo.getSubType().author() != null)
					modelAuthor = editingInfo.getSubType().author();

				if (textureAuthor != null && textureAuthor.equals(modelAuthor))
					textureAuthor = null;

				if (textureAuthor != null || modelAuthor != null) {
					final boolean twoAuthors = textureAuthor != null && modelAuthor != null;
					if (twoAuthors) nameOffset = -7;
					final String singleAuthor = textureAuthor != null ? textureAuthor : modelAuthor;

					// Yeah its not nice but eh, works.
					gui.pose().pushMatrix();
					gui.pose().translate(getX() + 5, getContentY() + 27 + nameOffset);
					gui.pose().scale(0.6F, 0.6F);

					if (!twoAuthors) {
						gui.text(parent.font(), textureAuthor != null ? TailsComponents.TEXTURE_CREDIT : TailsComponents.PART_CREDIT, 0, 0, 0xFFFFFFFF);
						gui.text(parent.font(), Component.literal(singleAuthor).withStyle(ChatFormatting.AQUA), 0, 10, 0xFFFFFFFF);
					} else {
						gui.text(parent.font(), TailsComponents.PART_CREDIT, 0, 0, 0xFFFFFFFF);
						gui.text(parent.font(), Component.literal(modelAuthor).withStyle(ChatFormatting.AQUA), 0, 10, 0xFFFFFFFF);
						gui.text(parent.font(), TailsComponents.TEXTURE_CREDIT, 0, 20, 0xFFFFFFFF);
						gui.text(parent.font(), Component.literal(textureAuthor).withStyle(ChatFormatting.AQUA), 0, 30, 0xFFFFFFFF);
					}

					gui.pose().popMatrix();
				}
			}

			RenderHelper.drawScrollingString(gui, gui.textRenderer(), parent.font(), Component.translatable(partInfo.getPart().getTranslationKey()), getX() + 5, getWidth() - 8, getContentY() + 17 + nameOffset);
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