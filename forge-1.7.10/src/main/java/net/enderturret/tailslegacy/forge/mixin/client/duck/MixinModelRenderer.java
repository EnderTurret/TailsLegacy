/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.mixin.client.duck;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

import net.enderturret.tailslegacy.common.JavaColor;
import net.enderturret.tailslegacy.common.client.duck.TailsBuffer;
import net.enderturret.tailslegacy.common.client.duck.TailsModelPart;
import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;
import net.enderturret.tailslegacy.common.client.duck.TailsRandomSource;
import net.enderturret.tailslegacy.forge.client.ClientEventHandler;
import net.enderturret.tailslegacy.forge.client.render.ModelPartExtensions;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer implements TailsModelPart, ModelPartExtensions {

	@Shadow
	@Final
	private List<ModelRenderer> childModels;

	@Unique
	private Map<String, ModelRenderer> tailslegacy$children;

	@Unique
	private float[] tailslegacy$initialPose;

	@Override
	public void tailslegacy$storeInitialPose() {
		final ModelRenderer self = (ModelRenderer) (Object) this;
		tailslegacy$initialPose = new float[] { self.offsetX, self.offsetY, self.offsetZ, self.rotateAngleX, self.rotateAngleY, self.rotateAngleZ };
	}

	@Override
	public boolean t$isVisible() {
		return ((ModelRenderer) (Object) this).showModel && !((ModelRenderer) (Object) this).isHidden;
	}

	@Override
	public void t$setVisible(boolean value) {
		((ModelRenderer) (Object) this).showModel = value;
	}

	@Override
	public float t$getXRot() {
		return ((ModelRenderer) (Object) this).rotateAngleX;
	}

	@Override
	public void t$setXRot(float value) {
		((ModelRenderer) (Object) this).rotateAngleX = value;
	}

	@Override
	public float t$getYRot() {
		return ((ModelRenderer) (Object) this).rotateAngleY;
	}

	@Override
	public void t$setYRot(float value) {
		((ModelRenderer) (Object) this).rotateAngleY = value;
	}

	@Override
	public float t$getZRot() {
		return ((ModelRenderer) (Object) this).rotateAngleZ;
	}

	@Override
	public void t$setZRot(float value) {
		((ModelRenderer) (Object) this).rotateAngleZ = value;
	}

	@Override
	public boolean t$hasInitialPose() {
		final float[] pose = tailslegacy$initialPose;
		return pose != null && !(pose[0] == 0 && pose[1] == 0 && pose[2] == 0 && pose[3] == 0 && pose[4] == 0 && pose[5] == 0);
	}

	@Override
	public float t$getInitialXRot() {
		return tailslegacy$initialPose[3];
	}

	@Override
	public float t$getInitialYRot() {
		return tailslegacy$initialPose[4];
	}

	@Override
	public float t$getInitialZRot() {
		return tailslegacy$initialPose[5];
	}

	@Override
	public boolean t$isEmpty() {
		return ((ModelRenderer) (Object) this).cubeList.isEmpty();
	}

	@Override
	public TailsModelPart t$getChild(String name) {
		return t$getChildren().get(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, TailsModelPart> t$getChildren() {
		if (tailslegacy$children == null) {
			if (childModels == null || childModels.isEmpty())
				tailslegacy$children = Collections.emptyMap();
			else {
				tailslegacy$children = new LinkedHashMap<>();
				for (ModelRenderer renderer : childModels) {
					if (renderer.boxName == null) throw new IllegalArgumentException("ModelRenderer " + renderer + " has no name");
					tailslegacy$children.put(renderer.boxName, renderer);
				}
			}
		}

		return (Map) tailslegacy$children;
	}

	@Override
	public CubePose t$getRandomCube(TailsRandomSource random) {
		final ModelRenderer self = (ModelRenderer) (Object) this;
		final ModelBox cube = (ModelBox) self.cubeList.get(((Random) random.t$unwrap()).nextInt(self.cubeList.size()));
		return new CubePose(cube.posX1, cube.posY1, cube.posZ1, cube.posX2, cube.posY2, cube.posZ2);
	}

	@Override
	public void t$render(TailsPoseStack pose, TailsBuffer buffer, int packedLight, int packedOverlay, int color) {
		float[] oldColors = null;
		if (color != 0xFFFFFFFF) {
			oldColors = ClientEventHandler.captureCurrentColor();
			GL11.glColor4f(JavaColor.red(color) / 255F * oldColors[0], JavaColor.green(color) / 255F * oldColors[1], JavaColor.blue(color) / 255F * oldColors[2], JavaColor.alpha(color) / 255F * oldColors[3]);
		}

		((ModelRenderer) (Object) this).render(0.0625F);

		if (oldColors != null) GL11.glColor4f(oldColors[0], oldColors[1], oldColors[2], oldColors[3]);
	}

	@Override
	public void t$translateAndRotate(TailsPoseStack poseStack) {
		final ModelRenderer part = (ModelRenderer) (Object) this;
		GL11.glTranslatef(part.offsetX, part.offsetY, part.offsetZ);
		part.postRender(0.0625F);
	}
}