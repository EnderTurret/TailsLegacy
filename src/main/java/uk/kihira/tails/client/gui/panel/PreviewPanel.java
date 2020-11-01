package uk.kihira.tails.client.gui.panel;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.IconButton;

public class PreviewPanel extends Panel<EditorScreen> {

	private float yaw = 0F;
	private float pitch = 10F;
	private double prevMouseX = -1;
	private boolean doRender;

	public PreviewPanel(EditorScreen parent, int left, int top, int right, int bottom) {
		super(parent, left, top, right, bottom);
	}

	@Override
	public void init() {
		doRender = Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.FIRST_PERSON;
		if (!doRender)
			return;
		// Reset Camera
		addButton(new IconButton(right - left - 18, 22, IconButton.Icons.UNDO, b -> {
			yaw = 0;
			pitch = 10F;
		}, new TranslationTextComponent("gui.button.reset.camera")));
		// Help
		addButton(new IconButton(right - left - 18, 4, IconButton.Icons.QUESTION, b -> {}, new TranslationTextComponent("gui.button.help.camera.0"), new TranslationTextComponent("gui.button.help.camera.1")));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!doRender)
			return;
		setBlitOffset(-1000);
		// Background
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xFF000000, 0xFF000000);

		RenderSystem.color4f(1F, 1F, 1F, 1F);
		setBlitOffset(0);

		// Player
		drawEntity(width / 2, height / 2 + Minecraft.getInstance().getMainWindow().getScaledHeight() / 4,
				Minecraft.getInstance().getMainWindow().getScaledHeight() / 4,
				yaw, pitch, partialTicks, Minecraft.getInstance().player);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (button == 0)
			// Yaw
			if (prevMouseX == -1) prevMouseX = mouseX;
			else {
				yaw += (mouseX - prevMouseX) * 1.5F;
				prevMouseX = mouseX;
			}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		prevMouseX = -1;
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@SuppressWarnings("deprecation")
	private static void drawEntity(int x, int y, int scale, float yaw, float pitch, float partialTicks, LivingEntity entity) {
		RenderSystem.pushMatrix();
		RenderSystem.translatef(x, y, 100F);
		RenderSystem.scalef(1F, 1F, -1F);

		final MatrixStack matrixStack = new MatrixStack();

		matrixStack.translate(0, 0, 1000);
		matrixStack.scale(scale, scale, scale);

		final Quaternion quaternion = Vector3f.ZP.rotationDegrees(180f);
		final Quaternion quaternion1 = Vector3f.XP.rotationDegrees(pitch * 20F);

		quaternion.multiply(quaternion1);
		matrixStack.rotate(quaternion);
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));
		matrixStack.rotate(Vector3f.YP.rotationDegrees(yaw));

		final float oldRotationYawHead = entity.rotationYawHead;
		final float oldRotationYaw = entity.rotationYaw;
		final float oldRotationPitch = entity.rotationPitch;

		entity.rotationYawHead = 0F;
		entity.rotationYaw = 0F;
		entity.rotationPitch = 0F;
		entity.renderYawOffset = 0F;
		entity.setSneaking(false);

		final EntityRendererManager rendererManager = Minecraft.getInstance().getRenderManager();

		quaternion1.conjugate();

		rendererManager.setCameraOrientation(quaternion1);
		rendererManager.setRenderShadow(false);

		final IRenderTypeBuffer.Impl impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

		RenderSystem.runAsFancy(() -> {
			rendererManager.renderEntityStatic(entity, 0, 0, 0, 0f, 1F, matrixStack, impl, 15728880);
		});

		impl.finish();

		rendererManager.setRenderShadow(true);

		entity.rotationYawHead = oldRotationYawHead;
		entity.rotationYaw = oldRotationYaw;
		entity.rotationPitch = oldRotationPitch;

		RenderSystem.popMatrix();
	}
}
