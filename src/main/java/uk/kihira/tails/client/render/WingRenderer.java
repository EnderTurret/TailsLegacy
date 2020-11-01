/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.common.PartInfo;

public class WingRenderer extends PartRenderer {

	public WingRenderer(String name, int subTypes, String modelAuthor, PartModel modelPart, String... textureNames) {
		super(name, subTypes, modelPart, modelAuthor, textureNames);
	}

	@Override
	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IRenderTypeBuffer bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn) {
		//Minecraft.getInstance().getTextureManager().bindTexture(info.getTexture());
		final IVertexBuilder renderer = bufferIn.getBuffer(RenderStates.getWings(info.getTexture()));
		//BufferBuilder renderer = Tessellator.getInstance().getBuffer();
		final boolean isFlying = entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.isFlying && entity.isAirBorne || entity.fallDistance > 0F;
		final float timestep = PartModel.getAnimationTime(isFlying ? 500 : 6500, entity);
		final float angle = MathHelper.sin(timestep) * (isFlying ? 24F : 4F);
		final float scale = info.subid == 1 ? 1F : 2F;

		matrixStack.push();

		matrixStack.translate(0, -(scale * 8F) * PartModel.SCALE + (info.subid == 1 ? 0.1F : 0), 0.1F);
		matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
		matrixStack.scale(scale, scale, scale);
		matrixStack.translate(0.1F, -0.4F * PartModel.SCALE, -0.025F);

		matrixStack.push();
		matrixStack.translate(0F, 0F, 1F * PartModel.SCALE);
		matrixStack.rotate(Vector3f.XP.rotationDegrees(30F - angle));
		//renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(matrixStack.getLast().getMatrix(), 0, 1, 0).tex(0, 0).endVertex();
		renderer.pos(matrixStack.getLast().getMatrix(), 1, 1, 0).tex(1, 0).endVertex();
		renderer.pos(matrixStack.getLast().getMatrix(), 1, 0, 0).tex(1, 1).endVertex();
		renderer.pos(matrixStack.getLast().getMatrix(), 0, 0, 0).tex(0, 1).endVertex();
		//Tessellator.getInstance().draw();
		matrixStack.pop();

		matrixStack.push();
		matrixStack.translate(0F, 0.3F * PartModel.SCALE, 0F);
		matrixStack.rotate(Vector3f.XP.rotationDegrees(-30F + angle));
		//renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(matrixStack.getLast().getMatrix(), 0, 1, 0).tex(0, 0).endVertex();
		renderer.pos(matrixStack.getLast().getMatrix(), 1, 1, 0).tex(1, 0).endVertex();
		renderer.pos(matrixStack.getLast().getMatrix(), 1, 0, 0).tex(1, 1).endVertex();
		renderer.pos(matrixStack.getLast().getMatrix(), 0, 0, 0).tex(0, 1).endVertex();
		//Tessellator.getInstance().draw();
		matrixStack.pop();
		matrixStack.pop();
	}
}
