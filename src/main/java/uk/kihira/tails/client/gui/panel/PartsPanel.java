/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.RenderHelper;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.FocusableExtendedButton;
import uk.kihira.tails.client.gui.widget.ListWidget;
import uk.kihira.tails.client.part.AttachmentPoint;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.part.PartRegistry;
import uk.kihira.tails.client.part.PartType;
import uk.kihira.tails.client.render.RenderStates;
import uk.kihira.tails.client.render.part.PartRenderer;
import uk.kihira.tails.common.part.ServerPartInfo;

@Internal
public final class PartsPanel extends Panel<EditorScreen> {

	private ListWidget<PartEntry> partList;
	private Button partTypeButton;

	private final FakeEntity fakeEntity;
	private final int listTop = 35;

	public PartsPanel(EditorScreen parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
		alwaysReceiveMouse = true;

		fakeEntity = new FakeEntity(Minecraft.getInstance().level);
	}

	@Override
	public void init() {
		initPartList();

		addRenderableWidget(partTypeButton = new FocusableExtendedButton((right - left) / 2 - 25, 16, 50, 16, Component.translatable("tails.part." + parent.getAttachmentPoint().id()), b -> {
			if (parent.getAttachmentPoint().ordinal() + 1 >= PartType.values().length)
				parent.setAttachmentPoint(PartType.values()[0]);
			else
				parent.setAttachmentPoint(PartType.values()[parent.getAttachmentPoint().ordinal() + 1]);

			partTypeButton.setMessage(Component.translatable("tails.part." + parent.getAttachmentPoint().getId()));
			initPartList();
		}));
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		setBlitOffset(-100);
		fillGradient(poseStack, 0, 0, right - left, listTop, 0xEA000000, 0xEA000000);

		fillGradient(poseStack, 0, listTop, right - left, bottom - top, 0xFF000000, 0xFF000000);

		setBlitOffset(0);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		drawCenteredString(poseStack, font, I18n.get("tails.gui.partselect"), (right - left) / 2, 5, 0xFFFFFF);

		super.render(poseStack, mouseX, mouseY, partialTick);
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
			partInfo = new ClientPartInfo(spi, entry.partInfo.getPart(), subType, texture);
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

			removeWidget(this.partList);
		}

		this.partList = new ListWidget<>(108 + 6, bottom - top - listTop, listTop, bottom - top, 55, partList) {
			@Override
			public void onItemSelected(PartEntry item) {
				onEntrySelected(item);
			}
		};

		addRenderableWidget(this.partList);
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

	private void renderPart(PoseStack poseStack, int x, int y, int z, int scale, ClientPartInfo partInfo, float partialTick) {
		final PartRenderer renderer = PartRenderRegistry.getRenderer(partInfo.getPart());

		if (partInfo.isEmpty() || partInfo.isInvalid()) return;

		poseStack.pushPose();
		poseStack.translate(x, y, z);
		poseStack.scale(-scale, scale, 1F);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderLights(RenderStates.PART_PREVIEW_DIFFUSE_LIGHTING_0, RenderStates.PART_PREVIEW_DIFFUSE_LIGHTING_1);

		final MultiBufferSource.BufferSource impl = Minecraft.getInstance().renderBuffers().bufferSource();

		renderer.compileTextureIfNeeded(fakeEntity, partInfo);
		final VertexConsumer consumer = impl.getBuffer(RenderStates.getPartPreview(partInfo.getTexture()));

		renderer.render(poseStack, fakeEntity, partInfo, impl, consumer, 0, 0, 0, partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1F);
		impl.endBatch();

		Lighting.setupFor3DItems();

		poseStack.popPose();
	}

	class PartEntry extends ObjectSelectionList.Entry<PartEntry> {

		private final ClientPartInfo partInfo;

		PartEntry(ClientPartInfo partInfo) {
			this.partInfo = partInfo;
		}

		@Override
		public void render(PoseStack poseStack, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
			RenderSystem.setShaderColor(1, 1, 1, 1);
			setBlitOffset(0);

			if (!partInfo.isEmpty()) {
				final boolean currentPart = partList.isSelectedItem(slotIndex);
				renderPart(poseStack, right - 25 - 2, x - 25, currentPart ? 10 : 1, 50, partInfo, partialTick);
				RenderHelper.drawStringMultiLine(poseStack, font, I18n.get(partInfo.getPart().getTranslationKey()), 5, x + 17, 0xFFFFFF);

				if (currentPart && parent.getEditingPartInfo().getPartTexture() != null && parent.getEditingPartInfo().getSubType() != null) {
					final String author;

					if (parent.getEditingPartInfo().getPartTexture().author() != null)
						author = parent.getEditingPartInfo().getPartTexture().author();
					else if (parent.getEditingPartInfo().getSubType().author() != null)
						author = parent.getEditingPartInfo().getSubType().author();
					else author = null;

					if (author != null) {
						// Yeah its not nice but eh, works.
						poseStack.pushPose();
						poseStack.translate(5, x + 27, 0);
						poseStack.scale(0.6F, 0.6F, 1F);
						setBlitOffset(100);
						font.draw(poseStack, I18n.get("tails.gui.createdby") + ":", 0, 0, 0xFFFFFF);
						poseStack.translate(0, 10, 0);
						font.draw(poseStack, ChatFormatting.AQUA + author, 0, 0, 0xFFFFFF);
						poseStack.popPose();
						setBlitOffset(0);
					}
				}
			} else
				font.draw(poseStack, I18n.get("tails.gui.part.none"), 5, x + partList.getItemHeight() / 2 - 5, 0xFFFFFF);
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