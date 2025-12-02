/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.common.part.PartsData;

/**
 * Represents a single library entry.
 */
@Internal
public final class LibraryEntryData {

	public String entryName = "";
	public final long creationDate;
	public final UUID creatorUUID;
	/**
	 * Name is used purely for display purposes.
	 */
	public String creatorName;
	public boolean favourite;
	public final PartsData partsData;

	public LibraryEntryData(String entryName, long creationDate, UUID creatorUUID, String creatorName, boolean favourite, PartsData partsData) {
		this.entryName = entryName;
		this.creationDate = creationDate;
		this.creatorUUID = creatorUUID;
		this.creatorName = creatorName;
		this.favourite = favourite;
		this.partsData = partsData;
	}

	public LibraryEntryData(UUID creatorUUID, String creatorName, String name, PartsData partsData) {
		this(name, Instant.now().toEpochMilli(), creatorUUID, creatorName, false, partsData);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LibraryEntryData)) return false;
		final LibraryEntryData data = (LibraryEntryData) o;

		return creationDate == data.creationDate && favourite == data.favourite && creatorUUID.equals(data.creatorUUID)
				&& Objects.equals(entryName, data.entryName) && Objects.equals(partsData, data.partsData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(creationDate, favourite, creatorUUID, entryName, partsData);
	}
}
