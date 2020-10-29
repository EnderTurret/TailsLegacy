package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

import java.io.IOException;
import java.util.ArrayList;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

public abstract class GuiBase extends GuiBaseScreen {

    //0 is bottom layer
    private final ArrayList<ArrayList<Panel>> layers = new ArrayList<>();

    public GuiBase(int layerCount, ITextComponent title) {
    	super(title);
        for (int i = 0; i < layerCount; i++) {
            layers.add(new ArrayList<>());
        }
    }

    public ArrayList<Panel> getLayer(int layer) {
        return layers.get(layer);
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                //todo switch over to using scaled resolution to allow for cramming more stuff on screen
                // Gotta cache displayWidth/Height as it can't be passed in as params anymore
                //int displayWidth = mc.getMainWindow().getWidth();
                //int displayHeight = mc.getMainWindow().getHeight();
                //mc.displayWidth = panel.right - panel.left;
                //mc.displayHeight = panel.bottom - panel.top;
                //ScaledResolution scaledRes = new ScaledResolution(mc);
                panel.resize(mc, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
                //mc.displayWidth = displayWidth;
                //mc.displayHeight = displayHeight;
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (panel.enabled) {
                    matrixStack.push();
                    matrixStack.translate(panel.left, panel.top, 0);
                    RenderSystem.color4f(1f, 1f, 1f, 1f);
                    panel.render(matrixStack, mouseX - panel.left, mouseY - panel.top, partialTicks);
                    RenderSystem.disableLighting();
                    matrixStack.pop();
                }
            }
        }
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (panel.enabled) panel.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (panel.enabled && mouseX > panel.left && mouseX < panel.right && mouseY > panel.top && mouseY < panel.bottom || panel.alwaysReceiveMouse) {
                    panel.mouseClicked(mouseX - panel.left, mouseY - panel.top, button);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (panel.enabled && mouseX > panel.left && mouseX < panel.right && mouseY > panel.top && mouseY < panel.bottom || panel.alwaysReceiveMouse) {
                    panel.mouseReleased(mouseX - panel.left, mouseY - panel.top, button);
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                if (panel.enabled && mouseX > panel.left && mouseX < panel.right && mouseY > panel.top && mouseY < panel.bottom || panel.alwaysReceiveMouse) {
                    panel.mouseDragged(mouseX - panel.left, mouseY - panel.top, button, dragX, dragY);
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void onClose() {
        for (ArrayList<Panel> layer : layers) {
            for (Panel panel : layer) {
                panel.onClose();
            }
        }
        super.onClose();
    }
}
