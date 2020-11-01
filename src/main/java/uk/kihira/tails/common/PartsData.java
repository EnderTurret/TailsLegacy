/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class PartsData {

	@Expose
	public Map<PartType, PartInfo> partInfoMap = new EnumMap<>(PartType.class);

	public PartsData() {}
	public PartsData(Map<PartType,PartInfo> partData) {
		partInfoMap = partData;
	}

	public void setPartInfo(PartType partType, PartInfo partInfo) {
		partInfoMap.put(partType, partInfo);
	}

	public PartInfo getPartInfo(PartType partType) {
		return partInfoMap.get(partType);
	}

	public boolean hasPartInfo(PartType partType) {
		return partInfoMap.containsKey(partType) && partInfoMap.get(partType).hasPart;
	}

	public void clearTextures() {
		for (PartInfo partInfo : partInfoMap.values())
			if (partInfo != null) partInfo.setTexture(null);
	}

	public PartsData deepCopy() {
		final Map<PartType,PartInfo> data = new EnumMap<>(PartType.class);
		for (Map.Entry<PartType,PartInfo> e : partInfoMap.entrySet()) {
			data.put(e.getKey(), e.getValue().deepCopy());
		}

		return new PartsData(data);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final PartsData partsData = (PartsData) o;

		return partInfoMap != null ? partInfoMap.equals(partsData.partInfoMap) : partsData.partInfoMap == null;

	}

	@Override
	public int hashCode() {
		return partInfoMap != null ? partInfoMap.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "PartsData{" +
				"partInfoMap=" + partInfoMap +
				'}';
	}

	// NOTE: We rely on the order of this, don't re-arrange, only append! Order is for legacy reasons.
	public static enum PartType {
		TAIL,
		EARS,
		WINGS,
		MUZZLE
	}
}