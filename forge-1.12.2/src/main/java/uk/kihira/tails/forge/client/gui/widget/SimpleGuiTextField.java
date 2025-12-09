package uk.kihira.tails.forge.client.gui.widget;

import java.util.function.Consumer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;

public class SimpleGuiTextField extends GuiTextField {

	public SimpleGuiTextField(int id, FontRenderer font, int x, int y, int width, int height) {
		super(id, font, x, y, width, height);
	}

	public void setGuiResponder(Consumer<String> responder) {
		super.setGuiResponder(new GuiResponder() {
			@Override
			public void setEntryValue(int id, String value) { responder.accept(value); }
			@Override
			public void setEntryValue(int id, float value) { responder.accept(Float.toString(value)); }
			@Override
			public void setEntryValue(int id, boolean value) { responder.accept(Boolean.toString(value)); }
		});
	}
}