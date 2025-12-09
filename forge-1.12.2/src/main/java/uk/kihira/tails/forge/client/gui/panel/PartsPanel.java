/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import uk.kihira.tails.common.client.duck.FakeTailsEntity;
import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.part.AttachmentPoint;
import uk.kihira.tails.common.client.part.AttachmentPoints;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.common.client.part.PartRegistry;
import uk.kihira.tails.common.client.part.RootAttachmentPoint;
import uk.kihira.tails.common.client.render.part.PartRenderer;
import uk.kihira.tails.common.part.ServerPartInfo;
import uk.kihira.tails.forge.client.gui.EditorScreen;
import uk.kihira.tails.forge.client.gui.TailsComponents;
import uk.kihira.tails.forge.client.gui.widget.ListWidget;
import uk.kihira.tails.forge.client.gui.widget.Spinner;
import uk.kihira.tails.forge.client.platform.TailsPoseStackImpl;

@Internal
public final class PartsPanel extends Panel {

	public static final int ROOT_ATTACHMENT = 500;
	public static final int ROOT_ATTACHMENT_PREV = 501;
	public static final int ROOT_ATTACHMENT_NEXT = 502;
	public static final int ATTACHMENT = 503;
	public static final int ATTACHMENT_PREV = 504;
	public static final int ATTACHMENT_NEXT = 505;

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
		addRenderableWidget(rootAttachment = new Spinner<>(ROOT_ATTACHMENT, AttachmentPoints.getRoots(), parent.getAttachmentPoint().root(),
				(right - left) / 2, 16, 108,
				ap -> ap.translationKey(), selection -> {
					parent.setRootAttachmentPoint(selection);
					attachment.setValues(selection.children());
					initPartList();
				}));

		addRenderableWidget(attachment = new Spinner<>(ATTACHMENT, rootAttachment.getSelection().children(), parent.getAttachmentPoint(),
				(right - left) / 2, 32, 108,
				ap -> ap.translationKey(), selection -> {
					parent.setAttachmentPoint(selection);
					initPartList();
				}));

		addRenderableWidget(rootAttachment.left);
		addRenderableWidget(rootAttachment.right);
		addRenderableWidget(attachment.left);
		addRenderableWidget(attachment.right);

		this.partList = new ListWidget<PartEntry>(
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
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);

		drawCenteredString(parent.font(), TailsComponents.PART_SELECT.getFormattedText(), (right - left) / 2, 5, 0xFFFFFF);
	}

	@Override
	public void removed() {
		// Delete textures on close.
		for (PartEntry entry : partList.getEntries())
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
			for (PartEntry entry : this.partList.getEntries())
				entry.partInfo.clearGlTexture();
		}

		this.partList.replaceEntries(partList);

		selectDefaultListEntry();
	}

	void selectDefaultListEntry() {
		// Default selection.
		final ClientPartInfo partInfo = parent.getEditingPartInfo();
		// Don't try to force a different selection for unknown parts.
		if (partInfo.getPart() == null) return;

		for (PartEntry entry : partList.getEntries())
			if (entry.partInfo.isEmpty() && partInfo.isEmpty() || !partInfo.isEmpty() && !entry.partInfo.isEmpty()
					&& entry.partInfo.getPart() == partInfo.getPart()) {
				partList.setSelected(entry);
				//onEntrySelected(partList.children().indexOf(entry), entry);
				break;
			}
	}

	private void renderPart(int x, int y, int z, int scale, ClientPartInfo partInfo, float partialTick) {
		if (partInfo.isEmpty() || partInfo.isInvalid()) return;

		final PartRenderer renderer = partInfo.getRenderer();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(-scale, scale, 1F);

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableLighting();

		renderer.compileTextureIfNeeded(fakeEntity, partInfo);

		renderer.render(
				TailsPoseStackImpl.INSTANCE,
				fakeEntity,
				null, partInfo,
				(TailsBufferSource) impl, (TailsBuffer) consumer,
				0, 0, 0, partialTick,
				1, 1, 0xFF);

		GlStateManager.enableLighting();

		GlStateManager.popMatrix();
	}

	class PartEntry implements GuiListExtended.IGuiListEntry {

		private final ClientPartInfo partInfo;

		PartEntry(ClientPartInfo partInfo) {
			this.partInfo = partInfo;
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
			GlStateManager.color(1, 1, 1, 1);

			if (!partInfo.isEmpty()) {
				final boolean currentPart = partList.isSelected(slotIndex);
				renderPart(right - 25 - 2, x - 25, currentPart ? 10 : 1, 50, partInfo, partialTick);
				drawString(parent.font(), I18n.format(partInfo.getPart().getTranslationKey()), 5, x + 17, 0xFFFFFF);

				if (currentPart && parent.getEditingPartInfo().getPartTexture() != null && parent.getEditingPartInfo().getSubType() != null) {
					final String author;

					if (parent.getEditingPartInfo().getPartTexture().author() != null)
						author = parent.getEditingPartInfo().getPartTexture().author();
					else if (parent.getEditingPartInfo().getSubType().author() != null)
						author = parent.getEditingPartInfo().getSubType().author();
					else author = null;

					if (author != null) {
						// Yeah its not nice but eh, works.
						GlStateManager.pushMatrix();
						GlStateManager.translate(5, x + 27, 0);
						GlStateManager.scale(0.6F, 0.6F, 1);
						parent.font().drawString(TailsComponents.PART_CREDIT.getFormattedText(), 0, 0, 0xFFFFFF);
						parent.font().drawString(TextFormatting.AQUA + author, 0, 10, 0xFFFFFF);
						GlStateManager.popMatrix();
					}
				}
			} else
				parent.font().drawString(TailsComponents.EMPTY_PART.getFormattedText(), 5, x + partList.getItemHeight() / 2 - 5, 0xFFFFFF);
		}

		@Override
		public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
			partList.setSelected(this);
			//onEntrySelected(partList.children().indexOf(this), this);
			return true;
		}

		@Override
		public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}

		@Override
		public void updatePosition(int slotIndex, int x, int y, float partialTick) {}
	}
}