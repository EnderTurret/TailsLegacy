/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import uk.kihira.tails.common.TailsLanguage;

public final class TailsComponents {

	// ControlsPanel

	public static final ITextComponent LIBRARY_MODE = new TextComponentTranslation(TailsLanguage.LIBRARY_MODE);
	public static final ITextComponent EDITOR_MODE = new TextComponentTranslation(TailsLanguage.EDITOR_MODE);
	public static final ITextComponent RESET_BUTTON = new TextComponentTranslation(TailsLanguage.RESET_BUTTON);
	public static final ITextComponent DONE_BUTTON = new TextComponentTranslation(TailsLanguage.DONE_BUTTON);

	// LibraryImportPanel

	public static final ITextComponent IMPORT_STRING = new TextComponentTranslation(TailsLanguage.IMPORT_STRING);

	// LibraryInfoPanel

	public static final ITextComponent LIBRARY_ENTRY_CREATOR = new TextComponentTranslation(TailsLanguage.LIBRARY_ENTRY_CREATOR);
	public static final ITextComponent LIBRARY_ENTRY_CREATION_DATE = new TextComponentTranslation(TailsLanguage.LIBRARY_ENTRY_CREATION_DATE);

	public static final ITextComponent FAVORITE_BUTTON = new TextComponentTranslation(TailsLanguage.FAVORITE_BUTTON);
	public static final ITextComponent DELETE_BUTTON = new TextComponentTranslation(TailsLanguage.DELETE_BUTTON);
	public static final ITextComponent EXPORTED_MESSAGE = new TextComponentTranslation(TailsLanguage.EXPORTED_MESSAGE);
	public static final ITextComponent SHARE_BUTTON = new TextComponentTranslation(TailsLanguage.SHARE_BUTTON);

	// LibraryPanel

	public static final ITextComponent CREATE_ENTRY = new TextComponentTranslation(TailsLanguage.CREATE_ENTRY);
	public static final ITextComponent RELOAD_LIBRARY = new TextComponentTranslation(TailsLanguage.RELOAD_LIBRARY);

	// PartsPanel

	public static final ITextComponent PART_SELECT = new TextComponentTranslation(TailsLanguage.PART_SELECT);
	public static final ITextComponent PART_CREDIT = new TextComponentTranslation(TailsLanguage.PART_CREDIT);
	public static final ITextComponent EMPTY_PART = new TextComponentTranslation(TailsLanguage.EMPTY_PART);

	// PreviewPanel

	public static final ITextComponent PREVIEW_HELP = new TextComponentTranslation(TailsLanguage.PREVIEW_HELP);
	public static final ITextComponent RESET_CAMERA = new TextComponentTranslation(TailsLanguage.RESET_CAMERA);

	// TexturePanel

	public static final ITextComponent TEXTURE_SELECT = new TextComponentTranslation(TailsLanguage.TEXTURE_SELECT);

	// TintPanel

	public static final ITextComponent EDIT_TINT = new TextComponentTranslation(TailsLanguage.EDIT_TINT);
	public static final ITextComponent HUE = new TextComponentTranslation(TailsLanguage.HUE);
	public static final ITextComponent SATURATION = new TextComponentTranslation(TailsLanguage.SATURATION);
	public static final ITextComponent BRIGHTNESS = new TextComponentTranslation(TailsLanguage.BRIGHTNESS);
	public static final ITextComponent RED = new TextComponentTranslation(TailsLanguage.RED);
	public static final ITextComponent GREEN = new TextComponentTranslation(TailsLanguage.GREEN);
	public static final ITextComponent BLUE = new TextComponentTranslation(TailsLanguage.BLUE);
	public static final ITextComponent RESET_TINT = new TextComponentTranslation(TailsLanguage.RESET_TINT);
	public static final ITextComponent COLOR_PICKER_0 = new TextComponentTranslation(TailsLanguage.COLOR_PICKER_0);
	public static final ITextComponent COLOR_PICKER_1 = new TextComponentTranslation(TailsLanguage.COLOR_PICKER_1);
	public static final ITextComponent COLOR_PICKER = new TextComponentString("").appendSibling(COLOR_PICKER_0).appendSibling(new TextComponentString("\n")).appendSibling(COLOR_PICKER_1);

	public static final ITextComponent HEX = new TextComponentTranslation(TailsLanguage.HEX);

	// Miscellaneous

	public static final ITextComponent EDITOR_BUTTON = new TextComponentTranslation(TailsLanguage.EDITOR_BUTTON);
}