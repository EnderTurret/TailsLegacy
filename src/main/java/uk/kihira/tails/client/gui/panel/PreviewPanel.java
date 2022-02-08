/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.IconButton;

public class PreviewPanel extends Panel<EditorScreen> {

	private float yaw = 0F;
	private float pitch = 10F;
	private double prevMouseX = -1;
	private double prevMouseY = -1;
	private boolean doRender;

	public PreviewPanel(EditorScreen parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
	}

	@Override
	public void init() {
		doRender = Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
		if (!doRender)
			return;
		// Reset Camera
		addRenderableWidget(new IconButton(right - left - 18, 22, IconButton.Icons.UNDO, b -> {
			yaw = 0;
			pitch = 10F;
		}, new TranslatableComponent("tails.gui.button.reset.camera")));
		// Help
		addRenderableWidget(new IconButton(right - left - 18, 4, IconButton.Icons.QUESTION, b -> {}, new TranslatableComponent("tails.gui.button.help.camera.0"), new TranslatableComponent("tails.gui.button.help.camera.1")));
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!doRender)
			return;
		setBlitOffset(-900);
		// Background
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xFF000000, 0xFF000000);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		setBlitOffset(0);

		// Player
		drawEntity(left + width / 2, top + height / 2 + Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4,
				Minecraft.getInstance().getWindow().getGuiScaledHeight() / 4,
				yaw, pitch, partialTicks, Minecraft.getInstance().player);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (button == 0) {
			// Yaw
			if (prevMouseX == -1) prevMouseX = mouseX;
			else {
				yaw += (mouseX - prevMouseX) * 1.5F;
				prevMouseX = mouseX;
			}
			// Pitch
			if (prevMouseY == -1) prevMouseY = mouseY;
			else {
				pitch += (mouseY - prevMouseY) * 0.1F;
				pitch = Mth.clamp(pitch, 6, 10);
				prevMouseY = mouseY;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		prevMouseX = -1;
		prevMouseY = -1;
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@SuppressWarnings("deprecation")
	private static void drawEntity(int x, int y, int scale, float yaw, float pitch, float partialTicks, LivingEntity entity) {
		final PoseStack matrixStack = RenderSystem.getModelViewStack();

		matrixStack.pushPose();

		matrixStack.translate(x, y, 1050);
		matrixStack.scale(1, 1, -1);

		RenderSystem.applyModelViewMatrix();

		final PoseStack pose2 = new PoseStack();
		pose2.translate(0, 0, 1000);
		pose2.scale(scale, scale, scale);

		final Quaternion quaternion = Vector3f.ZP.rotationDegrees(180f);
		final Quaternion quaternion1 = Vector3f.XP.rotationDegrees(pitch * 20F);
		quaternion.mul(quaternion1);

		pose2.mulPose(quaternion);
		pose2.mulPose(Vector3f.ZP.rotationDegrees(180));
		pose2.mulPose(Vector3f.YP.rotationDegrees(yaw));

		final float oldYBodyRot = entity.yBodyRot;
		final float oldYRot = entity.getYRot();
		final float oldXRot = entity.getXRot();
		final float oldYHeadRot = entity.yHeadRot;
		final float oldYHeadRotO = entity.yHeadRotO;

		entity.yBodyRot = 0;
		entity.setYRot(0);
		entity.setXRot(0);
		entity.yHeadRot = 0;
		entity.yHeadRotO = 0;
		entity.setShiftKeyDown(false);

		Lighting.setupForEntityInInventory();

		final EntityRenderDispatcher rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();

		quaternion1.conj();

		rendererManager.overrideCameraOrientation(quaternion1);
		rendererManager.setRenderShadow(false);

		final MultiBufferSource.BufferSource impl = Minecraft.getInstance().renderBuffers().bufferSource();

		RenderSystem.runAsFancy(() -> {
			rendererManager.render(entity, 0, 0, 0, 0F, 1F, pose2, impl, 15728880);
		});

		impl.endBatch();

		rendererManager.setRenderShadow(true);

		entity.yBodyRot = oldYBodyRot;
		entity.setYRot(oldYRot);
		entity.setXRot(oldXRot);
		entity.yHeadRot = oldYHeadRot;
		entity.yHeadRotO = oldYHeadRotO;

		matrixStack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}
}
