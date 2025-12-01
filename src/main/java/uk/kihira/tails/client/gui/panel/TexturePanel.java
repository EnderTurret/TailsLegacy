/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.common2.client.part.Part;

@Internal
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
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		final ClientPartInfo partInfo = parent.getEditingPartInfo();

		gui.fill(0, 0, right - left, bottom - top, -10, 0xCC000000);
		gui.fill(7, variantSelectY, right - left - 15, texSelectY + 15, -5, 0x55000000);

		// Texture select
		gui.drawCenteredString(font, I18n.get("tails.gui.texture"), right / 2, variantSelectY - 12, 0xFFFFFF);

		final Part part = partInfo.getPart();

		final String texFormatted;
		boolean texTranslated = true;

		if (partInfo.isEmpty() || partInfo.getPartTexture() != null) {
			final String texLangKey = partInfo.getTextureTranslationKey();
			texFormatted = I18n.get(texLangKey);
			texTranslated = !texLangKey.equals(texFormatted);
		} else texFormatted = partInfo.getTextureId();

		final String variantFormatted;
		boolean variantTranslated = true;

		if (partInfo.isEmpty() || partInfo.getSubType() != null) {
			final String variantLangKey = partInfo.getSubTypeTranslationKey();
			variantFormatted = I18n.get(variantLangKey);
			variantTranslated = !variantLangKey.equals(variantFormatted);
		} else
			variantFormatted = partInfo.getSubTypeId();

		super.render(gui, mouseX, mouseY, partialTick);

		if (!texTranslated) {
			gui.fill(25, texSelectY + 4, 25 + font.width(texFormatted), texSelectY + 4 + font.lineHeight, 0xFFFFFFFF);
			gui.drawString(font, texFormatted, 25, texSelectY + 4, 0xFF0000);
		} else
			gui.drawString(font, texFormatted, 25, texSelectY + 4, 0xFFFFFF);

		if (!variantTranslated) {
			gui.fill(25, variantSelectY + 4, 25 + font.width(variantFormatted), variantSelectY + 4 + font.lineHeight, 0xFFFFFFFF);
			gui.drawString(font, variantFormatted, 25, variantSelectY + 4, 0xFF0000);
		} else
			gui.drawString(font, variantFormatted, 25, variantSelectY + 4, 0xFFFFFF);
	}

	private void cycleTexLeft() {
		parent.setPartsInfo(parent.getEditingPartInfo().nextTexture(-1));
	}

	private void cycleTexRight() {
		parent.setPartsInfo(parent.getEditingPartInfo().nextTexture(1));
	}

	private void cycleVariantLeft() {
		parent.setPartsInfo(parent.getEditingPartInfo().nextSubType(-1));
	}

	private void cycleVariantRight() {
		parent.setPartsInfo(parent.getEditingPartInfo().nextSubType(1));
	}

	public void updateButtons() {
		final ClientPartInfo partInfo = parent.getEditingPartInfo();

		if (leftBtn != null && rightBtn != null)
			leftBtn.active = rightBtn.active = !partInfo.isInvalid() && partInfo.getSubType().textures().size() > 1;

		if (variantLeftBtn != null && variantRightBtn != null)
			variantLeftBtn.active = variantRightBtn.active = !partInfo.isInvalid() && partInfo.getPart().getSubTypes().size() > 1;
	}
}
