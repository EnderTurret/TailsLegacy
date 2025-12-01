package uk.kihira.tails.common2.client.duck;

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

	public void t$render(TailsPoseStack pose, TailsBuffer buffer, int packedLight, int packedOverlay, int color);
	public void t$translateAndRotate(TailsPoseStack poseStack);
}