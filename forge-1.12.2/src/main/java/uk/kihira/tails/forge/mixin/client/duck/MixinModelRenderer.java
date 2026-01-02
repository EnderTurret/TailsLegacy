/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.mixin.client.duck;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

import uk.kihira.tails.common.JavaColor;
import uk.kihira.tails.common.client.duck.TailsBuffer;
import uk.kihira.tails.common.client.duck.TailsModelPart;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.duck.TailsRandomSource;
import uk.kihira.tails.forge.client.render.ModelPartExtensions;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer implements TailsModelPart, ModelPartExtensions {

	@Shadow
	@Final
	private List<ModelRenderer> childModels;

	@Unique
	private Map<String, ModelRenderer> tails$children;

	@Unique
	private float[] tails$initialPose;

	@Override
	public void tails$storeInitialPose() {
		final ModelRenderer self = (ModelRenderer) (Object) this;
		tails$initialPose = new float[] { self.offsetX, self.offsetY, self.offsetZ, self.rotateAngleX, self.rotateAngleY, self.rotateAngleZ };
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
		final float[] pose = tails$initialPose;
		return pose != null && !(pose[0] == 0 && pose[1] == 0 && pose[2] == 0 && pose[3] == 0 && pose[4] == 0 && pose[5] == 0);
	}

	@Override
	public float t$getInitialXRot() {
		return tails$initialPose[3];
	}

	@Override
	public float t$getInitialYRot() {
		return tails$initialPose[4];
	}

	@Override
	public float t$getInitialZRot() {
		return tails$initialPose[5];
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
		if (tails$children == null) {
			if (childModels == null || childModels.isEmpty())
				tails$children = Collections.emptyMap();
			else {
				tails$children = new LinkedHashMap<>();
				for (ModelRenderer renderer : childModels) {
					if (renderer.boxName == null) throw new IllegalArgumentException("ModelRenderer " + renderer + " has no name");
					tails$children.put(renderer.boxName, renderer);
				}
			}
		}

		return (Map) tails$children;
	}

	@Override
	public CubePose t$getRandomCube(TailsRandomSource random) {
		final ModelRenderer self = (ModelRenderer) (Object) this;
		final ModelBox cube = self.cubeList.get(((Random) random.t$unwrap()).nextInt(self.cubeList.size()));
		return new CubePose(cube.posX1, cube.posY1, cube.posZ1, cube.posX2, cube.posY2, cube.posZ2);
	}

	@Override
	public void t$render(TailsPoseStack pose, TailsBuffer buffer, int packedLight, int packedOverlay, int color) {
		GlStateManager.color(JavaColor.red(color) / 255F, JavaColor.green(color) / 255F, JavaColor.blue(color) / 255F, JavaColor.alpha(color) / 255F);
		((ModelRenderer) (Object) this).render(0.0625F);
		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	public void t$translateAndRotate(TailsPoseStack poseStack) {
		final ModelRenderer part = (ModelRenderer) (Object) this;
		GlStateManager.translate(part.offsetX, part.offsetY, part.offsetZ);
		part.postRender(0.0625F);
	}
}