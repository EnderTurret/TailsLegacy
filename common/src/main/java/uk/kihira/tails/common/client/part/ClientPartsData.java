/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.part;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartsData;

public class ClientPartsData extends PartsData {

	/**
	 * The singleton empty {@link ClientPartsData}.
	 */
	public static final ClientPartsData EMPTY = new ClientPartsData() {
		@Override public void addPartInfo(IPartInfo partInfo) {}
		@Override public void setPartInfo(AttachmentPoint attachment, ClientPartInfo info) {}
		@Override public ClientPartInfo getPartInfo(AttachmentPoint attachment) { return ClientPartInfo.empty(); }
		@Override public Set<IPartInfo> getPartInfos() { return Collections.emptySet(); }
		@Override public Set<ClientPartInfo> getParts() { return Collections.emptySet(); }
		@Override public void clearTextures() {}
		@Override public ClientPartsData deepCopy() { return this; }
		@Override public String toString() { return "ClientPartsData#EMPTY"; }
		@Override public boolean isEmpty() { return true; }
	};

	public ClientPartsData() {
		super();
	}

	public ClientPartsData(Set<IPartInfo> parts) {
		super(coerceAll(parts));
	}

	private static Set<IPartInfo> coerceAll(Set<IPartInfo> parts) {
		return Collections.unmodifiableSet(parts.stream().map(ClientPartInfo::coerce).collect(Collectors.toSet()));
	}

	public ClientPartInfo getPartInfo(AttachmentPoint attachment) {
		for (ClientPartInfo info : getParts())
			if (info.getPart().getAttachment().equals(attachment))
				return info;

		return empty();
	}

	public void setPartInfo(AttachmentPoint attachment, ClientPartInfo info) {
		Objects.requireNonNull(attachment);
		Objects.requireNonNull(info);

		for (Iterator<IPartInfo> it = parts.iterator(); it.hasNext(); )
			if (((ClientPartInfo) it.next()).getPart().getAttachment().equals(attachment))
				it.remove();

		addPartInfo(info);
	}

	@Override
	protected ClientPartInfo empty() {
		return ClientPartInfo.empty();
	}

	@Override
	public void addPartInfo(IPartInfo partInfo) {
		super.addPartInfo(ClientPartInfo.coerce(partInfo));
	}

	@SuppressWarnings({ "cast", "unchecked" })
	public Set<ClientPartInfo> getParts() {
		return (Set<ClientPartInfo>) (Set) super.getPartInfos();
	}

	@Override
	public ClientPartsData deepCopy() {
		return ClientPartsData.clone(super.deepCopy());
	}

	public static ClientPartsData clone(PartsData data) {
		if (data.isEmpty()) return ClientPartsData.EMPTY;
		return new ClientPartsData(data.getPartInfos());
	}
}