/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import com.google.gson.annotations.Expose;

import uk.kihira.tails.common.part.PartsData;

/**
 * Represents a single library entry.
 */
public class LibraryEntryData {

	@Expose
	public final PartsData partsData;
	@Expose
	public String entryName = "";
	@Expose
	public boolean favourite;
	@Expose
	public final long creationDate;
	@Expose
	public final UUID creatorUUID;
	/**
	 * Name is used purely for display purposes.
	 */
	@Expose
	public String creatorName;
	public boolean remoteEntry = false;

	public LibraryEntryData(UUID creatorUUID, String creatorName, String name, PartsData partsData) {
		entryName = name;
		this.partsData = partsData;
		creationDate = Calendar.getInstance().getTimeInMillis();
		this.creatorUUID = creatorUUID;
		this.creatorName = creatorName;
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final LibraryEntryData data = (LibraryEntryData) o;

		if (creationDate != data.creationDate) return false;
		if (favourite != data.favourite) return false;
		if (!creatorUUID.equals(data.creatorUUID)) return false;
		if (entryName != null ? !entryName.equals(data.entryName) : data.entryName != null) return false;
		if (partsData != null ? !partsData.equals(data.partsData) : data.partsData != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(partsData == null ? 0 : partsData.hashCode(), "", creatorUUID, favourite, creationDate);
	}
}
