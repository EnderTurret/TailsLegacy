/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.forge.client.gui.EditorScreen;
import uk.kihira.tails.forge.client.gui.TailsComponents;

@Internal
public final class TexturePanel extends Panel {

	private final int variantSelectY;
	private final int texSelectY;

	private ExtendedButton variantLeftBtn;
	private ExtendedButton variantRightBtn;
	private ExtendedButton leftBtn;
	private ExtendedButton rightBtn;

	public TexturePanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		variantSelectY = top + 17;
		texSelectY = variantSelectY + 15;
	}

	@Override
	public void init() {
		// Texture select
		addRenderableWidget(variantLeftBtn = new ExtendedButton(5, variantSelectY, 15, 15, Component.literal("<"), b -> cycleVariantLeft()));
		addRenderableWidget(variantRightBtn = new ExtendedButton(right - 20, variantSelectY, 15, 15, Component.literal(">"), b -> cycleVariantRight()));
		addRenderableWidget(leftBtn = new ExtendedButton(5, texSelectY, 15, 15, Component.literal("<"), b -> cycleTexLeft()));
		addRenderableWidget(rightBtn = new ExtendedButton(right - 20, texSelectY, 15, 15, Component.literal(">"), b -> cycleTexRight()));

		updateButtons();
	}

	@Override
	public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		super.renderWidget(gui, mouseX, mouseY, partialTick);

		final ClientPartInfo partInfo = parent.getEditingPartInfo();

		gui.fill(7, variantSelectY, right - 15, texSelectY + 15, -5, 0x55000000);

		// Texture select
		gui.drawCenteredString(parent.font(), TailsComponents.TEXTURE_SELECT, right / 2, variantSelectY - 12, 0xFFFFFF);

		final Part part = partInfo.getPart();

		final Component texFormatted;

		if (partInfo.isEmpty() || partInfo.getPartTexture() != null)
			texFormatted = Component.translatable(partInfo.getTextureTranslationKey());
		else texFormatted = Component.literal(partInfo.getTextureId());

		final Component variantFormatted;

		if (partInfo.isEmpty() || partInfo.getSubType() != null)
			variantFormatted = Component.translatable(partInfo.getSubTypeTranslationKey());
		else
			variantFormatted = Component.literal(partInfo.getSubTypeId());

		gui.drawScrollingString(parent.font(), variantFormatted, left + 25, right - 25, variantSelectY + 4, 0xFFFFFF);
		gui.drawScrollingString(parent.font(), texFormatted, left + 25, right - 25, texSelectY + 4, 0xFFFFFF);
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
