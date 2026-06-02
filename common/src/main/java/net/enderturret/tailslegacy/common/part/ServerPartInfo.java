/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.part;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import net.enderturret.tailslegacy.common.JavaColor;
import net.enderturret.tailslegacy.common.client.duck.TResourceLocation;

/**
 * The server-side implementation of {@link IPartInfo}.
 * @author EnderTurret
 */
public final class ServerPartInfo implements IPartInfo {

	private final TResourceLocation partId;
	private final String subTypeId;
	private final String textureId;
	private final int[] tints;

	/**
	 * Constructs a new {@code ServerPartInfo}.
	 * @param partId The part id.
	 * @param subTypeId The subtype id.
	 * @param textureId The texture id.
	 * @param tints The tints.
	 */
	public ServerPartInfo(TResourceLocation partId, String subTypeId, String textureId, int[] tints) {
		this.partId = partId;
		this.subTypeId = subTypeId;
		this.textureId = textureId;
		this.tints = tints;
	}

	@Override
	public IPartInfo clone() {
		return new ServerPartInfo(partId, subTypeId, textureId, tints.clone());
	}

	@Override
	public TResourceLocation getPartId() {
		return partId;
	}

	@Override
	public String getSubTypeId() {
		return subTypeId;
	}

	@Override
	public String getTextureId() {
		return textureId;
	}

	@Override
	public int[] getTints() {
		return tints;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ServerPartInfo)) return false;
		final ServerPartInfo spi = (ServerPartInfo) o;
		return Objects.equals(partId, spi.partId)
				&& Objects.equals(subTypeId, spi.subTypeId)
				&& Objects.equals(textureId, spi.textureId)
				&& Arrays.equals(tints, spi.tints);
	}

	@Override
	public int hashCode() {
		return Objects.hash(partId, subTypeId, textureId, tints);
	}

	@Override
	public String toString() {
		return "ServerPartInfo[partId=" + partId
				+ ", subTypeId=" + subTypeId
				+ ", textureId=" + textureId
				+ ", tints=" + Arrays.stream(tints).mapToObj(t -> "0x" + JavaColor.hex(t, true)).collect(Collectors.toList())
				+ "]";
	}
}