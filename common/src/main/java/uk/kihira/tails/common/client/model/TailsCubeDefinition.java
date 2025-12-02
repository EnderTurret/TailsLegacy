package uk.kihira.tails.common.client.model;

import java.util.EnumSet;
import java.util.Set;

import uk.kihira.tails.common.TailsDirection;

public final class TailsCubeDefinition {

	public final float x, y, z;
	public final float sizeX, sizeY, sizeZ;
	public final float growX, growY, growZ;
	public final boolean mirror;
	public final float u, v;
	public final Set<TailsDirection> visibleFaces;

	public TailsCubeDefinition(float x, float y, float z,
			float sizeX, float sizeY, float sizeZ,
			float growX, float growY, float growZ,
			boolean mirror, float u, float v,
			Set<TailsDirection> visibleFaces) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.growX = growX;
		this.growY = growY;
		this.growZ = growZ;
		this.mirror = mirror;
		this.u = u;
		this.v = v;
		this.visibleFaces = visibleFaces;
	}

	public TailsCubeDefinition(float x, float y, float z,
			float sizeX, float sizeY, float sizeZ,
			boolean mirror, float u, float v) {
		this(x, y, z, sizeX, sizeY, sizeZ, 0, 0, 0, mirror, u, v, EnumSet.allOf(TailsDirection.class));
	}
}