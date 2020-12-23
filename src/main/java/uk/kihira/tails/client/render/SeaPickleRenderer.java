package uk.kihira.tails.client.render;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;

public class SeaPickleRenderer extends PartRenderer {

	public SeaPickleRenderer(String name, int subTypes, PartModel modelPart, String modelAuthor, String... textureNames) {
		super(name, subTypes, modelPart, modelAuthor, textureNames);
	}

	@Override
	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IVertexBuilder bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		final BlockState state = Blocks.SEA_PICKLE.getDefaultState();
		final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		final IRenderTypeBuffer buffers = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

		matrixStack.push();
		matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
		matrixStack.translate(-0.5, 0.5, -0.5);

		for (RenderType type : RenderType.getBlockRenderTypes())
			if (RenderTypeLookup.canRenderInLayer(state, type)) {
				ForgeHooksClient.setRenderLayer(type);
				renderModel(info, dispatcher.getBlockModelRenderer(), entity.world, dispatcher.getModelForState(state), state, entity.getPosition(), matrixStack, buffers.getBuffer(type), entity.getRNG(),
						state.getPositionRandom(BlockPos.ZERO), packedOverlayIn, EmptyModelData.INSTANCE);
			}
		ForgeHooksClient.setRenderLayer(null);

		matrixStack.pop();
	}

	@Override
	public PartInfo makeDefaultPartInfo(int type, int subType, PartType partType) {
		// Red sea pickles are kind of weird, which is why we're overriding this to make all tints white.
		return new PartInfo(type, subType, 0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, partType, null);
	}

	public void renderModel(PartInfo info, BlockModelRenderer bmr, IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer, Random randomIn, long rand, int combinedOverlayIn, IModelData modelData) {
		final Vector3d pos = stateIn.getOffset(worldIn, posIn);
		matrixIn.translate(pos.x, pos.y, pos.z);
		modelData = modelIn.getModelData(worldIn, posIn, stateIn, modelData);

		try {
			renderModelFlat(info, bmr, worldIn, modelIn, stateIn, posIn, matrixIn, buffer, randomIn, rand, combinedOverlayIn, modelData);
		} catch (Throwable e) {
			final CrashReport crash = CrashReport.makeCrashReport(e, "Tesselating block model");
			final CrashReportCategory category = crash.makeCategory("Block model being tesselated");
			CrashReportCategory.addBlockInfo(category, posIn, stateIn);
			throw new ReportedException(crash);
		}
	}

	public void renderModelFlat(PartInfo info, BlockModelRenderer bmr, IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, Random randomIn, long randSeed, int combinedOverlayIn, IModelData modelData) {
		final BitSet bitSet = new BitSet(3);

		for (Direction dir : Direction.values()) {
			randomIn.setSeed(randSeed);
			final List<BakedQuad> quads = modelIn.getQuads(stateIn, dir, randomIn, modelData);
			if (!quads.isEmpty() && Block.shouldSideBeRendered(stateIn, worldIn, posIn, dir)) {
				final int packedLight = WorldRenderer.getPackedLightmapCoords(worldIn, stateIn, posIn.offset(dir));
				renderQuadsFlat(info, bmr, worldIn, stateIn, posIn, packedLight, combinedOverlayIn, false, matrixStackIn, buffer, quads, bitSet);
			}
		}

		randomIn.setSeed(randSeed);
		final List<BakedQuad> quads = modelIn.getQuads(stateIn, (Direction)null, randomIn, modelData);
		if (!quads.isEmpty())
			renderQuadsFlat(info, bmr, worldIn, stateIn, posIn, -1, combinedOverlayIn, true, matrixStackIn, buffer, quads, bitSet);
	}

	private void renderQuadsFlat(PartInfo info, BlockModelRenderer bmr, IBlockDisplayReader worldIn, BlockState stateIn, BlockPos posIn, int brightnessIn, int combinedOverlayIn, boolean ownBrightness, MatrixStack matrixStackIn, IVertexBuilder buffer, List<BakedQuad> list, BitSet bitSet) {
		for (BakedQuad quad : list) {
			if (ownBrightness) {
				bmr.fillQuadBounds(worldIn, stateIn, posIn, quad.getVertexData(), quad.getFace(), (float[])null, bitSet);
				final BlockPos blockpos = bitSet.get(0) ? posIn.offset(quad.getFace()) : posIn;
				brightnessIn = WorldRenderer.getPackedLightmapCoords(worldIn, stateIn, blockpos);
			}

			final float colorMul = worldIn.func_230487_a_(quad.getFace(), quad.applyDiffuseLighting());
			renderQuadSmooth(info, worldIn, stateIn, posIn, buffer, matrixStackIn.getLast(), quad, colorMul, colorMul, colorMul, colorMul, brightnessIn, brightnessIn, brightnessIn, brightnessIn, combinedOverlayIn);
		}

	}

	private void renderQuadSmooth(PartInfo info, IBlockDisplayReader blockAccessIn, BlockState stateIn, BlockPos posIn, IVertexBuilder buffer, MatrixStack.Entry matrixEntry, BakedQuad quadIn, float colorMul0, float colorMul1, float colorMul2, float colorMul3, int brightness0, int brightness1, int brightness2, int brightness3, int combinedOverlayIn) {
		final int tint = info.getTints()[0];
		final float r = (tint >> 16 & 255) / 255F;
		final float g = (tint >> 8 & 255) / 255F;
		final float b = (tint & 255) / 255F;

		buffer.addQuad(matrixEntry, quadIn, new float[]{colorMul0, colorMul1, colorMul2, colorMul3}, r, g, b, new int[]{brightness0, brightness1, brightness2, brightness3}, combinedOverlayIn, true);
	}
}