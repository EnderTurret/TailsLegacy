/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client.duck;

import java.util.Map;
import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

import net.enderturret.tailslegacy.common.JavaColor;
import net.enderturret.tailslegacy.common.client.duck.TailsBuffer;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.duck.TailsRandomSource;
import net.enderturret.tailslegacy.forge.client.render.ModelPartExtensions;

@Mixin(ModelPart.class)
public class MixinModelPart implements TailsModelPart, ModelPartExtensions {

	@Shadow
	@Final
	private Map<String, ModelPart> children;

	@Unique
	private PartPose tailslegacy$initialPose;

	@Override
	public void tailslegacy$storeInitialPose() {
		tailslegacy$initialPose = ((ModelPart) (Object) this).storePose();
	}

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
		final PartPose pose = tailslegacy$initialPose;
		return pose != null && !(pose.x == 0 && pose.y == 0 && pose.z == 0 && pose.xRot == 0 && pose.yRot == 0 && pose.zRot == 0);
	}

	@Override
	public float t$getInitialXRot() {
		return tailslegacy$initialPose.xRot;
	}

	@Override
	public float t$getInitialYRot() {
		return tailslegacy$initialPose.yRot;
	}

	@Override
	public float t$getInitialZRot() {
		return tailslegacy$initialPose.zRot;
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
		final ModelPart.Cube cube = ((ModelPart) (Object) this).getRandomCube((Random) random.t$unwrap());
		return new CubePose(cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ);
	}

	@Override
	public void t$render(TailsPoseStack pose, TailsBuffer buffer, int packedLight, int packedOverlay, int color) {
		((ModelPart) (Object) this).render((PoseStack) pose, (VertexConsumer) buffer, packedLight, packedOverlay,
				JavaColor.red(color) / 255F, JavaColor.green(color) / 255F, JavaColor.blue(color) / 255F, JavaColor.alpha(color) / 255F);
	}

	@Override
	public void t$translateAndRotate(TailsPoseStack poseStack) {
		((ModelPart) (Object) this).translateAndRotate((PoseStack) poseStack);
	}
}