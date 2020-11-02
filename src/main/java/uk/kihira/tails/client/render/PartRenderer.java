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
import uk.kihira.tails.common.part.PartInfo;

/**
 * A renderer for a part. It also keeps track of some metadata.<br>
 * You can register one in {@link PartRegistry#registerPart(PartType, PartRenderer)}.
 */
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

	/**
	 * A pre-render callback for translation, rotation, and making sure the texture exists.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity that is about to be used for rendering.
	 * @param info The {@link PartInfo} about to be rendered.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTicks The current partial tick value.
	 */
	public void preRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, double x, double y, double z, float partialTicks) {
		if (!info.isEmpty() && (info.needsTextureCompile || info.getTexture() == null)) {
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
			modelPart.setRotationAngles(entity, entity.limbSwing, entity.limbSwingAmount, partialTicks, info.getSubType(), entity.rotationPitch);
			modelPart.setLivingAnimations(entity, entity.limbSwing, entity.limbSwingAmount, partialTicks);
		}
	}

	/**
	 * Renders the given part on the given entity.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link PartInfo}.
	 * @param bufferIn The {@link IRenderTypeBuffer} to retrieve an {@link IVertexBuilder} from.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTicks The current partial ticks.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 */
	public void render(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IRenderTypeBuffer bufferIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn) {
		if (modelPart != null && !info.isEmpty()) {
			matrixStack.push();

			preRender(matrixStack, entity, info, x, y, z, partialTicks);

			doRender(matrixStack, entity, info, bufferIn, partialTicks, packedLightIn, packedOverlayIn);

			matrixStack.pop();
		}
	}

	/**
	 * Renders the given part on the given entity.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link PartInfo}.
	 * @param bufferIn The buffer to draw to.
	 * @param x The x location.
	 * @param y The y location.
	 * @param z The z location.
	 * @param partialTicks The current partial ticks.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 */
	public void render(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IVertexBuilder bufferIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn) {
		if (modelPart != null && !info.isEmpty()) {
			matrixStack.push();

			preRender(matrixStack, entity, info, x, y, z, partialTicks);

			doRender(matrixStack, entity, info, bufferIn, partialTicks, packedLightIn, packedOverlayIn);

			matrixStack.pop();
		}
	}

	/**
	 * Renders the given part on the given entity.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link PartInfo}.
	 * @param bufferIn The {@link IRenderTypeBuffer} to retrieve an {@link IVertexBuilder} from.
	 * @param partialTicks The current partial ticks.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 */
	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IRenderTypeBuffer bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn) {
		final RenderType type = RenderType.getEntityCutoutNoCull(info.getTexture());
		final IVertexBuilder buf = bufferIn.getBuffer(type);

		doRender(matrixStack, entity, info, buf, partialTicks, packedLightIn, packedOverlayIn);
	}

	/**
	 * Renders the given part on the given entity.
	 * @param matrixStack The {@link MatrixStack} to use for transformations.
	 * @param entity The entity the part is being rendered on.
	 * @param info The {@link PartInfo}.
	 * @param bufferIn The buffer to draw to.
	 * @param partialTicks The current partial ticks.
	 * @param packedLightIn The packed light.
	 * @param packedOverlayIn The packed overlay. Use {@link OverlayTexture#NO_OVERLAY} for no overlay.
	 */
	protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IVertexBuilder bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn) {
		modelPart.render(matrixStack, bufferIn, entity, packedLightIn, packedOverlayIn, 1F, 1F, 1F, 1F, info.getSubType(), partialTicks);
	}

	/**
	 * Returns the textures available for the given subtype.<br>
	 * This can be overriden for more control over texture names for a particular subtype.
	 * @param subid The part subtype.
	 * @return The available textures.
	 */
	public String[] getTextureNames(int subid) {
		return textureNames;
	}

	/**
	 * Returns the maximum subtype id.
	 * @return The subtype id.
	 */
	public int getAvailableSubTypes() {
		return subTypes;
	}

	/**
	 * Returns the translation key with the given subtype.
	 * @param subType The subtype.
	 * @return The translation key.
	 */
	public String getUnlocalisedName(int subType) {
		return name + "." + subType + ".name";
	}

	/**
	 * Sets the author for the given texture for the given subtype.
	 * @param author The author.
	 * @param subType The subtype.
	 * @param textureID The texture id.
	 * @return {@code this}.
	 */
	public PartRenderer setAuthor(String author, int subType, int textureID) {
		authors[subType][textureID] = author;

		return this;
	}

	/**
	 * Sets the author for all textures under the given subtype.
	 * @param author The author.
	 * @param subType The subtype.
	 * @return {@code this}.
	 */
	public PartRenderer setAuthor(String author, int subType) {
		for (int i = 0; i < getTextureNames(subType).length; i++)
			setAuthor(author, subType, i);

		return this;
	}

	/**
	 * Sets the author for all textures under all subtypes.
	 * @param author The author.
	 * @return {@code this}.
	 */
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
			throw new IllegalArgumentException("An invalid IRenderHelper was registered!");
	}

	public static IRenderHelper getRenderHelper(Class<? extends LivingEntity> clazz) {
		return RENDER_HELPERS.getOrDefault(clazz, null);
	}
}
