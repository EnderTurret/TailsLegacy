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

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.enderturret.tailslegacy.common.client.duck.FakeTailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsBuffer;
import net.enderturret.tailslegacy.common.client.duck.TailsBufferSource;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
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
import net.enderturret.tailslegacy.forge.client.gui.widget.ListWidget;
import net.enderturret.tailslegacy.forge.client.gui.widget.Spinner;
import net.enderturret.tailslegacy.forge.client.render.RenderStates;

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
		//this.partList.setRenderTopAndBottom(false);

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

		gui.pose().pushPose();
		gui.pose().translate(x, y, z);
		gui.pose().scale(-scale, scale, 1F);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderLights(RenderStates.PART_PREVIEW_DIFFUSE_LIGHTING_0, RenderStates.PART_PREVIEW_DIFFUSE_LIGHTING_1);

		final MultiBufferSource.BufferSource impl = Minecraft.getInstance().renderBuffers().bufferSource();

		renderer.compileTextureIfNeeded(fakeEntity, partInfo);
		final RenderType renderType = RenderStates.getPartPreview((ResourceLocation) partInfo.getTexture());
		final VertexConsumer consumer = impl.getBuffer(renderType);

		renderer.render(
				(TailsPoseStack) gui.pose(),
				fakeEntity,
				null, partInfo,
				(TailsBufferSource) impl, (TailsBuffer) consumer,
				0, 0, 0, partialTick,
				LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFF);

		impl.endBatch();

		Lighting.setupFor3DItems();

		gui.pose().popPose();
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
		public void render(GuiGraphics gui, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
			RenderSystem.setShaderColor(1, 1, 1, 1);

			if (partInfo.isEmpty()) {
				gui.drawString(parent.font(), TailsComponents.EMPTY_PART, 5, x + partList.getItemHeight() / 2 - 5, 0xFFFFFFFF);
				return;
			}

			final boolean currentPart = partList.isSelectedItem(slotIndex);
			renderPart(gui, right - 25 - 2, x - 25, currentPart ? 10 : 1, 50, partInfo, partialTick);
			gui.drawString(parent.font(), I18n.get(partInfo.getPart().getTranslationKey()), 5, x + 17, 0xFFFFFFFF);

			final ClientPartInfo editingInfo = parent.getEditingPartInfo();
			if (currentPart && editingInfo.getPartTexture() != null && editingInfo.getSubType() != null) {
				final String author;

				if (editingInfo.getPartTexture().author() != null)
					author = editingInfo.getPartTexture().author();
				else if (editingInfo.getSubType().author() != null)
					author = editingInfo.getSubType().author();
				else author = null;

				if (author != null) {
					// Yeah its not nice but eh, works.
					gui.pose().pushPose();
					gui.pose().translate(5, x + 27, 0);
					gui.pose().scale(0.6F, 0.6F, 1);
					gui.drawString(parent.font(), TailsComponents.PART_CREDIT, 0, 0, 0xFFFFFFFF);
					gui.drawString(parent.font(), Component.literal(author).withStyle(ChatFormatting.AQUA), 0, 10, 0xFFFFFFFF);
					gui.pose().popPose();
				}
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			partList.setSelected(this);
			//onEntrySelected(partList.children().indexOf(this), this);
			return true;
		}

		@Override
		public Component getNarration() {
			return Component.empty();
		}
	}
}