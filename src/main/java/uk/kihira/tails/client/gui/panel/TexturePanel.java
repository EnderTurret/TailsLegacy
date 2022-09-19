/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.Part;

public final class TexturePanel extends Panel<EditorScreen> {

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
		addRenderableWidget(leftBtn = new ExtendedButton(5, texSelectY, 15, 15, Component.literal("<"), b -> cycleTexLeft()));
		addRenderableWidget(rightBtn = new ExtendedButton(right - left - 20, texSelectY, 15, 15, Component.literal(">"), b -> cycleTexRight()));
		addRenderableWidget(variantLeftBtn = new ExtendedButton(5, variantSelectY, 15, 15, Component.literal("<"), b -> cycleVariantLeft()));
		addRenderableWidget(variantRightBtn = new ExtendedButton(right - left - 20, variantSelectY, 15, 15, Component.literal(">"), b -> cycleVariantRight()));

		updateButtons();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		final ClientPartInfo partInfo = parent.getEditingPartInfo();

		setBlitOffset(-10);
		fillGradient(poseStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);
		setBlitOffset(-5);
		fillGradient(poseStack, 7, variantSelectY, right - left - 15, texSelectY + 15, 0x55000000, 0x55000000); // Use fillGradient so it actually takes into account blitOffset.

		// Texture select
		drawCenteredString(poseStack, font, I18n.get("tails.gui.texture"), right / 2, variantSelectY - 12, 0xFFFFFF);

		final Part part = partInfo.getPart();

		final String texFormatted;
		boolean texTranslated = true;

		if (partInfo.isEmpty() || partInfo.getPartTexture() != null) {
			final String texLangKey = partInfo.isEmpty() ? "tails.texture.none" : part.getId().getNamespace() + ".part." + part.getId().getPath() + ".texture." + partInfo.getPartTexture().id();
			texFormatted = I18n.get(texLangKey);
			texTranslated = texLangKey.equals(texFormatted);
		} else texFormatted = partInfo.getTextureId();

		final String variantFormatted;
		boolean variantTranslated = true;

		if (partInfo.isEmpty() || partInfo.getSubType() != null) {
			final String variantLangKey = partInfo.isEmpty() ? "tails.subtype.none" : part.getTranslationKey() + ".subtype." + partInfo.getSubType().id();
			variantFormatted = I18n.get(variantLangKey);
			variantTranslated = variantLangKey.equals(variantFormatted);
		} else
			variantFormatted = partInfo.getSubTypeId();

		super.render(poseStack, mouseX, mouseY, partialTick);

		if (texTranslated) {
			fill(poseStack, 25, texSelectY + 4, 25 + font.width(texFormatted), texSelectY + 4 + font.lineHeight, 0xFFFFFFFF);
			font.draw(poseStack, texFormatted, 25, texSelectY + 4, 0xFF0000);
		} else
			font.draw(poseStack, texFormatted, 25, texSelectY + 4, 0xFFFFFF);

		if (variantTranslated) {
			fill(poseStack, 25, variantSelectY + 4, 25 + font.width(variantFormatted), variantSelectY + 4 + font.lineHeight, 0xFFFFFFFF);
			font.draw(poseStack, variantFormatted, 25, variantSelectY + 4, 0xFF0000);
		} else
			font.draw(poseStack, variantFormatted, 25, variantSelectY + 4, 0xFFFFFF);
	}

	private void cycleTexLeft() {
		ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();

		int index = partInfo.getSubType().textures().indexOf(partInfo.getPartTexture());
		if (index > 0)
			index--;
		else
			index = partInfo.getSubType().textures().size() - 1;
		final Part.PartTexture texture = partInfo.getSubType().textures().get(index);

		partInfo = new ClientPartInfo(partInfo.getTints(), part, partInfo.getSubType(), texture);
		parent.setPartsInfo(partInfo);
	}

	private void cycleTexRight() {
		ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();

		int index = partInfo.getSubType().textures().indexOf(partInfo.getPartTexture());
		if (index < partInfo.getSubType().textures().size() - 1)
			index++;
		else
			index = 0;
		final Part.PartTexture texture = partInfo.getSubType().textures().get(index);

		partInfo = new ClientPartInfo(partInfo.getTints(), part, partInfo.getSubType(), texture);
		parent.setPartsInfo(partInfo);
	}

	private void cycleVariantLeft() {
		ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();

		int index = part.getSubTypes().indexOf(partInfo.getSubType());
		final Part.SubType newType;
		if (index > 0)
			index--;
		else
			index = part.getSubTypes().size() - 1;
		newType = part.getSubTypes().get(index);

		partInfo = new ClientPartInfo(partInfo.getTints(), part, newType, partInfo.getPartTexture());
		parent.setPartsInfo(partInfo);
	}

	private void cycleVariantRight() {
		ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();

		int index = part.getSubTypes().indexOf(partInfo.getSubType());
		final Part.SubType newType;
		if (index < part.getSubTypes().size() - 1)
			index++;
		else
			index = 0;
		newType = part.getSubTypes().get(index);

		partInfo = new ClientPartInfo(partInfo.getTints(), part, newType, partInfo.getPartTexture());
		parent.setPartsInfo(partInfo);
	}

	public void updateButtons() {
		final ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();

		if (leftBtn != null && rightBtn != null)
			leftBtn.active = rightBtn.active = !partInfo.isEmpty() && partInfo.getSubType().textures().size() > 1;

		if (variantLeftBtn != null && variantRightBtn != null)
			variantLeftBtn.active = variantRightBtn.active = !partInfo.isEmpty() && part.getSubTypes().size() > 1;
	}
}
