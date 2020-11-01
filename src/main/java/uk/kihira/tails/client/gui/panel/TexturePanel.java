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
import uk.kihira.tails.common.PartInfo;

public class TexturePanel extends Panel<EditorScreen> {
	private final int texSelectX = 17;

	private ExtendedButton leftBtn;
	private ExtendedButton rightBtn;

	public TexturePanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		// Texture select
		addButton(leftBtn = new ExtendedButton(5, texSelectX, 15, 15, new StringTextComponent("<"), b -> {
			final PartInfo originalPartInfo = parent.getEditingPartInfo();
			final PartRenderer part = PartRegistry.getPartRenderer(parent.getPartType(), originalPartInfo.typeid);
			if (parent.getTextureId() - 1 >= 0)
				parent.setTextureId(parent.getTextureId() - 1);
			else
				parent.setTextureId(part.getTextureNames(originalPartInfo.subid).length - 1);
			final PartInfo partInfo = new PartInfo(true, originalPartInfo.typeid, originalPartInfo.subid, parent.getTextureId(),
					originalPartInfo.tints, originalPartInfo.partType, originalPartInfo.scale, null);
			parent.setPartsInfo(partInfo);
		}));
		addButton(rightBtn = new ExtendedButton(right - left - 20, texSelectX, 15, 15, new StringTextComponent(">"), b -> {
			final PartInfo originalPartInfo = parent.getEditingPartInfo();
			final PartRenderer part = PartRegistry.getPartRenderer(parent.getPartType(), originalPartInfo.typeid);
			if (part.getTextureNames(originalPartInfo.subid).length > parent.getTextureId() + 1)
				parent.setTextureId(parent.getTextureId() + 1);
			else
				parent.setTextureId(0);
			final PartInfo partInfo = new PartInfo(true, originalPartInfo.typeid, originalPartInfo.subid, parent.getTextureId(),
					originalPartInfo.tints, originalPartInfo.partType, originalPartInfo.scale, null);
			parent.setPartsInfo(partInfo);
		}));
		parent.setTextureId(parent.getEditingPartInfo().textureID);

		updateButtons();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		final PartInfo partInfo = parent.getEditingPartInfo();

		setBlitOffset(-10);
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);
		setBlitOffset(-5);
		fillGradient(matrixStack, 7, texSelectX, right - left - 15, texSelectX + 15, 0x55000000, 0x55000000); // Use fillGradient so it actually takes into account blitOffset.

		// Texture select
		drawCenteredString(matrixStack, font, I18n.format("gui.texture"), right / 2, texSelectX - 12, 0xFFFFFF);

		final PartRenderer renderer = PartRegistry.getPartRenderer(parent.getPartType(), partInfo.typeid);

		final String langKey = parent.getPartType().name().toLowerCase() + ".texture." + renderer.getTextureNames(partInfo.subid)[parent.getTextureId()] + ".name";
		final String formatted = I18n.format(langKey);

		if (formatted.equals(langKey)) {
			super.render(matrixStack, mouseX, mouseY, partialTicks);

			matrixStack.push();

			matrixStack.translate(0, 0, 1000);

			fill(matrixStack, 25, texSelectX + 4, 25 + font.getStringWidth(formatted), texSelectX + 4 + font.FONT_HEIGHT, 0xFFFFFFFF);
			font.drawString(matrixStack, formatted, 25, texSelectX + 4, 0xFF0000);

			matrixStack.pop();
		} else {
			font.drawString(matrixStack, formatted, 25, texSelectX + 4, formatted.equals(langKey) ? 0xFF0000 : 0xFFFFFF);
			super.render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}

	public void updateButtons() {
		final PartInfo originalPartInfo = parent.getEditingPartInfo();
		final PartRenderer part = PartRegistry.getPartRenderer(parent.getPartType(), originalPartInfo.typeid);

		final int texCount = part.getTextureNames(originalPartInfo.subid).length;
		if (leftBtn != null && rightBtn != null)
			leftBtn.active = rightBtn.active = texCount > 1;
	}
}
