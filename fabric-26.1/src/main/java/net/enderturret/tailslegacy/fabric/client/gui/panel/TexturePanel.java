/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.gui.panel;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.fabric.client.gui.EditorScreen;
import net.enderturret.tailslegacy.fabric.client.gui.TailsComponents;

@Internal
public final class TexturePanel extends Panel {

	private int variantSelectY;
	private int texSelectY;

	private ExtendedButton variantLeftBtn;
	private ExtendedButton variantRightBtn;
	private ExtendedButton leftBtn;
	private ExtendedButton rightBtn;

	public TexturePanel(EditorScreen parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	@Override
	public void init() {
		variantSelectY = top + 17;
		texSelectY = variantSelectY + 15;

		// Texture select
		addRenderableWidget(variantLeftBtn = new ExtendedButton(5, variantSelectY, 15, 15, Component.literal("<"), _ -> cycleVariantLeft()));
		addRenderableWidget(variantRightBtn = new ExtendedButton(right - 20, variantSelectY, 15, 15, Component.literal(">"), _ -> cycleVariantRight()));
		addRenderableWidget(leftBtn = new ExtendedButton(5, texSelectY, 15, 15, Component.literal("<"), _ -> cycleTexLeft()));
		addRenderableWidget(rightBtn = new ExtendedButton(right - 20, texSelectY, 15, 15, Component.literal(">"), _ -> cycleTexRight()));

		updateButtons();
	}

	@Override
	public void extractWidgetRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		super.extractWidgetRenderState(gui, mouseX, mouseY, partialTick);

		final ClientPartInfo partInfo = parent.getEditingPartInfo();

		gui.fill(20, variantSelectY, right - 20, texSelectY + 15, 0x55000000);

		// Texture select
		gui.centeredText(parent.font(), TailsComponents.TEXTURE_SELECT, right / 2, variantSelectY - 12, 0xFFFFFFFF);

		gui.drawScrollingString(gui.textRenderer(), parent.font(), Component.literal(TailsComponents.getSubTypeName(partInfo)), left + 25, right - 25, variantSelectY + 4);
		gui.drawScrollingString(gui.textRenderer(), parent.font(), Component.literal(TailsComponents.getTextureName(partInfo)), left + 25, right - 25, texSelectY + 4);
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
			leftBtn.active = rightBtn.active = !partInfo.isPartInvalid() && !partInfo.isSubTypeInvalid() && partInfo.getSubType().textures().size() > 1;

		if (variantLeftBtn != null && variantRightBtn != null)
			variantLeftBtn.active = variantRightBtn.active = !partInfo.isPartInvalid() && partInfo.getPart().getSubTypes().size() > 1;
	}
}
