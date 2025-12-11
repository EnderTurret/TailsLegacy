/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import uk.kihira.tails.common.TailsLanguage;
import uk.kihira.tails.common.client.part.ClientPartInfo;

public final class TailsComponents {

	// ControlsPanel

	public static final Component LIBRARY_MODE = new TranslatableComponent(TailsLanguage.LIBRARY_MODE);
	public static final Component EDITOR_MODE = new TranslatableComponent(TailsLanguage.EDITOR_MODE);
	public static final Component RESET_BUTTON = new TranslatableComponent(TailsLanguage.RESET_BUTTON);
	public static final Component DONE_BUTTON = new TranslatableComponent(TailsLanguage.DONE_BUTTON);

	// LibraryImportPanel

	public static final Component IMPORT_STRING = new TranslatableComponent(TailsLanguage.IMPORT_STRING);

	// LibraryInfoPanel

	public static final Component LIBRARY_ENTRY_CREATOR = new TranslatableComponent(TailsLanguage.LIBRARY_ENTRY_CREATOR);
	public static final Component LIBRARY_ENTRY_CREATION_DATE = new TranslatableComponent(TailsLanguage.LIBRARY_ENTRY_CREATION_DATE);

	public static final Component FAVORITE_BUTTON = new TranslatableComponent(TailsLanguage.FAVORITE_BUTTON);
	public static final Component DELETE_BUTTON = new TranslatableComponent(TailsLanguage.DELETE_BUTTON);
	public static final Component EXPORTED_MESSAGE = new TranslatableComponent(TailsLanguage.EXPORTED_MESSAGE);
	public static final Component SHARE_BUTTON = new TranslatableComponent(TailsLanguage.SHARE_BUTTON);

	// LibraryPanel

	public static final Component CREATE_ENTRY = new TranslatableComponent(TailsLanguage.CREATE_ENTRY);
	public static final Component RELOAD_LIBRARY = new TranslatableComponent(TailsLanguage.RELOAD_LIBRARY);

	// PartsPanel

	public static final Component PART_SELECT = new TranslatableComponent(TailsLanguage.PART_SELECT);
	public static final Component PART_CREDIT = new TranslatableComponent(TailsLanguage.PART_CREDIT);
	public static final Component EMPTY_PART = new TranslatableComponent(TailsLanguage.EMPTY_PART);

	// PreviewPanel

	public static final Component PREVIEW_HELP = new TranslatableComponent(TailsLanguage.PREVIEW_HELP);
	public static final Component RESET_CAMERA = new TranslatableComponent(TailsLanguage.RESET_CAMERA);

	// TexturePanel

	public static final Component TEXTURE_SELECT = new TranslatableComponent(TailsLanguage.TEXTURE_SELECT);

	// TintPanel

	public static final Component EDIT_TINT = new TranslatableComponent(TailsLanguage.EDIT_TINT);
	public static final Component HUE = new TranslatableComponent(TailsLanguage.HUE);
	public static final Component SATURATION = new TranslatableComponent(TailsLanguage.SATURATION);
	public static final Component BRIGHTNESS = new TranslatableComponent(TailsLanguage.BRIGHTNESS);
	public static final Component RED = new TranslatableComponent(TailsLanguage.RED);
	public static final Component GREEN = new TranslatableComponent(TailsLanguage.GREEN);
	public static final Component BLUE = new TranslatableComponent(TailsLanguage.BLUE);
	public static final Component RESET_TINT = new TranslatableComponent(TailsLanguage.RESET_TINT);
	public static final Component COLOR_PICKER_0 = new TranslatableComponent(TailsLanguage.COLOR_PICKER_0);
	public static final Component COLOR_PICKER_1 = new TranslatableComponent(TailsLanguage.COLOR_PICKER_1);
	public static final Component COLOR_PICKER = TextComponent.EMPTY.copy().append(COLOR_PICKER_0).append(new TextComponent("\n")).append(COLOR_PICKER_1);

	public static final Component HEX = new TranslatableComponent(TailsLanguage.HEX);

	// Miscellaneous

	public static final Component EDITOR_BUTTON = new TranslatableComponent(TailsLanguage.EDITOR_BUTTON);

	public static String getPartName(ClientPartInfo partInfo) {
		if (!partInfo.isEmpty() && partInfo.getPart() == null) return partInfo.getPartId().toString();
		return I18n.get(partInfo.getPart().getTranslationKey());
	}

	public static String getSubTypeName(ClientPartInfo partInfo) {
		if (!partInfo.isEmpty() && partInfo.getSubType() == null) return partInfo.getSubTypeId();

		final String key = partInfo.getSubTypeTranslationKey();
		if (I18n.exists(key)) return I18n.get(key);

		final String fallback = partInfo.getFallbackSubTypeTranslationKey();
		return fallback != null ? I18n.get(fallback) : key;
	}

	public static String getTextureName(ClientPartInfo partInfo) {
		if (!partInfo.isEmpty() && partInfo.getPartTexture() == null) return partInfo.getTextureId();

		final String key = partInfo.getTextureTranslationKey();
		if (I18n.exists(key)) return I18n.get(key);

		final String fallback = partInfo.getFallbackTextureTranslationKey();
		return fallback != null ? I18n.get(fallback) : key;
	}
}