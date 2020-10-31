/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class GuiSlider extends ExtendedButton implements IControl<Float> {

	public float currentValue;
	private final float minValue;
	private final float maxValue;
	private float sliderValue;
	private boolean dragging = false;
	private IControlCallback<GuiSlider, Float> parent;

	public GuiSlider(int x, int y, int width, float minValue, float maxValue, float defaultValue) {
		super(x, y, width, 20, new StringTextComponent(String.valueOf(defaultValue)), b -> {});
		this.minValue = minValue;
		this.maxValue = maxValue;
		currentValue = defaultValue;
		sliderValue = MathHelper.clamp((currentValue - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
	}

	public GuiSlider(IControlCallback<GuiSlider, Float> parent, int x, int y, int width, float minValue, float maxValue, float defaultValue) {
		this(x, y, width, minValue, maxValue, defaultValue);
		this.parent = parent;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
		super.renderButton(matrixStack, mouseX, mouseY, partial);

		if (visible) {
			Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);
			blit(matrixStack, (int) (x + sliderValue * (width - 8)), y, 0, 66, 4, height);
			blit(matrixStack, (int) (x + sliderValue * (width - 8) + 4), y, 196, 66, 4, height);
		}
	}

	@Override
	public boolean mouseDragged(double xPos, double yPos, int button, double dragX, double dragY) {
		if (visible && dragging)
			updateValues(xPos, yPos);

		return super.mouseDragged(xPos, yPos, button, dragX, dragY);
	}

	@Override
	public boolean mouseClicked(double xPos, double yPos, int button) {
		if (super.mouseClicked(xPos, yPos, button)) {
			updateValues(xPos, yPos);
			dragging = true;
			return true;
		} else
			return false;
	}

	@Override
	public int getYImage(boolean bool) {
		return 0;
	}

	@Override
	public boolean mouseReleased(double xPos, double yPos, int button) {
		dragging = false;
		return super.mouseReleased(xPos, yPos, button);
	}

	@Override
	public void setValue(Float newValue) {
		currentValue = newValue;
		sliderValue = MathHelper.clamp((currentValue - minValue) / (maxValue - minValue), 0.0F, 1.0F);
		setMessage(new StringTextComponent(String.valueOf(currentValue)));
	}

	@Override
	public Float getValue() {
		return currentValue;
	}

	private void updateValues(double xPos, double yPos) {
		final float prevValue = currentValue;

		sliderValue = (float) MathHelper.clamp((xPos - (x + 4F)) / (width - 8F), 0F, 1F);
		currentValue = (int) (sliderValue * (maxValue - minValue) + minValue);

		if (parent != null && !parent.onValueChange(this, prevValue, currentValue))
			setValue(prevValue);
		else setMessage(new StringTextComponent(String.valueOf(currentValue)));
	}
}
