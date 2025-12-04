/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui;

import net.minecraft.network.chat.Component;

import uk.kihira.tails.common.TailsLanguage;

public final class TailsComponents {

	// ControlsPanel

	public static final Component LIBRARY_MODE = Component.translatable(TailsLanguage.LIBRARY_MODE);
	public static final Component EDITOR_MODE = Component.translatable(TailsLanguage.EDITOR_MODE);
	public static final Component RESET_BUTTON = Component.translatable(TailsLanguage.RESET_BUTTON);
	public static final Component DONE_BUTTON = Component.translatable(TailsLanguage.DONE_BUTTON);

	// LibraryImportPanel

	public static final Component IMPORT_STRING = Component.translatable(TailsLanguage.IMPORT_STRING);

	// LibraryInfoPanel

	public static final Component LIBRARY_ENTRY_CREATOR = Component.translatable(TailsLanguage.LIBRARY_ENTRY_CREATOR);
	public static final Component LIBRARY_ENTRY_CREATION_DATE = Component.translatable(TailsLanguage.LIBRARY_ENTRY_CREATION_DATE);

	public static final Component FAVORITE_BUTTON = Component.translatable(TailsLanguage.FAVORITE_BUTTON);
	public static final Component DELETE_BUTTON = Component.translatable(TailsLanguage.DELETE_BUTTON);
	public static final Component EXPORTED_MESSAGE = Component.translatable(TailsLanguage.EXPORTED_MESSAGE);
	public static final Component SHARE_BUTTON = Component.translatable(TailsLanguage.SHARE_BUTTON);

	// LibraryPanel

	public static final Component CREATE_ENTRY = Component.translatable(TailsLanguage.CREATE_ENTRY);
	public static final Component RELOAD_LIBRARY = Component.translatable(TailsLanguage.RELOAD_LIBRARY);

	// PartsPanel

	public static final Component PART_SELECT = Component.translatable(TailsLanguage.PART_SELECT);
	public static final Component PART_CREDIT = Component.translatable(TailsLanguage.PART_CREDIT);
	public static final Component EMPTY_PART = Component.translatable(TailsLanguage.EMPTY_PART);

	// PreviewPanel

	public static final Component PREVIEW_HELP = Component.translatable(TailsLanguage.PREVIEW_HELP);
	public static final Component RESET_CAMERA = Component.translatable(TailsLanguage.RESET_CAMERA);

	// TexturePanel

	public static final Component TEXTURE_SELECT = Component.translatable(TailsLanguage.TEXTURE_SELECT);

	// TintPanel

	public static final Component EDIT_TINT = Component.translatable(TailsLanguage.EDIT_TINT);
	public static final Component HUE = Component.translatable(TailsLanguage.HUE);
	public static final Component SATURATION = Component.translatable(TailsLanguage.SATURATION);
	public static final Component BRIGHTNESS = Component.translatable(TailsLanguage.BRIGHTNESS);
	public static final Component RED = Component.translatable(TailsLanguage.RED);
	public static final Component GREEN = Component.translatable(TailsLanguage.GREEN);
	public static final Component BLUE = Component.translatable(TailsLanguage.BLUE);
	public static final Component RESET_TINT = Component.translatable(TailsLanguage.RESET_TINT);
	public static final Component COLOR_PICKER_0 = Component.translatable(TailsLanguage.COLOR_PICKER_0);
	public static final Component COLOR_PICKER_1 = Component.translatable(TailsLanguage.COLOR_PICKER_1);
	public static final Component COLOR_PICKER = Component.empty().append(COLOR_PICKER_0).append(Component.literal("\n")).append(COLOR_PICKER_1);

	public static final Component HEX = Component.translatable(TailsLanguage.HEX);

	// Miscellaneous

	public static final Component EDITOR_BUTTON = Component.translatable(TailsLanguage.EDITOR_BUTTON);
}