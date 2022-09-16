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

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.PartRenderRegistry;
import uk.kihira.tails.client.RenderHelper;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.ListWidget;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.client.render.RenderStates;
import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartRegistry;
import uk.kihira.tails.common.part.PartType;

public class PartsPanel extends Panel<EditorScreen> {

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

		addRenderableWidget(partTypeButton = new ExtendedButton((right - left) / 2 - 25, 16, 50, 16, Component.translatable("tails.part." + parent.getPartType().getId()), b -> {
			if (parent.getPartType().ordinal() + 1 >= PartType.values().length)
				parent.setPartType(PartType.values()[0]);
			else
				parent.setPartType(PartType.values()[parent.getPartType().ordinal() + 1]);

			partTypeButton.setMessage(Component.translatable("tails.part." + parent.getPartType().getId()));
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
		// Tails list
		partList.render(poseStack, mouseX, mouseY, partialTick);

		super.render(poseStack, mouseX, mouseY, partialTick);
	}

	@Override
	public void removed() {
		// Delete textures on close.
		for (PartEntry entry : partList.children())
			entry.partInfo.setTexture(null);
	}

	public boolean onEntrySelected(int index, PartEntry entry) {
		final PartInfo oldInfo = parent.getEditingPartInfo();
		final int subType = oldInfo.getPart() == entry.partInfo.getPart() ? oldInfo.getSubType() : 0;
		// Reset texture ID.
		parent.setTextureId(0);
		// Need to keep tints from original part.
		final PartInfo partInfo = entry.partInfo.isEmpty() ? entry.partInfo.deepCopy() : new PartInfo(entry.partInfo.getPartId(), subType, entry.partInfo.getTextureId(),
				oldInfo.getTints().clone(), null);

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
		final PartType partType = parent.getPartType();
		partList.add(new PartEntry(PartInfo.none())); // No tail
		// Generate tail preview textures and add to list.
		final List<Part> parts = PartRegistry.getParts(partType);
		for (int type = 0; type < parts.size(); type++) {
			final PartInfo partInfo = parts.get(type).makeDefaultPartInfo(0);
			partList.add(new PartEntry(partInfo));
		}

		this.removeWidget(this.partList);
		this.partList = new ListWidget<>(108, bottom - top - listTop, listTop, bottom - top, 55, partList);
		addWidget(this.partList);
		selectDefaultListEntry();
	}

	void selectDefaultListEntry() {
		// Default selection.
		final PartInfo partInfo = parent.getEditingPartInfo();
		for (PartEntry entry : partList.children())
			if (entry.partInfo.isEmpty() && partInfo.isEmpty() || !partInfo.isEmpty() && !entry.partInfo.isEmpty()
					&& entry.partInfo.getPart() == partInfo.getPart()) {
				partList.setSelected(entry);
				onEntrySelected(partList.children().indexOf(entry), entry);
				break;
			}
	}

	private void renderPart(PoseStack poseStack, int x, int y, int z, int scale, PartInfo partInfo, float partialTick) {
		final PartRenderer renderer = PartRenderRegistry.getRenderer(partInfo.getPart());
		renderer.compileTextureIfNeeded(fakeEntity, partInfo);

		if (partInfo.getTexture() == null) return;

		poseStack.pushPose();
		poseStack.translate(x, y, z);
		poseStack.scale(-scale, scale, 1F);

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderLights(RenderStates.PART_PREVIEW_DIFFUSE_LIGHTING_0, RenderStates.PART_PREVIEW_DIFFUSE_LIGHTING_1);

		final MultiBufferSource.BufferSource impl = Minecraft.getInstance().renderBuffers().bufferSource();
		final VertexConsumer consumer = impl.getBuffer(RenderStates.getPartPreview(partInfo.getTexture()));

		renderer.render(poseStack, fakeEntity, partInfo, impl, consumer, 0, 0, 0, partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
		impl.endBatch();

		Lighting.setupFor3DItems();

		poseStack.popPose();
	}

	class PartEntry extends ObjectSelectionList.Entry<PartEntry> {

		final PartInfo partInfo;

		PartEntry(PartInfo partInfo) {
			this.partInfo = partInfo;
		}

		@Override
		public void render(PoseStack poseStack, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTick) {
			RenderSystem.setShaderColor(1, 1, 1, 1);
			setBlitOffset(0);

			if (!partInfo.isEmpty()) {
				final boolean currentPart = partList.isSelectedItem(slotIndex);
				renderPart(poseStack, right - 25, x - 25, currentPart ? 10 : 1, 50, partInfo, partialTick);
				RenderHelper.drawStringMultiLine(poseStack, font, I18n.get(partInfo.getPart().getTranslationKey()), 5, x + 17, 0xFFFFFF);

				if (currentPart) {
					final Part renderPart = partInfo.getPart();
					final String author = renderPart.getAuthor(parent.getEditingPartInfo().getSubType(), parent.getTextureId());
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
			onEntrySelected(partList.children().indexOf(this), this);
			return true;
		}

		@Override
		public Component getNarration() {
			return Component.empty();
		}
	}
}
