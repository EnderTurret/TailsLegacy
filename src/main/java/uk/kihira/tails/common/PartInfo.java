/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class PartInfo implements Cloneable {

	@Expose public final boolean hasPart;
	@Expose public final int typeid;
	@Expose public final int subid;
	@Expose public final int[] tints;
	@Expose public final int textureID;
	@Expose public PartsData.PartType partType; // Not final to preserve compat.
	@Expose public final float scale;
	private ResourceLocation texture;
	public boolean needsTextureCompile = true;

	public PartInfo(boolean hasPart, int type, int subtype, int textureID, int[] tints, PartsData.PartType partType, float scale, ResourceLocation texture) {
		this.hasPart = hasPart;
		typeid = type;
		subid = subtype;
		this.textureID = textureID;
		this.tints = tints;
		this.partType = partType;
		this.scale = scale;
		this.texture = texture;
	}

	public PartInfo(boolean hasPart, int type, int subtype, int textureID, int tint1, int tint2, int tint3, float scale, ResourceLocation texture, PartsData.PartType partType) {
		this(hasPart, type, subtype, textureID, new int[] {tint1, tint2, tint3}, partType, scale, texture);
	}

	public static PartInfo none(PartsData.PartType partType) {
		return new PartInfo(false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 1F, null, partType);
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public void setTexture(ResourceLocation texture) {
		if (texture == null || this.texture != null && !this.texture.equals(texture)) {
			try {
				Minecraft.getInstance().getTextureManager().deleteTexture(this.texture);
			} catch (Exception ignored) {}

			needsTextureCompile = true;
		} else
			needsTextureCompile = false;
		this.texture = texture;
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final PartInfo partInfo = (PartInfo) o;

		if (hasPart != partInfo.hasPart) return false;
		if (subid != partInfo.subid) return false;
		if (textureID != partInfo.textureID) return false;
		if (typeid != partInfo.typeid) return false;
		if (partType != partInfo.partType) return false;
		if (!Arrays.equals(tints, partInfo.tints)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = hasPart ? 1 : 0;
		result = 31 * result + typeid;
		result = 31 * result + subid;
		result = 31 * result + Arrays.hashCode(tints);
		result = 31 * result + textureID;
		result = 31 * result + partType.hashCode();
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("PartInfo{");
		sb.append("hasPart=").append(hasPart);
		sb.append(", typeId=").append(guessTypeFromId());
		sb.append(", subid=").append(subid);
		sb.append(", tints=").append(Arrays.stream(tints).mapToObj(tint -> "0x" + Integer.toHexString(tint)).collect(Collectors.joining(", ", "[", "]")));
		if (textureID != 0)
			sb.append(", textureID=").append(textureID);
		sb.append(", partType=").append(partType.name());
		if (texture != null)
			sb.append(", texture=").append(texture);
		sb.append("}");

		return sb.toString();
	}

	public String guessTypeFromId() {
		if (partType == PartsData.PartType.TAIL) {
			if (typeid == 0) return "Fluffy";
			if (typeid == 1) return "Dragon";
			if (typeid == 2) return "Raccoon";
			if (typeid == 3) return "Devil";
			if (typeid == 4) return "Cat";
			if (typeid == 5) return "Bird";
			if (typeid == 6) return "Shark";
			if (typeid == 7) return "Bunny";
		} else if (partType == PartsData.PartType.EARS) {
			if (typeid == 0) return "Fox";
			if (typeid == 1) return "Cat";
			if (typeid == 2) return "Panda";
			if (typeid == 3) return "Small Cat";
		} else if (partType == PartsData.PartType.WINGS) {
			if (typeid == 0) return "Wings";
		} else if (partType == PartsData.PartType.MUZZLE) {
			if (typeid == 0) return "Standard";
			if (typeid == 1) return "Slim";
			if (typeid == 2) return "Thin";
		}

		return "Unknown";
	}

	public PartInfo deepCopy() {
		final int[] tints = new int[this.tints.length];
		for (int i = 0; i < tints.length; i++)
			tints[i] = this.tints[i];

		return new PartInfo(hasPart, typeid, subid, textureID, tints, partType, scale, texture);
	}
}
