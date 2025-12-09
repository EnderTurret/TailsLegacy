package uk.kihira.tails.forge.client.platform;

import net.minecraft.client.renderer.GlStateManager;

import uk.kihira.tails.common.client.duck.TailsPoseStack;

public final class TailsPoseStackImpl implements TailsPoseStack, TailsPoseStack.Entry {

	public static final TailsPoseStack INSTANCE = new TailsPoseStackImpl();

	private TailsPoseStackImpl() {}

	@Override
	public void t$push() {
		GlStateManager.pushMatrix();
	}

	@Override
	public void t$pop() {
		GlStateManager.popMatrix();
	}

	@Override
	public void t$translate(double x, double y, double z) {
		GlStateManager.translate(x, y, z);
	}

	@Override
	public void t$translate(float x, float y, float z) {
		GlStateManager.translate(x, y, z);
	}

	@Override
	public void t$rotateX(float radians) {
		GlStateManager.rotate(radians, 1, 0, 0);
	}

	@Override
	public void t$rotateY(float radians) {
		GlStateManager.rotate(radians, 0, 1, 0);
	}

	@Override
	public void t$rotateZ(float radians) {
		GlStateManager.rotate(radians, 0, 0, 1);
	}

	@Override
	public void t$scale(float x, float y, float z) {
		GlStateManager.scale(x, y, z);
	}

	@Override
	public Entry t$lastEntry() {
		return this;
	}
}