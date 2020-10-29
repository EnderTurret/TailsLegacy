package uk.kihira.tails.client.model.ears;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import uk.kihira.tails.client.model.ModelPartBase;

public class ModelCatSmallEars extends ModelPartBase {
    public ModelRenderer leftEarBottom;
    public ModelRenderer leftEarRearLayer1;
    public ModelRenderer leftEarRearBottom;
    public ModelRenderer leftEarLayer1;
    public ModelRenderer leftEarLayer3;
    public ModelRenderer leftEarLayer2;
    public ModelRenderer rightEarBottom;
    public ModelRenderer rightEarLayer1;
    public ModelRenderer rightEarRearLayer1;
    public ModelRenderer rightEarRearBottom;
    public ModelRenderer rightEarLayer2;
    public ModelRenderer rightEarLayer3;

    public ModelCatSmallEars() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.rightEarRearLayer1 = new ModelRenderer(this, 13, 14);
        this.rightEarRearLayer1.setRotationPoint(-3.0F, -8.0F, 0.0F);
        this.rightEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
        this.leftEarRearLayer1 = new ModelRenderer(this, 0, 14);
        this.leftEarRearLayer1.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarRearLayer1.addBox(-1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F);
        this.rightEarRearBottom = new ModelRenderer(this, 13, 12);
        this.rightEarRearBottom.setRotationPoint(-3.0F, -8.0F, 0.0F);
        this.rightEarRearBottom.addBox(-1.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
        this.leftEarLayer1 = new ModelRenderer(this, 0, 2);
        this.leftEarLayer1.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarLayer1.addBox(-3.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
        this.leftEarLayer2 = new ModelRenderer(this, 0, 4);
        this.leftEarLayer2.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
        this.rightEarLayer1 = new ModelRenderer(this, 13, 2);
        this.rightEarLayer1.setRotationPoint(-3.0F, -8.0F, 0.0F);
        this.rightEarLayer1.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 1, 0.0F);
        this.rightEarBottom = new ModelRenderer(this, 13, 0);
        this.rightEarBottom.setRotationPoint(-4.0F, -8.0F, 0.0F);
        this.rightEarBottom.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.leftEarRearBottom = new ModelRenderer(this, 0, 12);
        this.leftEarRearBottom.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarRearBottom.addBox(-2.0F, -1.0F, 1.0F, 2, 1, 1, 0.0F);
        this.rightEarLayer3 = new ModelRenderer(this, 13, 6);
        this.rightEarLayer3.setRotationPoint(-2.0F, -8.0F, 0.0F);
        this.rightEarLayer3.addBox(-2.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
        this.leftEarBottom = new ModelRenderer(this, 0, 0);
        this.leftEarBottom.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarBottom.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.rightEarLayer2 = new ModelRenderer(this, 13, 4);
        this.rightEarLayer2.setRotationPoint(-3.0F, -8.0F, 0.0F);
        this.rightEarLayer2.addBox(-2.0F, -2.0F, 0.0F, 3, 1, 1, 0.0F);
        this.leftEarLayer3 = new ModelRenderer(this, 0, 6);
        this.leftEarLayer3.setRotationPoint(4.0F, -8.0F, 0.0F);
        this.leftEarLayer3.addBox(-1.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
        this.rightEarRearLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leftEarRearLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.rightEarRearBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leftEarLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leftEarLayer2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.rightEarLayer1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.rightEarBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leftEarRearBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.rightEarLayer3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leftEarBottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.rightEarLayer2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.leftEarLayer3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
