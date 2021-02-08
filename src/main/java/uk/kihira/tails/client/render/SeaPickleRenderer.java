package uk.kihira.tails.client.render;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.lwjgl.system.MemoryStack;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;

public class SeaPickleRenderer extends PartRenderer {

	public SeaPickleRenderer(String name, int subTypes, PartModel modelPart, String modelAuthor, String... textureNames) {
		super(name, subTypes, new Model(), modelAuthor, textureNames);
	}

	@Override
	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IVertexBuilder bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		final int tint = info.getTints()[0];
		final float r = (tint >> 16 & 255) / 255F;
		final float g = (tint >> 8 & 255) / 255F;
		final float b = (tint & 255) / 255F;
		super.doRender(matrixStack, entity, info, bufferIn, partialTicks, packedLightIn, packedOverlayIn, r, g, b, alpha);
	}

	@Override
	public void compileTextureIfNeeded(LivingEntity entity, PartInfo info) {
		info.setTexture(new ResourceLocation("tails", "texture/ears/sea_pickle.png"));
	}

	@Override
	public PartInfo makeDefaultPartInfo(int type, int subType, PartType partType) {
		// Red sea pickles are kind of weird, which is why we're overriding this to make all tints white.
		return new PartInfo(type, subType, 0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, partType, null);
	}

	public static class Model extends PartModel {

		private ModelRenderer model;

		public Model() {
			textureWidth = 32;
			textureHeight = 32;

			model = new ModelRenderer(this);
			model.setRotationPoint(0F, 18.2875F, 0F);
			model.setTextureOffset(0, 1).addBox(-2F, -0.2875F, -2F, 4F, 6F, 4F, 0F, false);
			model.setTextureOffset(0, 11).addBox(-2F, -0.2375F, -2F, 4F, 0F, 4F, 0F, false);

			final ModelRenderer cube = new ModelRenderer(this);
			cube.setRotationPoint(0F, -2.2875F, 0F);
			model.addChild(cube);
			cube.rotateAngleY = -0.7854F;
			cube.setTextureOffset(1, 1).addBox(0F, -0.7F, -0.5F, 0F, 3F, 1F, 0F, false);
			cube.setTextureOffset(0, 2).addBox(-0.5F, -0.7F, 0F, 1F, 3F, 0F, 0F, false);
		}

		@Override
		public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, LivingEntity entity, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, int subtype, float partialTicks) {
			model = new ModelRenderer(this);
			model.setRotationPoint(0F, 18.2875F, 0F);
			model.setTextureOffset(0, 1).addBox(-2F, -0.2875F, -2F, 4F, 6F, 4F, 0F, false);
			model.setTextureOffset(0, 11).addBox(-2F, -0.2375F, -2F, 4F, 0F, 4F, 0F, false);

			matrixStackIn.push();

			matrixStackIn.translate(0, -2, 0);

			model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			matrixStackIn.pop();
		}
	}
}