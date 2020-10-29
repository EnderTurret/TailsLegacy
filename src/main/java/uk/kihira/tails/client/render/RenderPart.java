/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.render;

import uk.kihira.tails.api.IRenderHelper;
import uk.kihira.tails.client.model.ModelPartBase;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.PartInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class RenderPart {

    private static final HashMap<Class<? extends LivingEntity>, IRenderHelper> renderHelpers = new HashMap<>();

    protected final String name;
    protected final String[] textureNames;
    protected final int subTypes;
    protected final String[][] authors;
    protected final String modelAuthor;
    public final ModelPartBase modelPart;

    public RenderPart(String name, int subTypes, ModelPartBase modelPart, @Nullable String modelAuthor, String... textureNames) {
        this.name = name;
        this.subTypes = subTypes;
        this.modelAuthor = modelAuthor;
        this.modelPart = modelPart;
        this.textureNames = textureNames;
        this.authors = new String[subTypes + 1][textureNames.length];
    }

    public void render(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IRenderTypeBuffer bufferIn, double x, double y, double z, float partialTicks, int packedLightIn, int packedOverlayIn) {
        if (info.needsTextureCompile || info.getTexture() == null) {
            info.setTexture(TextureHelper.generateTexture(entity.getUniqueID(), info));
            info.needsTextureCompile = false;
        }

        matrixStack.push();

        IRenderHelper helper;
        //Support for Galacticraft as it adds its own EntityPlayer
        if (entity instanceof PlayerEntity) helper = getRenderHelper(PlayerEntity.class);
        else helper = getRenderHelper(entity.getClass());
        if (helper != null) {
            helper.onPreRenderTail(matrixStack, entity, this, info, x, y, z);
        }

        modelPart.setRotationAngles(entity, entity.limbSwing, entity.limbSwingAmount, partialTicks, info.subid, entity.rotationPitch);
        modelPart.setLivingAnimations(entity, entity.limbSwing, entity.limbSwingAmount, partialTicks);
        this.doRender(matrixStack, entity, info, bufferIn, partialTicks, packedLightIn, packedOverlayIn);
        matrixStack.pop();
    }

    protected void doRender(MatrixStack matrixStack, LivingEntity entity, PartInfo info, IRenderTypeBuffer bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn) {
        //Minecraft.getInstance().getTextureManager().bindTexture(info.getTexture());
        final IVertexBuilder buf = bufferIn.getBuffer(modelPart.getRenderType(info.getTexture()));
        this.modelPart.render(matrixStack, buf, entity, packedLightIn, packedOverlayIn, 1F, 1F, 1F, 1F, info.subid, partialTicks);
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
     * Gets the available subtypes for this tail
     * @return subtypes
     */
    public int getAvailableSubTypes() {
        return subTypes;
    }

    public String getUnlocalisedName(int subType) {
        return this.name+"."+subType+".name";
    }

    public RenderPart setAuthor(String author, int subID, int textureID) {
        authors[subID][textureID] = author;
        return this;
    }

    public RenderPart setAuthor(String author, int subID) {
        for (int textureID = 0; textureID < getTextureNames(subID).length; textureID++) {
            setAuthor(author, subID, textureID);
        }
        return this;
    }

    public RenderPart setAuthor(String author) {
        for (int subID = 0; subID <= subTypes; subID++) {
            setAuthor(author, subID);
        }
        return this;
    }

    public String getModelAuthor() {
        return modelAuthor;
    }

    public String getAuthor(int subID, int textureID) {
        return authors[subID][textureID];
    }

    public boolean hasAuthor(int subID, int textureID) {
        return getAuthor(subID, textureID) != null;
    }

    public static void registerRenderHelper(Class<? extends LivingEntity> clazz, IRenderHelper helper) {
        if (!renderHelpers.containsKey(clazz) && helper != null) {
            renderHelpers.put(clazz, helper);
        }
        else {
            throw new IllegalArgumentException("An invalid RenderHelper was registered!");
        }
    }

    public static IRenderHelper getRenderHelper(Class<? extends LivingEntity> clazz) {
        return renderHelpers.getOrDefault(clazz, null);
    }
}
