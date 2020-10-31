/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiIconButton extends Button implements ITooltip {

    public static final ResourceLocation iconsTextures = new ResourceLocation("tails", "texture/gui/icons.png");

    protected final Icons icon;
    private final List<IReorderingProcessor> tooltip;

    public GuiIconButton(int x, int y, Icons icon, IPressable onPress, ITextComponent... tooltips) {
        super(x, y, 16 ,16, new StringTextComponent(""), onPress);
        this.icon = icon;
        this.tooltip = Arrays.stream(tooltips).map(ITextComponent::func_241878_f).collect(Collectors.toList());
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            Minecraft.getInstance().getTextureManager().bindTexture(iconsTextures);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            //Check mouse over
            isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            int textureOffset = getYImage(isHovered);

            blit(matrixStack, x, y, icon.u, icon.v + (textureOffset * 16), 16, 16);
        }
    }

    public void setHover(boolean hover) {
        isHovered = hover;
    }

    @Override
    public List<IReorderingProcessor> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
        return tooltip;
    }

    public static class GuiIconToggleButton extends GuiIconButton {

        public boolean toggled;

        public GuiIconToggleButton(int x, int y, Icons icon, IPressable onPress, ITextComponent... tooltips) {
            super(x, y, icon, onPress, tooltips);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height) {
                toggled = !toggled;
                onPress();
                return true;
            }
            return false;
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (visible && toggled) {
                Minecraft.getInstance().getTextureManager().bindTexture(iconsTextures);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                //Check mouse over
                isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
                blit(matrixStack, x, y, icon.u, icon.v + 32, 16, 16);
            } else {
                super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    public enum Icons {
        UNDO(0, 0),
        QUESTION(16, 0),
        EYEDROPPER(32, 0),
        SAVE(48, 0),
        DELETE(64, 0),
        COPY(80, 0),
        STAR(96, 0),
        EDIT(112, 0),
        UPLOAD(128, 0),
        DOWNLOAD(144, 0),
        SEARCH(160, 0),
        SERVER(176, 0),
        IMPORT(192, 0),
        EXPORT(208, 0);

        public final int u;
        public final int v;

        Icons(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }
}
