/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.client.duck;

import java.util.Map;

public interface TailsModelPart {

	public boolean t$isVisible();
	public void t$setVisible(boolean value);

	public float t$getXRot();
	public void t$setXRot(float value);
	public float t$getYRot();
	public void t$setYRot(float value);
	public float t$getZRot();
	public void t$setZRot(float value);

	public boolean t$hasInitialPose();
	public float t$getInitialXRot();
	public float t$getInitialYRot();
	public float t$getInitialZRot();

	public boolean t$isEmpty();
	public TailsModelPart t$getChild(String name);
	public Map<String, TailsModelPart> t$getChildren();
	public CubePose t$getRandomCube(TailsRandomSource random);

	public void t$render(TailsPoseStack pose, TailsBuffer buffer, int packedLight, int packedOverlay, int color);
	public void t$translateAndRotate(TailsPoseStack poseStack);

	public static final class CubePose {

		public final float minX, maxX;
		public final float minY, maxY;
		public final float minZ, maxZ;

		public CubePose(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}
	}
}