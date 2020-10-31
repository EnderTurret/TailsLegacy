/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

class GuiExport extends GuiBaseScreen {

	private final GuiEditor parent;
	private final PartsData partsData;

	private ITextComponent exportMessage = null;
	private URI exportLoc;
	private Button openFolderButton;

	GuiExport(GuiEditor parent, PartsData partsData) {
		super(new StringTextComponent(""));
		this.parent = parent;
		this.partsData = partsData;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init() {
		// Left
		addButton(new GuiButtonTooltip(20, height - 90, 130, 20, new TranslationTextComponent("gui.button.export.userdir"),
				minecraft.getMainWindow().getScaledWidth() / 2, b -> handleExport(0), new TranslationTextComponent("gui.button.export.tooltip", System.getProperty("user.home"))));
		addButton(new GuiButtonTooltip(20, height - 65, 130, 20, new TranslationTextComponent("gui.button.export.minecraftdir"),
				minecraft.getMainWindow().getScaledWidth() / 2, b -> handleExport(1), new TranslationTextComponent("gui.button.export.tooltip", System.getProperty("user.dir"))));
		addButton(new GuiButtonTooltip(20, height - 40, 130, 20, new TranslationTextComponent("gui.button.export.custom"),
				minecraft.getMainWindow().getScaledWidth() / 2, b -> handleExport(2), new TranslationTextComponent("gui.button.export.custom.tooltip")));

		// Right
		addButton(openFolderButton = new GuiButtonTooltip(width - 150, height - 65, 130, 20, new TranslationTextComponent("gui.button.openfolder"),
				minecraft.getMainWindow().getScaledWidth() / 2, b -> {
					if (exportLoc != null)
						try {
							Desktop.getDesktop().browse(exportLoc);
						} catch (IOException e) {
							setExportMessage(new StringTextComponent("Failed to open export location: " + e).mergeStyle(TextFormatting.DARK_RED));
							e.printStackTrace();
						}
				}, new TranslationTextComponent("gui.button.openfolder.tooltip")));
		openFolderButton.visible = exportMessage != null;

		addButton(new GuiButtonTooltip(width - 150, height - 40, 130, 20, new TranslationTextComponent("gui.button.upload"),
				minecraft.getMainWindow().getScaledWidth() / 2, b -> {
					final BufferedImage image = TextureHelper.writePartsDataToSkin(partsData, minecraft.player);
					final Runnable runnable = () -> {
						exportMessage = new TranslationTextComponent("tails.uploading");
						new ImgurUpload().uploadImage(image);
					};
					runnable.run();
				}, new TranslationTextComponent("tails.upload.tooltip")));
	}

	@Override
	public void render(MatrixStack matrixStackIn, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStackIn);

		this.drawCenteredString(matrixStackIn, font, I18n.format("gui.export.title"), width / 2, 25, 0xFFFFFF);
		font.func_238418_a_(new TranslationTextComponent("gui.export.information"), width / 6, 50, (int) (minecraft.getMainWindow().getScaledWidth() / 1.5F), 0xFFFFFF);
		if (exportMessage != null)
			font.func_238418_a_(exportMessage, 160, height - 88, width - 160, 0xFFFFFF);

		super.render(matrixStackIn, mouseX, mouseY, partialTicks);
	}

	private void handleExport(int id) {
		// Export to file.
		final AbstractClientPlayerEntity player = minecraft.player;
		File file;

		exportMessage = null;
		exportLoc = null;
		if (id == 0) file = new File(System.getProperty("user.home"));
		else if (id == 1) file = new File(System.getProperty("user.dir"));
		else {
			final JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				file = fileChooser.getSelectedFile();
			else return;
		}

		if (file.exists() && file.canWrite()) {
			exportLoc = file.toURI();
			file = new File(file, File.separatorChar + player.getGameProfile().getName() + ".png");

			if (!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					setExportMessage(new StringTextComponent("Failed to create skin file! " + e).mergeStyle(TextFormatting.DARK_RED));
					e.printStackTrace();
				}

			final BufferedImage image = TextureHelper.writePartsDataToSkin(partsData, player);
			if (image != null)
				try {
					ImageIO.write(image, "png", file);
				} catch (IOException e) {
					setExportMessage(new StringTextComponent("Failed to save skin file! " + e).mergeStyle(TextFormatting.DARK_RED));
					e.printStackTrace();
				}
			else {
				setExportMessage(new StringTextComponent("Failed to export skin, image was null!").mergeStyle(TextFormatting.DARK_RED));
				file.delete();
			}
		}

		if (exportMessage == null) {
			savePartsData();
			openFolderButton.visible = true;
			setExportMessage(new TranslationTextComponent("tails.export.success", file).mergeStyle(TextFormatting.GREEN));
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 1) {
			minecraft.displayGuiScreen(parent);
			return true;
		} else
			return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private void setExportMessage(ITextComponent message) {
		exportMessage = message;
		ToastManager.INSTANCE.createCenteredToast(width / 2, height - 45, minecraft.getMainWindow().getScaledWidth() / 3, exportMessage);
	}

	private void savePartsData() {
		Tails.setLocalPartsData(partsData);
		Tails.proxy.addPartsData(minecraft.player.getUniqueID(), partsData);
		Tails.networkWrapper.sendToServer(new PlayerDataMessage(minecraft.getSession().getProfile().getId(), partsData, false));
	}

	private class ImgurUpload {
		static final String CLIENT_ID = "ceb9fca19ef9a31";

		void uploadImage(BufferedImage image) {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedReader in = null;

			try {
				final URL url = new URL("https://api.imgur.com/3/upload.json");

				ImageIO.write(image, "png", baos);
				baos.flush();

				final String base64Image = DatatypeConverter.printBase64Binary(baos.toByteArray());
				final String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(base64Image, "UTF-8");

				final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);

				conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);

				final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.close();

				// Successful uploading!
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					final JsonObject jsonElement = new JsonParser().parse(in).getAsJsonObject();
					if (jsonElement.get("status").getAsInt() == 200) {
						final JsonObject dataJson = jsonElement.get("data").getAsJsonObject();
						final String id = dataJson.get("id").getAsString();

						final String imgurURL = "http://imgur.com/" + id + ".png";
						final String skinURL = "https://minecraft.net/profile/skin/remote?url=";

						setExportMessage(new TranslationTextComponent("tails.upload.success").mergeStyle(TextFormatting.GREEN));
						exportLoc = URI.create(skinURL + imgurURL);
						openFolderButton.visible = true;
						savePartsData();

						Desktop.getDesktop().browse(exportLoc);
					} else
						handleError(jsonElement);
				} else if (conn.getResponseCode() != 500) {
					in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					final JsonObject jsonElement = new JsonParser().parse(in).getAsJsonObject();
					handleError(jsonElement);
				}
				else setExportMessage(new TranslationTextComponent("tails.upload.failed").mergeStyle(TextFormatting.DARK_RED));

			} catch (IOException | JsonParseException e) {
				Tails.logger.catching(e);
			} finally {
				IOUtils.closeQuietly(baos);
				IOUtils.closeQuietly(in);
			}
		}

		private void handleError(JsonObject json) {
			final int status = json.get("status").getAsInt();

			// Rate limiting.
			if (status == 429 || status == 403)
				setExportMessage(new TranslationTextComponent("tails.upload.ratelimit").mergeStyle(TextFormatting.DARK_RED));
			else setExportMessage(new TranslationTextComponent("tails.upload.failed").mergeStyle(TextFormatting.DARK_RED));
		}
	}

}
