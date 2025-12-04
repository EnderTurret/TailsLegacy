/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.mixin.client.duck;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.RandomSource;

import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsRandomSource;

@Mixin(ModelPart.class)
public class MixinModelPart implements TailsModelPart {

	@Shadow
	@Final
	private Map<String, ModelPart> children;

	@Override
	public boolean t$isVisible() {
		return ((ModelPart) (Object) this).visible;
	}

	@Override
	public void t$setVisible(boolean value) {
		((ModelPart) (Object) this).visible = value;
	}

	@Override
	public float t$getXRot() {
		return ((ModelPart) (Object) this).xRot;
	}

	@Override
	public void t$setXRot(float value) {
		((ModelPart) (Object) this).xRot = value;
	}

	@Override
	public float t$getYRot() {
		return ((ModelPart) (Object) this).yRot;
	}

	@Override
	public void t$setYRot(float value) {
		((ModelPart) (Object) this).yRot = value;
	}

	@Override
	public float t$getZRot() {
		return ((ModelPart) (Object) this).zRot;
	}

	@Override
	public void t$setZRot(float value) {
		((ModelPart) (Object) this).zRot = value;
	}

	@Override
	public boolean t$hasInitialPose() {
		final PartPose pose = ((ModelPart) (Object) this).getInitialPose();
		return !(pose.x == 0 && pose.y == 0 && pose.z == 0 && pose.xRot == 0 && pose.yRot == 0 && pose.zRot == 0);
	}

	@Override
	public float t$getInitialXRot() {
		return ((ModelPart) (Object) this).getInitialPose().xRot;
	}

	@Override
	public float t$getInitialYRot() {
		return ((ModelPart) (Object) this).getInitialPose().yRot;
	}

	@Override
	public float t$getInitialZRot() {
		return ((ModelPart) (Object) this).getInitialPose().zRot;
	}

	@Override
	public boolean t$isEmpty() {
		return ((ModelPart) (Object) this).isEmpty();
	}

	@Override
	public TailsModelPart t$getChild(String name) {
		return (TailsModelPart) (Object) ((ModelPart) (Object) this).getChild(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, TailsModelPart> t$getChildren() {
		return (Map) children;
	}

	@Override
	public CubePose t$getRandomCube(TailsRandomSource random) {
		final ModelPart.Cube cube = ((ModelPart) (Object) this).getRandomCube((RandomSource) random.t$unwrap());
		return new CubePose(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ);
	}

	@Override
	public void t$render(TailsPoseStack pose, TailsBuffer buffer, int packedLight, int packedOverlay, int color) {
		((ModelPart) (Object) this).render((PoseStack) pose, (VertexConsumer) buffer, packedLight, packedOverlay, color);
	}

	@Override
	public void t$translateAndRotate(TailsPoseStack poseStack) {
		((ModelPart) (Object) this).translateAndRotate((PoseStack) poseStack);
	}
}