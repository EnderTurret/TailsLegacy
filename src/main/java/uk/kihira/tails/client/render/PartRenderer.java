/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.PartInfo;

@OnlyIn(Dist.CLIENT)
public class PartRenderer {

	private static final Map<Class<? extends LivingEntity>, IRenderHelper> RENDER_HELPERS = new HashMap<>();

	protected final String name;
	protected final String[] textureNames;
	protected final int subTypes;
	protected final String[][] authors;
	protected final String modelAuthor;
	public final PartModel modelPart;

	public PartRenderer(String name, int subTypes, PartModel modelPart, @Nullable String modelAuthor, String... textureNames) {
		this.name = name;
		this.subTypes = subTypes;
		this.modelAuthor = modelAuthor;
		this.modelPart = modelPart;
		this.textureNames = textureNames;
		authors = new String[subTypes + 1][textureNames.length];
	}

	public void preRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, double x, double y, double z, float partialTicks) {
		if (info.needsTextureCompile || info.getTexture() == null) {
			info.setTexture(TextureHelper.generateTexture(entity.getUniqueID(), info));
			info.needsTextureCompile = false;
		}

		IRenderHelper helper;
		// Support for Galacticraft as it adds its own EntityPlayer.
		if (entity instanceof PlayerEntity) helper = getRenderHelper(PlayerEntity.class);
		else helper = getRenderHelper(entity.getClass());
		if (helper != null)
			helper.onPreRenderTail(matrixStack, entity, this, info, x, y, z);

		if (modelPart != null) {
			modelPart.setRotationAngles(entity, entity.limbSwing, entity.limbSwingAmount, partialTicks, info.subid, entity.rotationPitch);
			modelPart.setLivingAnimations(entity, entity.limbSwing, entity.limbSwingAmount, partialTicks);
		}
	}

	public void render(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IRenderTypeBuffer bufferIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn) {
		if (modelPart != null) {
			matrixStack.push();

			preRender(matrixStack, entity, info, x, y, z, partialTicks);

			doRender(matrixStack, entity, info, bufferIn, partialTicks, packedLightIn, packedOverlayIn);

			matrixStack.pop();
		}
	}

	public void render(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IVertexBuilder bufferIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn) {
		if (modelPart != null) {
			matrixStack.push();

			preRender(matrixStack, entity, info, x, y, z, partialTicks);

			doRender(matrixStack, entity, info, bufferIn, partialTicks, packedLightIn, packedOverlayIn);

			matrixStack.pop();
		}
	}

	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IRenderTypeBuffer bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn) {
		final RenderType type = RenderType.getEntityCutoutNoCull(info.getTexture());
		final IVertexBuilder buf = bufferIn.getBuffer(type);

		doRender(matrixStack, entity, info, buf, partialTicks, packedLightIn, packedOverlayIn);
	}

	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IVertexBuilder bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn) {
		modelPart.render(matrixStack, bufferIn, entity, packedLightIn, packedOverlayIn, 1F, 1F, 1F, 1F, info.subid, partialTicks);
	}

	/**
	 * Gets the available textures for this tail subid.
	 * By default, this provides {@link #textureNames} for all subid's but you can override for finer control
	 * @return Available textures
	 * @param subid The subid
	 */
	public String[] getTextureNames(int subid) {
		return textureNames;
	}

	/**
	 * Gets the available subtypes for this part.
	 * @return The subtypes.
	 */
	public int getAvailableSubTypes() {
		return subTypes;
	}

	public String getUnlocalisedName(int subType) {
		return name + "." + subType + ".name";
	}

	public PartRenderer setAuthor(String author, int subType, int textureID) {
		authors[subType][textureID] = author;

		return this;
	}

	public PartRenderer setAuthor(String author, int subType) {
		for (int i = 0; i < getTextureNames(subType).length; i++)
			setAuthor(author, subType, i);

		return this;
	}

	public PartRenderer setAuthor(String author) {
		for (int i = 0; i <= subTypes; i++)
			setAuthor(author, i);

		return this;
	}

	public String getModelAuthor() {
		return modelAuthor;
	}

	public String getAuthor(int subType, int textureID) {
		return authors[subType][textureID];
	}

	public boolean hasAuthor(int subType, int textureID) {
		return getAuthor(subType, textureID) != null;
	}

	public static void registerRenderHelper(Class<? extends LivingEntity> clazz, IRenderHelper helper) {
		if (helper != null && !RENDER_HELPERS.containsKey(clazz))
			RENDER_HELPERS.put(clazz, helper);
		else
			throw new IllegalArgumentException("An invalid RenderHelper was registered!");
	}

	public static IRenderHelper getRenderHelper(Class<? extends LivingEntity> clazz) {
		return RENDER_HELPERS.getOrDefault(clazz, null);
	}
}
