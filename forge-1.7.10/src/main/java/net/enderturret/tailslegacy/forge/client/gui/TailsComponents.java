/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import net.enderturret.tailslegacy.common.TailsLanguage;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;

public final class TailsComponents {

	// ControlsPanel

	public static final IChatComponent LIBRARY_MODE = new ChatComponentTranslation(TailsLanguage.LIBRARY_MODE);
	public static final IChatComponent EDITOR_MODE = new ChatComponentTranslation(TailsLanguage.EDITOR_MODE);
	public static final IChatComponent RESET_BUTTON = new ChatComponentTranslation(TailsLanguage.RESET_BUTTON);
	public static final IChatComponent DONE_BUTTON = new ChatComponentTranslation(TailsLanguage.DONE_BUTTON);

	// LibraryImportPanel

	public static final IChatComponent IMPORT_STRING = new ChatComponentTranslation(TailsLanguage.IMPORT_STRING);

	// LibraryInfoPanel

	public static final IChatComponent LIBRARY_ENTRY_CREATOR = new ChatComponentTranslation(TailsLanguage.LIBRARY_ENTRY_CREATOR);
	public static final IChatComponent LIBRARY_ENTRY_CREATION_DATE = new ChatComponentTranslation(TailsLanguage.LIBRARY_ENTRY_CREATION_DATE);

	public static final IChatComponent FAVORITE_BUTTON = new ChatComponentTranslation(TailsLanguage.FAVORITE_BUTTON);
	public static final IChatComponent DELETE_BUTTON = new ChatComponentTranslation(TailsLanguage.DELETE_BUTTON);
	public static final IChatComponent EXPORTED_MESSAGE = new ChatComponentTranslation(TailsLanguage.EXPORTED_MESSAGE);
	public static final IChatComponent SHARE_BUTTON = new ChatComponentTranslation(TailsLanguage.SHARE_BUTTON);

	// LibraryPanel

	public static final IChatComponent CREATE_ENTRY = new ChatComponentTranslation(TailsLanguage.CREATE_ENTRY);
	public static final IChatComponent RELOAD_LIBRARY = new ChatComponentTranslation(TailsLanguage.RELOAD_LIBRARY);

	// PartsPanel

	public static final IChatComponent PART_SELECT = new ChatComponentTranslation(TailsLanguage.PART_SELECT);
	public static final IChatComponent PART_CREDIT = new ChatComponentTranslation(TailsLanguage.PART_CREDIT);
	public static final IChatComponent EMPTY_PART = new ChatComponentTranslation(TailsLanguage.EMPTY_PART);

	// PreviewPanel

	public static final IChatComponent PREVIEW_HELP = new ChatComponentTranslation(TailsLanguage.PREVIEW_HELP);
	public static final IChatComponent RESET_CAMERA = new ChatComponentTranslation(TailsLanguage.RESET_CAMERA);

	// TexturePanel

	public static final IChatComponent TEXTURE_SELECT = new ChatComponentTranslation(TailsLanguage.TEXTURE_SELECT);

	// TintPanel

	public static final IChatComponent EDIT_TINT = new ChatComponentTranslation(TailsLanguage.EDIT_TINT);
	public static final IChatComponent HUE = new ChatComponentTranslation(TailsLanguage.HUE);
	public static final IChatComponent SATURATION = new ChatComponentTranslation(TailsLanguage.SATURATION);
	public static final IChatComponent BRIGHTNESS = new ChatComponentTranslation(TailsLanguage.BRIGHTNESS);
	public static final IChatComponent RED = new ChatComponentTranslation(TailsLanguage.RED);
	public static final IChatComponent GREEN = new ChatComponentTranslation(TailsLanguage.GREEN);
	public static final IChatComponent BLUE = new ChatComponentTranslation(TailsLanguage.BLUE);
	public static final IChatComponent RESET_TINT = new ChatComponentTranslation(TailsLanguage.RESET_TINT);
	public static final IChatComponent COLOR_PICKER_0 = new ChatComponentTranslation(TailsLanguage.COLOR_PICKER_0);
	public static final IChatComponent COLOR_PICKER_1 = new ChatComponentTranslation(TailsLanguage.COLOR_PICKER_1);
	public static final IChatComponent COLOR_PICKER = new ChatComponentText("").appendSibling(COLOR_PICKER_0).appendSibling(new ChatComponentText("\n")).appendSibling(COLOR_PICKER_1);

	public static final IChatComponent HEX = new ChatComponentTranslation(TailsLanguage.HEX);

	// Miscellaneous

	public static final IChatComponent EDITOR_BUTTON = new ChatComponentTranslation(TailsLanguage.EDITOR_BUTTON);

	public static String getPartName(ClientPartInfo partInfo) {
		if (!partInfo.isEmpty() && partInfo.getPart() == null) return partInfo.getPartId().toString();
		return I18n.format(partInfo.getPart().getTranslationKey());
	}

	public static String getSubTypeName(ClientPartInfo partInfo) {
		if (!partInfo.isEmpty() && partInfo.getSubType() == null) return partInfo.getSubTypeId();

		final String key = partInfo.getSubTypeTranslationKey();
		if (I18n_hasKey(key)) return I18n.format(key);

		final String fallback = partInfo.getFallbackSubTypeTranslationKey();
		return fallback != null ? I18n.format(fallback) : key;
	}

	public static String getTextureName(ClientPartInfo partInfo) {
		if (!partInfo.isEmpty() && partInfo.getPartTexture() == null) return partInfo.getTextureId();

		final String key = partInfo.getTextureTranslationKey();
		if (I18n_hasKey(key)) return I18n.format(key);

		final String fallback = partInfo.getFallbackTextureTranslationKey();
		return fallback != null ? I18n.format(fallback) : key;
	}

	private static boolean I18n_hasKey(String key) {
		return !I18n.format(key).equals(key);
	}
}