/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 *
 * Some code provided by iChun under LGPLv3
 */

package uk.kihira.tails.client;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    public static void startGlScissor(int x, int y, int width, int height) {
        MainWindow mc = Minecraft.getInstance().getMainWindow();

        double scaleW = (double)mc.getWidth() / mc.getScaledWidth();
        double scaleH = (double)mc.getHeight() / mc.getScaledHeight();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)Math.floor(x * scaleW), (int)Math.floor(mc.getHeight() - ((y + height) * scaleH)), (int)Math.floor((x + width) * scaleW) - (int)Math.floor(x * scaleW), (int)Math.floor(mc.getHeight() - (y * scaleH)) - (int)Math.floor(mc.getHeight() - ((y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public static void endGlScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
