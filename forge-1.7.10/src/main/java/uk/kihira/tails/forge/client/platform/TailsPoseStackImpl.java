/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.platform;

import org.lwjgl.opengl.GL11;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.TailsPoseStack;

public final class TailsPoseStackImpl implements TailsPoseStack, TailsPoseStack.Entry {

	public static final TailsPoseStack INSTANCE = new TailsPoseStackImpl();

	private TailsPoseStackImpl() {}

	@Override
	public void t$push() {
		GL11.glPushMatrix();
	}

	@Override
	public void t$pop() {
		GL11.glPopMatrix();
	}

	@Override
	public void t$translate(double x, double y, double z) {
		GL11.glTranslated(x, y, z);
	}

	@Override
	public void t$translate(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}

	@Override
	public void t$rotateX(float radians) {
		GL11.glRotatef(radians * TailsMath.RAD_TO_DEG, 1, 0, 0);
	}

	@Override
	public void t$rotateY(float radians) {
		GL11.glRotatef(radians * TailsMath.RAD_TO_DEG, 0, 1, 0);
	}

	@Override
	public void t$rotateZ(float radians) {
		GL11.glRotatef(radians * TailsMath.RAD_TO_DEG, 0, 0, 1);
	}

	@Override
	public void t$scale(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	@Override
	public Entry t$lastEntry() {
		return this;
	}
}