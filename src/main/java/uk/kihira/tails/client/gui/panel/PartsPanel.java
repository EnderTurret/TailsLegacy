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
import java.util.Locale;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.client.FakeEntity;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.IListCallback;
import uk.kihira.tails.client.gui.widget.ListWidget;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.client.render.RenderStates;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;

public class PartsPanel extends Panel<EditorScreen> implements IListCallback<PartsPanel.PartEntry> {

	private ListWidget<PartEntry> partList;
	private Button partTypeButton;

	private final FakeEntity fakeEntity;
	private final int listTop = 35;

	public PartsPanel(EditorScreen parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
		alwaysReceiveMouse = true;

		fakeEntity = new FakeEntity(Minecraft.getInstance().world);
	}

	@Override
	public void init() {
		initPartList();

		addButton(partTypeButton = new ExtendedButton((right - left) / 2 - 25, 16, 50, 16, new StringTextComponent(parent.getPartType().getId().toUpperCase(Locale.ROOT)), b -> {
			if (parent.getPartType().ordinal() + 1 >= PartType.values().length)
				parent.setPartType(PartType.values()[0]);
			else
				parent.setPartType(PartType.values()[parent.getPartType().ordinal() + 1]);

			partTypeButton.setMessage(new StringTextComponent(parent.getPartType().getId().toUpperCase(Locale.ROOT)));
			initPartList();
		}));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		setBlitOffset(-100);
		fillGradient(matrixStack, 0, 0, right - left, listTop, 0xEA000000, 0xEA000000);

		fillGradient(matrixStack, 0, listTop, right - left, bottom - top, 0xFF000000, 0xFF000000);

		setBlitOffset(0);
		RenderSystem.color4f(1, 1, 1, 1);
		drawCenteredString(matrixStack, font, I18n.format("gui.partselect"), (right - left) / 2, 5, 0xFFFFFF);
		// Tails list
		partList.render(matrixStack, mouseX, mouseY, partialTicks);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		// Delete textures on close.
		for (PartEntry entry : partList.getEventListeners())
			entry.partInfo.setTexture(null);
	}

	@Override
	public boolean onEntrySelected(ListWidget guiList, int index, PartEntry entry) {
		// Reset texture ID.
		parent.setTextureId(0);
		// Need to keep tints from original part.
		final PartInfo partInfo = entry.partInfo.isEmpty() ? entry.partInfo.deepCopy() : new PartInfo(entry.partInfo.getTypeId(), entry.partInfo.getSubType(), entry.partInfo.getTextureId(),
				parent.getEditingPartInfo().getTints().clone(), entry.partInfo.getPartType(), null);

		// Breaks immutability, but it's probably fine, right?
		if (entry.partInfo.isEmpty())
			for (int i = 0; i < partInfo.getTints().length; i++)
				partInfo.getTints()[i] = parent.getEditingPartInfo().getTints()[i];

		parent.setPartsInfo(partInfo);
		return true;
	}

	public void initPartList() {
		// Part List
		final List<PartEntry> partList = new ArrayList<>();
		final PartType partType = parent.getPartType();
		partList.add(new PartEntry(PartInfo.none(partType))); // No tail
		// Generate tail preview textures and add to list.
		final List<PartRenderer> parts = PartRegistry.getParts(partType);
		for (int type = 0; type < parts.size(); type++)
			for (int subType = 0; subType <= parts.get(type).getAvailableSubTypes(); subType++) {
				final PartInfo partInfo = parts.get(type).makeDefaultPartInfo(type, subType, partType);
				partList.add(new PartEntry(partInfo));
			}

		children.remove(this.partList);
		this.partList = new ListWidget<>(this, 108, bottom - top - listTop, listTop, bottom - top, 55, partList);
		addListener(this.partList);
		selectDefaultListEntry();
	}

	void selectDefaultListEntry() {
		// Default selection.
		final PartInfo partInfo = parent.getEditingPartInfo();
		for (PartEntry entry : partList.getEventListeners())
			if (entry.partInfo.isEmpty() && partInfo.isEmpty() || !partInfo.isEmpty() && !entry.partInfo.isEmpty()
					&& entry.partInfo.getTypeId() == partInfo.getTypeId() && entry.partInfo.getSubType() == partInfo.getSubType()) {
				partList.setSelected(entry);
				onEntrySelected(partList, partList.getEventListeners().indexOf(entry), entry);
				break;
			}
	}

	private void renderPart(MatrixStack matrixStack, int x, int y, int z, int scale, PartInfo partInfo, float partialTicks) {
		if (partInfo.needsTextureCompile || partInfo.getTexture() == null) {
			partInfo.setTexture(TextureHelper.generateTexture(fakeEntity.getUniqueID(), partInfo));
			partInfo.needsTextureCompile = false;
		}

		if (partInfo.getTexture() == null) return;

		matrixStack.push();
		matrixStack.translate(x, y, z);
		matrixStack.scale(-scale, scale, 1F);

		final IRenderTypeBuffer.Impl impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		PartRegistry.getPartRenderer(partInfo.getPartType(), partInfo.getTypeId())
		.render(matrixStack, fakeEntity, partInfo, impl, impl.getBuffer(RenderStates.getPartPreview(partInfo.getTexture())), 0, 0, 0, partialTicks, 15728880, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
		impl.finish();

		matrixStack.pop();
	}

	class PartEntry extends ExtendedList.AbstractListEntry<PartEntry> {

		final PartInfo partInfo;
		private final int clickTime = 0;

		PartEntry(PartInfo partInfo) {
			this.partInfo = partInfo;
		}

		@Override
		public void render(MatrixStack matrixStack, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
			RenderSystem.color4f(1, 1, 1, 1);
			setBlitOffset(0);

			if (!partInfo.isEmpty()) {
				final boolean currentPart = partList.isSelectedItem(slotIndex);
				renderPart(matrixStack, right - 25, x - 25, currentPart ? 10 : 1, 50, partInfo, partialTicks);
				ClientUtils.drawStringMultiLine(matrixStack, font, I18n.format(PartRegistry.getPartRenderer(partInfo.getPartType(), partInfo.getTypeId())
						.getUnlocalisedName(partInfo.getSubType())), 5, x + 17, 0xFFFFFF);

				if (currentPart) {
					final PartRenderer renderPart = PartRegistry.getPartRenderer(parent.getPartType(), partInfo.getTypeId());
					if (renderPart.getModelAuthor() != null) {
						// Yeah its not nice but eh, works.
						matrixStack.push();
						matrixStack.translate(5, x + 27, 0);
						matrixStack.scale(0.6F, 0.6F, 1F);
						setBlitOffset(100);
						font.drawString(matrixStack, I18n.format("gui.createdby") + ":", 0, 0, 0xFFFFFF);
						matrixStack.translate(0, 10, 0);
						font.drawString(matrixStack, TextFormatting.AQUA + renderPart.getModelAuthor(), 0, 0, 0xFFFFFF);
						matrixStack.pop();
						setBlitOffset(0);
					}
				}
			} else
				font.drawString(matrixStack, I18n.format("tail.none.name"), 5, x + partList.getItemHeight() / 2 - 5, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			partList.setSelected(this);
			onEntrySelected(partList, partList.getEventListeners().indexOf(this), this);
			return true;
		}
	}
}
