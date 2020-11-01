package uk.kihira.tails.client.render;

import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;

@OnlyIn(Dist.CLIENT)
public class PartLayer extends LayerRenderer<AbstractClientPlayerEntity,PlayerModel<AbstractClientPlayerEntity>> {

	private final ModelRenderer modelRenderer;
	private final PartsData.PartType partType;
	private final boolean mpmCompat;

	public PartLayer(IEntityRenderer<AbstractClientPlayerEntity,PlayerModel<AbstractClientPlayerEntity>> renderer, ModelRenderer modelRenderer, PartsData.PartType partType) {
		super(renderer);
		this.modelRenderer = modelRenderer;
		this.partType = partType;
		mpmCompat = ModList.get().isLoaded("moreplayermodels");
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		final UUID uuid = PlayerEntity.getUUID(entity.getGameProfile());
		if (Tails.PROXY.hasPartsData(uuid)) {
			final PartsData partsData = Tails.PROXY.getPartsData(uuid);
			if (partsData.hasPartInfo(partType)) {
				final PartInfo tailInfo = partsData.getPartInfo(partType);

				matrixStackIn.push();

				if (partType == PartsData.PartType.EARS || partType == PartsData.PartType.MUZZLE)
					getEntityModel().bipedHead.translateRotate(matrixStackIn);

				PartRegistry.getRenderPart(tailInfo.partType, tailInfo.typeid).render(matrixStackIn, entity, tailInfo, bufferIn, 0, 0, 0, partialTicks, packedLightIn, OverlayTexture.NO_OVERLAY);
				matrixStackIn.pop();
			}
		}
	}
}
