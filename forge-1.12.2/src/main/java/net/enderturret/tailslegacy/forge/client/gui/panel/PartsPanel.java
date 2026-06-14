/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

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
import net.enderturret.tailslegacy.forge.client.gui.EditorScreen;
import net.enderturret.tailslegacy.forge.client.gui.TailsComponents;
import net.enderturret.tailslegacy.forge.client.gui.panel.PartsPanel.PartEntry;
import net.enderturret.tailslegacy.forge.client.gui.widget.ListWidget;
import net.enderturret.tailslegacy.forge.client.gui.widget.Spinner;
import net.enderturret.tailslegacy.forge.client.platform.TailsPoseStackImpl;
import net.enderturret.tailslegacy.forge.client.platform.TailsTessellatorWrapper;

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
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case ROOT_ATTACHMENT_PREV:
				rootAttachment.previous(); break;
			case ROOT_ATTACHMENT_NEXT:
				rootAttachment.next(); break;
			case ATTACHMENT_PREV:
				attachment.previous(); break;
			case ATTACHMENT_NEXT:
				attachment.next(); break;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);

		drawCenteredString(parent.font(), TailsComponents.PART_SELECT.getFormattedText(), (right - left) / 2, 5, 0xFFFFFFFF);
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

		parent.mc.getTextureManager().bindTexture((ResourceLocation) partInfo.getTexture());

		renderer.render(
				TailsPoseStackImpl.INSTANCE,
				fakeEntity,
				null, partInfo,
				TailsTessellatorWrapper.get(), TailsTessellatorWrapper.get(),
				0, 0, 0, partialTick,
				1, 1, 0xFF);

		GlStateManager.popMatrix();
	}

	public void tick() {
		for (PartEntry entry : partList.getEntries())
			entry.partInfo.tickAnimator(fakeEntity);
	}

	class PartEntry implements GuiListExtended.IGuiListEntry {

		private final ClientPartInfo partInfo;

		PartEntry(ClientPartInfo partInfo) {
			this.partInfo = partInfo;
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
			GlStateManager.color(1, 1, 1, 1);

			if (partInfo.isEmpty()) {
				parent.font().drawString(TailsComponents.EMPTY_PART.getFormattedText(), 5, y + partList.getItemHeight() / 2 - 5, 0xFFFFFFFF);
				return;
			}

			final boolean currentPart = partList.isSelected(slotIndex);
			renderPart(right - 25 - 2, y - 25, currentPart ? 10 : 1, 50, partInfo, partialTick);

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
					GlStateManager.pushMatrix();
					GlStateManager.translate(5, y + 27 + nameOffset, 0);
					GlStateManager.scale(0.6F, 0.6F, 1);

					if (!twoAuthors) {
						parent.font().drawString((textureAuthor != null ? TailsComponents.TEXTURE_CREDIT : TailsComponents.PART_CREDIT).getFormattedText(), 0, 0, 0xFFFFFFFF);
						parent.font().drawString(TextFormatting.AQUA + singleAuthor, 0, 10, 0xFFFFFFFF);
					} else {
						parent.font().drawString(TailsComponents.PART_CREDIT.getFormattedText(), 0, 0, 0xFFFFFFFF);
						parent.font().drawString(TextFormatting.AQUA + modelAuthor, 0, 10, 0xFFFFFFFF);
						parent.font().drawString(TailsComponents.TEXTURE_CREDIT.getFormattedText(), 0, 20, 0xFFFFFFFF);
						parent.font().drawString(TextFormatting.AQUA + textureAuthor, 0, 30, 0xFFFFFFFF);
					}

					GlStateManager.popMatrix();
				}
			}

			drawString(parent.font(), I18n.format(partInfo.getPart().getTranslationKey()), 5, y + 17 + nameOffset, 0xFFFFFFFF);
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