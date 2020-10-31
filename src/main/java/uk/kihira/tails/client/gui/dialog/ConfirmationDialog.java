package uk.kihira.tails.client.gui.dialog;

import uk.kihira.tails.client.gui.GuiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public class ConfirmationDialog<T extends GuiBase & IDialogCallback> extends Dialog<T> {

	private final List<IReorderingProcessor> messageList;

	public ConfirmationDialog(T parent, String title, final ITextComponent messageList) {
		super(parent, title, parent.width / 4, parent.height / 4, parent.width / 2, 100);
		this.messageList = Minecraft.getInstance().fontRenderer.trimStringToWidth(messageList, (parent.width / 2) - 10);
	}

	@Override
	public void init() {
		setHeight((messageList.size() * 9) + 50);

		addButton(new ExtendedButton((width / 2) - 52, height - 25, 50, 20, new StringTextComponent("Cancel"), b -> {}));
		addButton(new ExtendedButton((width / 2) + 2, height - 25, 50, 20, new StringTextComponent("Confirm"), b -> {}));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		for (int i = 0; i < messageList.size(); i++) {
			font.func_238407_a_(matrixStack, messageList.get(i), width / 2, 17 + (i * 9), 0xFFFFFFFF);
		}
	}
}
