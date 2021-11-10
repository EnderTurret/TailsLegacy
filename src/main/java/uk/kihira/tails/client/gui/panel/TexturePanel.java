/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.render.PartRenderer;
import uk.kihira.tails.common.part.PartInfo;

public class TexturePanel extends Panel<EditorScreen> {

	private final int variantSelectY = 17;
	private final int texSelectY = variantSelectY + 15;

	private ExtendedButton variantLeftBtn;
	private ExtendedButton variantRightBtn;
	private ExtendedButton leftBtn;
	private ExtendedButton rightBtn;

	public TexturePanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		// Texture select
		addButton(leftBtn = new ExtendedButton(5, texSelectY, 15, 15, new StringTextComponent("<"), b -> cycleTexLeft()));
		addButton(rightBtn = new ExtendedButton(right - left - 20, texSelectY, 15, 15, new StringTextComponent(">"), b -> cycleTexRight()));
		addButton(variantLeftBtn = new ExtendedButton(5, variantSelectY, 15, 15, new StringTextComponent("<"), b -> cycleVariantLeft()));
		addButton(variantRightBtn = new ExtendedButton(right - left - 20, variantSelectY, 15, 15, new StringTextComponent(">"), b -> cycleVariantRight()));
		parent.setTextureId(parent.getEditingPartInfo().getTextureId());

		updateButtons();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		final PartInfo partInfo = parent.getEditingPartInfo();

		setBlitOffset(-10);
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);
		setBlitOffset(-5);
		fillGradient(matrixStack, 7, variantSelectY, right - left - 15, texSelectY + 15, 0x55000000, 0x55000000); // Use fillGradient so it actually takes into account blitOffset.

		// Texture select
		drawCenteredString(matrixStack, font, I18n.format("gui.texture"), right / 2, variantSelectY - 12, 0xFFFFFF);

		final PartRenderer renderer = PartRegistry.getPartRenderer(parent.getPartType(), partInfo.getTypeId());

		final String texLangKey = (partInfo.isEmpty() ? "texture.none" : parent.getPartType().getId() + ".texture." + renderer.getTextureNames(partInfo.getSubType())[parent.getTextureId()] + ".name");
		final String texFormatted = I18n.format(texLangKey);
		final String variantLangKey = (partInfo.isEmpty() ? "variant.none" : renderer.getName() + ".variant." + partInfo.getSubType());
		final String variantFormatted = I18n.format(variantLangKey);

		super.render(matrixStack, mouseX, mouseY, partialTicks);

		if (texFormatted.equals(texLangKey)) {
			fill(matrixStack, 25, texSelectY + 4, 25 + font.getStringWidth(texFormatted), texSelectY + 4 + font.FONT_HEIGHT, 0xFFFFFFFF);
			font.drawString(matrixStack, texFormatted, 25, texSelectY + 4, 0xFF0000);
		} else
			font.drawString(matrixStack, texFormatted, 25, texSelectY + 4, 0xFFFFFF);

		if (variantFormatted.equals(variantLangKey)) {
			fill(matrixStack, 25, variantSelectY + 4, 25 + font.getStringWidth(variantFormatted), variantSelectY + 4 + font.FONT_HEIGHT, 0xFFFFFFFF);
			font.drawString(matrixStack, variantFormatted, 25, variantSelectY + 4, 0xFF0000);
		} else
			font.drawString(matrixStack, variantFormatted, 25, variantSelectY + 4, 0xFFFFFF);
	}

	private void cycleTexLeft() {
		final PartInfo originalPartInfo = parent.getEditingPartInfo();
		final PartRenderer part = PartRegistry.getPartRenderer(parent.getPartType(), originalPartInfo.getTypeId());
		if (parent.getTextureId() > 0)
			parent.setTextureId(parent.getTextureId() - 1);
		else
			parent.setTextureId(part.getTextureNames(originalPartInfo.getSubType()).length - 1);
		final PartInfo partInfo = new PartInfo(originalPartInfo.getTypeId(), originalPartInfo.getSubType(), parent.getTextureId(),
				originalPartInfo.getTints(), originalPartInfo.getPartType(), null);
		parent.setPartsInfo(partInfo);
	}

	private void cycleTexRight() {
		final PartInfo originalPartInfo = parent.getEditingPartInfo();
		final PartRenderer part = PartRegistry.getPartRenderer(parent.getPartType(), originalPartInfo.getTypeId());
		if (part.getTextureNames(originalPartInfo.getSubType()).length > parent.getTextureId() + 1)
			parent.setTextureId(parent.getTextureId() + 1);
		else
			parent.setTextureId(0);
		final PartInfo partInfo = new PartInfo(originalPartInfo.getTypeId(), originalPartInfo.getSubType(), parent.getTextureId(),
				originalPartInfo.getTints(), originalPartInfo.getPartType(), null);
		parent.setPartsInfo(partInfo);
	}

	private void cycleVariantLeft() {
		PartInfo partInfo = parent.getEditingPartInfo();
		final PartRenderer part = PartRegistry.getPartRenderer(parent.getPartType(), partInfo.getTypeId());
		final int newType;
		if (partInfo.getSubType() > 0)
			newType = partInfo.getSubType() - 1;
		else
			newType = part.getAvailableSubTypes();
		partInfo = new PartInfo(partInfo.getTypeId(), newType, partInfo.getTextureId(), partInfo.getTints(), partInfo.getPartType(), null);
		parent.setPartsInfo(partInfo);
	}

	private void cycleVariantRight() {
		PartInfo partInfo = parent.getEditingPartInfo();
		final PartRenderer part = PartRegistry.getPartRenderer(parent.getPartType(), partInfo.getTypeId());
		final int newType;
		if (partInfo.getSubType() < part.getAvailableSubTypes())
			newType = partInfo.getSubType() + 1;
		else
			newType = 0;
		partInfo = new PartInfo(partInfo.getTypeId(), newType, partInfo.getTextureId(), partInfo.getTints(), partInfo.getPartType(), null);
		parent.setPartsInfo(partInfo);
	}

	public void updateButtons() {
		final PartInfo partInfo = parent.getEditingPartInfo();
		final PartRenderer part = PartRegistry.getPartRenderer(parent.getPartType(), partInfo.getTypeId());

		if (leftBtn != null && rightBtn != null)
			leftBtn.active = rightBtn.active = !partInfo.isEmpty() && part.getTextureNames(partInfo.getSubType()).length > 1;

		if (variantLeftBtn != null && variantRightBtn != null)
			variantLeftBtn.active = variantRightBtn.active = !partInfo.isEmpty() && part.getAvailableSubTypes() != 0;
	}
}
