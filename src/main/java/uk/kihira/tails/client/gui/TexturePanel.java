package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.render.RenderPart;
import uk.kihira.tails.common.PartInfo;

public class TexturePanel extends Panel<GuiEditor> {
	private final int texSelectX = 17;

	private ExtendedButton leftBtn;
	private ExtendedButton rightBtn;

	public TexturePanel(GuiEditor parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		//Texture select
		addButton(leftBtn = new ExtendedButton(5, texSelectX, 15, 15, new StringTextComponent("<"), b -> {
			PartInfo originalPartInfo = parent.getEditingPartInfo();
			RenderPart part = PartRegistry.getRenderPart(parent.getPartType(), originalPartInfo.typeid);
			if (parent.textureID - 1 >= 0)
				parent.textureID--;
			else
				parent.textureID = part.getTextureNames(originalPartInfo.subid).length - 1;
			PartInfo partInfo = new PartInfo(true, originalPartInfo.typeid, originalPartInfo.subid, parent.textureID,
					originalPartInfo.tints, originalPartInfo.partType, originalPartInfo.scale, null);
			parent.setPartsInfo(partInfo);
		}));
		addButton(rightBtn = new ExtendedButton(right - left - 20, texSelectX, 15, 15, new StringTextComponent(">"), b -> {
			PartInfo originalPartInfo = parent.getEditingPartInfo();
			RenderPart part = PartRegistry.getRenderPart(parent.getPartType(), originalPartInfo.typeid);
			if (part.getTextureNames(originalPartInfo.subid).length > parent.textureID + 1)
				parent.textureID++;
			else
				parent.textureID = 0;
			PartInfo partInfo = new PartInfo(true, originalPartInfo.typeid, originalPartInfo.subid, parent.textureID,
					originalPartInfo.tints, originalPartInfo.partType, originalPartInfo.scale, null);
			parent.setPartsInfo(partInfo);
		}));
		parent.textureID = parent.getEditingPartInfo().textureID;

		updateButtons();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		PartInfo partInfo = parent.getEditingPartInfo();

		setBlitOffset(-10);
		fillGradient(matrixStack, 0, 0, right - left, bottom - top, 0xCC000000, 0xCC000000);
		setBlitOffset(-5);
		fillGradient(matrixStack, 7, texSelectX, right - left - 15, texSelectX + 15, 0x55000000, 0x55000000); //Use gradientRect so it actually takes into account zlevel

		//Texture select
		drawCenteredString(matrixStack, font, I18n.format("gui.texture"), right / 2, texSelectX - 12, 0xFFFFFF);
		font.drawString(matrixStack, I18n.format(parent.getPartType().name().toLowerCase() + ".texture." + PartRegistry.getRenderPart(parent.getPartType(),
				partInfo.typeid).getTextureNames(partInfo.subid)[parent.textureID] + ".name"), 25, texSelectX + 4, 0xFFFFFF);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	void updateButtons() {
		PartInfo originalPartInfo = parent.getEditingPartInfo();
		RenderPart part = PartRegistry.getRenderPart(parent.getPartType(), originalPartInfo.typeid);

		int texCount = part.getTextureNames(originalPartInfo.subid).length;
		if (leftBtn != null && rightBtn != null)
			leftBtn.active = rightBtn.active = texCount > 1;
	}
}
