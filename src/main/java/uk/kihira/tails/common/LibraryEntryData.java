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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LibraryEntryData data)) return false;

		return creationDate == data.creationDate && favourite == data.favourite && creatorUUID.equals(data.creatorUUID)
				&& Objects.equals(entryName, data.entryName) && Objects.equals(partsData, data.partsData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(creationDate, favourite, creatorUUID, entryName, partsData);
	}
}
