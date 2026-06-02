/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.gui.panel;

import java.text.SimpleDateFormat;

import net.enderturret.tailslegacy.common.LibraryEntryData;
import net.enderturret.tailslegacy.common.client.part.LocalPartManager;

public interface BaseLibraryInfoPanel {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/YY");

	public default String exportString(LibraryEntryData data) {
		final StringBuilder sb = new StringBuilder();
		sb.append(data.entryName).append(":");
		sb.append(data.creatorUUID).append(":");
		sb.append(LocalPartManager.GSON.toJson(data.partsData));
		return sb.toString();
	}
}