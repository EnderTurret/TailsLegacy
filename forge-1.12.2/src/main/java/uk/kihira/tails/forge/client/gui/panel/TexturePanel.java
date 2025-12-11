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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.fml.client.config.GuiButtonExt;

import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.Part;
import uk.kihira.tails.forge.client.RenderHelper;
import uk.kihira.tails.forge.client.gui.EditorScreen;
import uk.kihira.tails.forge.client.gui.TailsComponents;

@Internal
public final class TexturePanel extends Panel {

	public static final int VARIANT_LEFT = 700;
	public static final int VARIANT_RIGHT = 701;
	public static final int TEXTURE_LEFT = 702;
	public static final int TEXTURE_RIGHT = 703;

	private int variantSelectY;
	private int texSelectY;

	private GuiButtonExt variantLeftBtn;
	private GuiButtonExt variantRightBtn;
	private GuiButtonExt leftBtn;
	private GuiButtonExt rightBtn;

	public TexturePanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		variantSelectY = top + 17;
		texSelectY = variantSelectY + 15;

		// Texture select
		addRenderableWidget(variantLeftBtn = new GuiButtonExt(VARIANT_LEFT, 5, variantSelectY, 15, 15, "<"));
		addRenderableWidget(variantRightBtn = new GuiButtonExt(VARIANT_RIGHT, right - 20, variantSelectY, 15, 15, ">"));
		addRenderableWidget(leftBtn = new GuiButtonExt(TEXTURE_LEFT, 5, texSelectY, 15, 15, "<"));
		addRenderableWidget(rightBtn = new GuiButtonExt(TEXTURE_RIGHT, right - 20, texSelectY, 15, 15, ">"));

		updateButtons();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
			case VARIANT_LEFT:
				cycleVariantLeft(); break;
			case VARIANT_RIGHT:
				cycleVariantRight(); break;
			case TEXTURE_LEFT:
				cycleTexLeft(); break;
			case TEXTURE_RIGHT:
				cycleTexRight(); break;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);

		final ClientPartInfo partInfo = parent.getEditingPartInfo();

		final float oldZ = zLevel;
		zLevel = -5;
		drawGradientRect(20, variantSelectY, right - 20, texSelectY + 15, 0x55000000, 0x55000000);
		zLevel = oldZ;

		// Texture select
		drawCenteredString(parent.font(), TailsComponents.TEXTURE_SELECT.getFormattedText(), right / 2, variantSelectY - 12, 0xFFFFFF);

		final Part part = partInfo.getPart();

		final String texFormatted;

		if (partInfo.isEmpty() || partInfo.getPartTexture() != null)
			texFormatted = I18n.format(partInfo.getTextureTranslationKey());
		else texFormatted = partInfo.getTextureId();

		final String variantFormatted;

		if (partInfo.isEmpty() || partInfo.getSubType() != null)
			variantFormatted = I18n.format(partInfo.getSubTypeTranslationKey());
		else
			variantFormatted = partInfo.getSubTypeId();

		RenderHelper.drawScrollingString(parent.font(), variantFormatted, left + 25, right - 25, variantSelectY + 4, 0xFFFFFF);
		RenderHelper.drawScrollingString(parent.font(), texFormatted, left + 25, right - 25, texSelectY + 4, 0xFFFFFF);
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
			leftBtn.enabled = rightBtn.enabled = !partInfo.isInvalid() && partInfo.getSubType().textures().size() > 1;

		if (variantLeftBtn != null && variantRightBtn != null)
			variantLeftBtn.enabled = variantRightBtn.enabled = !partInfo.isInvalid() && partInfo.getPart().getSubTypes().size() > 1;
	}
}
