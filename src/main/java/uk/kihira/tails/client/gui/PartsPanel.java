/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */
package uk.kihira.tails.client.gui;

import java.util.ArrayList;
import java.util.List;

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
import uk.kihira.tails.client.render.RenderPart;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;

public class PartsPanel extends Panel<GuiEditor> implements IListCallback<PartsPanel.PartEntry> {

	private GuiList<PartEntry> partList;
	private Button partTypeButton;

	private final FakeEntity fakeEntity;
	private final int listTop = 35;

	public PartsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
		alwaysReceiveMouse = true;

		fakeEntity = new FakeEntity(Minecraft.getInstance().world);
	}

	@Override
	public void init() {
		initPartList();

		addButton(partTypeButton = new ExtendedButton((right - left) / 2 - 25, 16, 50, 16, new StringTextComponent(parent.getPartType().name()), b -> {
			if (parent.getPartType().ordinal() + 1 >= PartsData.PartType.values().length)
				parent.setPartType(PartsData.PartType.values()[0]);
			else
				parent.setPartType(PartsData.PartType.values()[parent.getPartType().ordinal() + 1]);

			partTypeButton.setMessage(new StringTextComponent(parent.getPartType().name()));
			initPartList();
		}));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		setBlitOffset(-100);
		fillGradient(matrixStack, 0, 0, right - left + 12, listTop, 0xEA000000, 0xEA000000);
		// TODO: This darkens the player preview. Do we actually need this?
		//fillGradient(matrixStack, 0, listTop, right - left, bottom - top, 0xCC000000, 0xCC000000);

		setBlitOffset(0);
		RenderSystem.color4f(1, 1, 1, 1);
		drawCenteredString(matrixStack, font, I18n.format("gui.partselect"), (right - left) / 2, 5, 0xFFFFFF);
		//Tails list
		partList.render(matrixStack, mouseX, mouseY, partialTicks);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		//Delete textures on close
		for (PartEntry entry : partList.getEventListeners())
			entry.partInfo.setTexture(null);
	}

	@Override
	public boolean onEntrySelected(GuiList guiList, int index, PartEntry entry) {
		//Reset texture ID
		parent.textureID = 0;
		//Need to keep tints from original part
		final PartInfo partInfo = new PartInfo(entry.partInfo.hasPart, entry.partInfo.typeid, entry.partInfo.subid, entry.partInfo.textureID,
				parent.getEditingPartInfo().tints.clone(), entry.partInfo.partType, entry.partInfo.scale, null);
		parent.setPartsInfo(partInfo);
		return true;
	}

	void initPartList() {
		//Part List
		final List<PartEntry> partList = new ArrayList<>();
		final PartsData.PartType partType = parent.getPartType();
		partList.add(new PartEntry(PartInfo.none(partType))); //No tail
		//Generate tail preview textures and add to list
		final List<RenderPart> parts = PartRegistry.getParts(partType);
		for (int type = 0; type < parts.size(); type++)
			for (int subType = 0; subType <= parts.get(type).getAvailableSubTypes(); subType++) {
				final PartInfo partInfo = new PartInfo(true, type, subType, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 1, null, partType);
				partList.add(new PartEntry(partInfo));
			}

		children.remove(this.partList);
		this.partList = new GuiList<>(this, 120, height - listTop, listTop, height, 55, partList);
		addListener(this.partList);
		selectDefaultListEntry();
	}

	void selectDefaultListEntry() {
		//Default selection
		final PartInfo partInfo = parent.getEditingPartInfo();
		for (PartEntry entry : partList.getEventListeners())
			if (!entry.partInfo.hasPart && !partInfo.hasPart || partInfo.hasPart && entry.partInfo.hasPart
					&& entry.partInfo.typeid == partInfo.typeid && entry.partInfo.subid == partInfo.subid) {
				partList.setSelected(entry);
				onEntrySelected(partList, partList.getEventListeners().indexOf(entry), entry);
				break;
			}
	}

	private void renderPart(MatrixStack matrixStack, int x, int y, int z, int scale, PartInfo partInfo, float partialTicks) {
		matrixStack.push();
		matrixStack.translate(x, y, z);
		matrixStack.scale(-scale, scale, 1F);

		final IRenderTypeBuffer.Impl impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid)
		.render(matrixStack, fakeEntity, partInfo, impl, 0, 0, 0, partialTicks, 15728880, OverlayTexture.NO_OVERLAY);
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

			if (partInfo.hasPart) {
				final boolean currentPart = partList.isSelectedItem(slotIndex);
				renderPart(matrixStack, right - 25, x - 25, currentPart ? 10 : 1, 50, partInfo, partialTicks);
				ClientUtils.drawStringMultiLine(matrixStack, font, I18n.format(PartRegistry.getRenderPart(partInfo.partType, partInfo.typeid)
						.getUnlocalisedName(partInfo.subid)), 5, x + 17, 0xFFFFFF);

				if (currentPart) {
					final RenderPart renderPart = PartRegistry.getRenderPart(parent.getPartType(), partInfo.typeid);
					if (renderPart.getModelAuthor() != null) {
						//Yeah its not nice but eh, works
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
