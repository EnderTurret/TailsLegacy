package uk.kihira.tails.client.model.tail;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import uk.kihira.tails.client.model.ModelPartBase;

public class ModelBunnyTail extends ModelPartBase {
	private final ModelRenderer tailBase;

	public ModelBunnyTail() {
		this.tailBase = new ModelRenderer(this);

		this.tailBase.addBox(0.0F, 0.0F, 0.0F, 4, 3, 3, 0.0F);
		this.tailBase.setRotationPoint(-2.0F, -1.5F, 0.0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
		float timestep = getAnimationTime(4000F, entity);

		this.setRotationAngles(0, timestep, 1F, 1F, 0, 0, partialTicks, entity);

		this.tailBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	private void setRotationAngles(int subtype, float timestep, float yOffset, float xOffset, float xAngle, float yAngle, float partialTicks, Entity entity) {
		this.setRotationDegrees(this.tailBase, xAngle, yAngle, 0F);
	}
}
