package uk.kihira.tails.common.client.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TailsPartDefinition {

	public final List<TailsCubeDefinition> cubes;
	public final Map<String, TailsPartDefinition> children = new LinkedHashMap<>();

	public float xOffset;
	public float yOffset;
	public float zOffset;
	public float xRot;
	public float yRot;
	public float zRot;

	public TailsPartDefinition(List<TailsCubeDefinition> cubes, float xOffset, float yOffset, float zOffset, float xRot, float yRot, float zRot) {
		this.cubes = cubes;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.xRot = xRot;
		this.yRot = yRot;
		this.zRot = zRot;
	}

	public TailsPartDefinition(List<TailsCubeDefinition> cubes) {
		this.cubes = cubes;
	}

	public TailsPartDefinition addChild(String name, TailsPartDefinition child) {
		children.put(name, child);
		return this;
	}
}