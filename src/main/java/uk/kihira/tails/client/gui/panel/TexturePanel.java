/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.common2.client.part.ClientPartInfo;
import uk.kihira.tails.common2.client.part.Part;
import uk.kihira.tails.common2.client.part.PartRegistry;

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
			final String texLangKey = partInfo.isEmpty() ? "tails.texture.none" : part.getId().t$getNamespace() + ".part." + part.getId().t$getPath() + ".texture." + partInfo.getPartTexture().id();
			texFormatted = I18n.get(texLangKey);
			texTranslated = !texLangKey.equals(texFormatted);
		} else texFormatted = partInfo.getTextureId();

		final String variantFormatted;
		boolean variantTranslated = true;

		if (partInfo.isEmpty() || partInfo.getSubType() != null) {
			final String variantLangKey = partInfo.isEmpty() ? "tails.subtype.none" : part.getTranslationKey() + ".subtype." + partInfo.getSubType().id();
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
		final ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();
		final Part.PartTexture texture = cycle(partInfo.getSubType().textures(), partInfo.getPartTexture(), -1);
		parent.setPartsInfo(new ClientPartInfo(partInfo.getTints(), PartRegistry.reference(partInfo.getPartId()), partInfo.getSubTypeId(), texture.id()));
	}

	private void cycleTexRight() {
		final ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();
		final Part.PartTexture texture = cycle(partInfo.getSubType().textures(), partInfo.getPartTexture(), 1);
		parent.setPartsInfo(new ClientPartInfo(partInfo.getTints(), PartRegistry.reference(partInfo.getPartId()), partInfo.getSubTypeId(), texture.id()));
	}

	private void cycleVariantLeft() {
		final ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();
		final Part.SubType newType = cycle(part.getSubTypes(), partInfo.getSubType(), -1);
		parent.setPartsInfo(new ClientPartInfo(partInfo.getTints(), PartRegistry.reference(partInfo.getPartId()), newType.id(), partInfo.getTextureId()));
	}

	private void cycleVariantRight() {
		final ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();
		final Part.SubType newType = cycle(part.getSubTypes(), partInfo.getSubType(), 1);
		parent.setPartsInfo(new ClientPartInfo(partInfo.getTints(), PartRegistry.reference(partInfo.getPartId()), newType.id(), partInfo.getTextureId()));
	}

	private static <T> T cycle(List<T> elements, T current, int direction) {
		final int index = elements.indexOf(current);

		int next = index + direction;

		if (next < 0) next = elements.size() - 1;
		else if (next == elements.size()) next = 0;

		return elements.get(next);
	}

	public void updateButtons() {
		final ClientPartInfo partInfo = parent.getEditingPartInfo();
		final Part part = partInfo.getPart();

		if (leftBtn != null && rightBtn != null)
			leftBtn.active = rightBtn.active = !partInfo.isInvalid() && partInfo.getSubType().textures().size() > 1;

		if (variantLeftBtn != null && variantRightBtn != null)
			variantLeftBtn.active = variantRightBtn.active = !partInfo.isInvalid() && part.getSubTypes().size() > 1;
	}
}
