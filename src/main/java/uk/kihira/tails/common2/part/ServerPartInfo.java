/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common2.part;

import java.util.Arrays;
import java.util.Objects;

import net.minecraft.resources.ResourceLocation;

import uk.kihira.tails.client.ColorUtil;

/**
 * The server-side implementation of {@link IPartInfo}.
 * @param partId The part id.
 * @param subTypeId The subtype id.
 * @param textureId The texture id.
 * @param tints The tints.
 * @author EnderTurret
 */
public record ServerPartInfo(ResourceLocation partId, String subTypeId, String textureId, int[] tints) implements IPartInfo {

	@Override
	public IPartInfo clone() {
		return new ServerPartInfo(partId, subTypeId, textureId, tints.clone());
	}

	@Override
	public ResourceLocation getPartId() {
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
		return o instanceof ServerPartInfo spi
				&& Objects.equals(partId, spi.partId)
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
				+ ", tints=" + Arrays.stream(tints).mapToObj(t -> "0x" + ColorUtil.hex(t, true, true)).toList()
				+ "]";
	}
}