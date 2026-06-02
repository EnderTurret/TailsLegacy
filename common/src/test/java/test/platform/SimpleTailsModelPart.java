package test.platform;

import java.util.LinkedHashMap;
import java.util.Map;

import net.enderturret.tailslegacy.common.client.duck.TailsBuffer;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.duck.TailsRandomSource;
import net.enderturret.tailslegacy.common.client.model.TailsPartDefinition;

public final class SimpleTailsModelPart implements TailsModelPart {

	private final String breadcrumb;
	private final Map<String, SimpleTailsModelPart> parentMap;
	private final Map<String, SimpleTailsModelPart> children = new LinkedHashMap<>();
	public boolean frozen = false;

	public float xRot;
	public float yRot;
	public float zRot;

	public SimpleTailsModelPart(String breadcrumb, Map<String, SimpleTailsModelPart> parentMap) {
		this.breadcrumb = breadcrumb;
		this.parentMap = parentMap;
	}

	@Override
	public boolean t$isVisible() { throw new UnsupportedOperationException(); }

	@Override
	public void t$setVisible(boolean value) { throw new UnsupportedOperationException(); }

	@Override
	public float t$getXRot() { return xRot; }

	@Override
	public void t$setXRot(float value) { xRot = value; }

	@Override
	public float t$getYRot() { return yRot; }

	@Override
	public void t$setYRot(float value) { yRot = value; }

	@Override
	public float t$getZRot() { return zRot; }

	@Override
	public void t$setZRot(float value) { zRot = value; }

	@Override
	public boolean t$hasInitialPose() { throw new UnsupportedOperationException(); }

	@Override
	public float t$getInitialXRot() { return 0; }

	@Override
	public float t$getInitialYRot() { return 0; }

	@Override
	public float t$getInitialZRot() { return 0; }

	@Override
	public boolean t$isEmpty() { throw new UnsupportedOperationException(); }

	@Override
	public TailsModelPart t$getChild(String name) {
		return children.computeIfAbsent(name, k -> {
			if (frozen) throw new UnsupportedOperationException("Frozen, cannot create " + k);

			final SimpleTailsModelPart ret = new SimpleTailsModelPart((breadcrumb.isEmpty() ? "" : breadcrumb + '.') + k, parentMap);
			parentMap.put(ret.breadcrumb, ret);

			return ret;
		});
	}

	@Override
	public Map<String, TailsModelPart> t$getChildren() { throw new UnsupportedOperationException(); }

	@Override
	public CubePose t$getRandomCube(TailsRandomSource random) { throw new UnsupportedOperationException(); }

	@Override
	public void t$render(TailsPoseStack pose, TailsBuffer buffer, int packedLight, int packedOverlay, int color) { throw new UnsupportedOperationException(); }

	@Override
	public void t$translateAndRotate(TailsPoseStack poseStack) { throw new UnsupportedOperationException(); }

	public CubePose savePose() {
		return new CubePose(xRot, yRot, zRot, 0, 0, 0);
	}
}