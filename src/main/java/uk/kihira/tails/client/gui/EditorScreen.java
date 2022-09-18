/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.client.gui.panel.ControlsPanel;
import uk.kihira.tails.client.gui.panel.LibraryImportPanel;
import uk.kihira.tails.client.gui.panel.LibraryInfoPanel;
import uk.kihira.tails.client.gui.panel.LibraryPanel;
import uk.kihira.tails.client.gui.panel.PartsPanel;
import uk.kihira.tails.client.gui.panel.PreviewPanel;
import uk.kihira.tails.client.gui.panel.TexturePanel;
import uk.kihira.tails.client.gui.panel.TintPanel;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.client.part.Part;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.part.IPartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.proxy.CommonProxy;

/**
 * The editor screen.
 */
public class EditorScreen extends LayeredScreen {

	private Part.PartTexture textureId;
	private PartType partType;
	private PartsData partsData;
	private ClientPartInfo editingPartInfo;
	private ClientPartInfo originalPartInfo;
	private final UUID playerUUID;

	private final Consumer<EditorScreen> onSave;

	protected TintPanel tintPanel;
	protected PartsPanel partsPanel;
	protected PreviewPanel previewPanel;
	protected TexturePanel texturePanel;
	protected ControlsPanel controlsPanel;
	protected LibraryPanel libraryPanel;
	protected LibraryInfoPanel libraryInfoPanel;
	protected LibraryImportPanel libraryImportPanel;

	public EditorScreen(PartsData original, Consumer<EditorScreen> onSave) {
		super(4, Component.empty());
		this.onSave = onSave;

		// Default to Tail.
		partType = PartType.TAIL;
		playerUUID = ClientUtils.getPlayerUUID();

		// Backup original PartInfo or create default one.
		if (original == null)
			original = new PartsData();

		for (PartType partType : PartType.values())
			if (!original.hasPartInfo(partType))
				original.setPartInfo(partType, IPartInfo.empty());

		final ClientPartInfo partInfo = (ClientPartInfo) original.getPartInfo(partType);

		originalPartInfo = partInfo.deepCopy();
		editingPartInfo = originalPartInfo.deepCopy();
		setPartsData(original.deepCopy());
	}

	public static EditorScreen openDefault() {
		PartsData data = LocalPartManager.localPartsData;

		if (data == null)
			LocalPartManager.setLocalPartsData(data = new PartsData(), null);

		return new EditorScreen(data, screen -> {
			// Update part info, set local and send it to the server.
			final PartsData partsData = screen.getPartsData();

			LocalPartManager.setLocalPartsData(partsData, null);
			Tails.PROXY.addPartsData(ClientUtils.getPlayerUUID(), partsData);

			Tails.CHANNEL.sendToServer(new PlayerDataMessage(ClientUtils.getPlayerUUID(), partsData));

			if (CommonProxy.sync != null)
				CommonProxy.sync.upload(ClientUtils.getPlayerUUID(), LocalPartManager.localPartsData);

			ToastManager.INSTANCE.createCenteredToast(screen.width / 2, screen.height - 40, 100, Component.translatable("tails.gui.saved").withStyle(ChatFormatting.GREEN));

			screen.minecraft.popGuiLayer();
		});
	}

	@Override
	public void init() {
		final int previewWindowEdgeOffset = 110;
		final int previewWindowRight = width - previewWindowEdgeOffset;
		final int previewWindowBottom = height - 30;
		final int texSelectHeight = 50;

		// Not an ideal solution but keeps everything from resetting on resize.
		if (tintPanel == null) {
			getLayer(0).add(previewPanel = new PreviewPanel(this, previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom));
			getLayer(1).add(partsPanel = new PartsPanel(this, 0, 0, previewWindowEdgeOffset, height - texSelectHeight));
			getLayer(1).add(libraryPanel = new LibraryPanel(this, 0, 0, previewWindowEdgeOffset, height));
			getLayer(1).add(tintPanel = new TintPanel(this, previewWindowRight, 0, width - previewWindowRight, height));
			getLayer(1).add(libraryImportPanel = new LibraryImportPanel(this, previewWindowRight, height - 60, width - previewWindowRight, 60));
			getLayer(1).add(libraryInfoPanel = new LibraryInfoPanel(this, previewWindowRight, 0, width - previewWindowRight, height - 60));
			getLayer(1).add(controlsPanel = new ControlsPanel(this, previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom));
			getLayer(1).add(texturePanel = new TexturePanel(this, 0, height - texSelectHeight, previewWindowEdgeOffset, 58));

			libraryInfoPanel.enabled = false;
			libraryImportPanel.enabled = false;
			libraryPanel.enabled = false;
		}
		else {
			previewPanel.resize(previewWindowEdgeOffset, 0, previewWindowRight - previewWindowEdgeOffset, previewWindowBottom);
			partsPanel.resize(0, 0, previewWindowEdgeOffset, height - texSelectHeight);
			libraryPanel.resize(0, 0, previewWindowEdgeOffset, height);
			tintPanel.resize(previewWindowRight, 0, width - previewWindowRight, height);
			libraryImportPanel.resize(previewWindowRight, height - 60, width - previewWindowRight, 60);
			libraryInfoPanel.resize(previewWindowRight, 0, width - previewWindowRight, height - 60);
			controlsPanel.resize(previewWindowEdgeOffset, previewWindowBottom, previewWindowRight - previewWindowEdgeOffset, height - previewWindowBottom);
			texturePanel.resize(0, height - texSelectHeight, previewWindowEdgeOffset, 58);
		}

		super.init();
	}

	@Override
	public void removed() {
		Tails.PROXY.addPartsData(playerUUID, LocalPartManager.localPartsData);
		super.removed();
	}

	public void close() {
		onSave.accept(this);
	}

	public void refreshTintPane() {
		tintPanel.refreshTintPane();
	}

	public void setPartsInfo(ClientPartInfo newPartInfo) {
		//editingPartInfo.setTexture(null); // Clear texture data as we will no longer need it.
		editingPartInfo = newPartInfo;

		if (!editingPartInfo.isEmpty())
			editingPartInfo.setTexture(TextureHelper.generateTexture(playerUUID, editingPartInfo));

		getPartsData().setPartInfo(partType, editingPartInfo);
		//setPartsData(getPartsData());

		texturePanel.updateButtons();
	}

	public ClientPartInfo getEditingPartInfo() {
		return editingPartInfo;
	}

	public void setPartsData(PartsData newPartsData) {
		partsData = newPartsData;
		Tails.PROXY.addPartsData(playerUUID, partsData);
	}

	public PartsData getPartsData() {
		return partsData;
	}

	public void setPartType(PartType partType) {
		this.partType = partType;

		ClientPartInfo newPartInfo = ClientPartInfo.coerce(getPartsData().getPartInfo(partType));
		originalPartInfo = newPartInfo.clone();
		final ClientPartInfo partInfo = originalPartInfo.clone();

		tintPanel.setEditingTint(0);
		setPartsInfo(partInfo);
		partsPanel.initPartList();
		refreshTintPane();
		textureId = originalPartInfo.getPartTexture();
		texturePanel.updateButtons();
	}

	public PartType getPartType() {
		return partType;
	}

	public ClientPartInfo getOriginalPartInfo() {
		return originalPartInfo;
	}

	public Part.PartTexture getTextureId() {
		return textureId;
	}

	public void setTextureId(Part.PartTexture value) {
		textureId = value;
	}

	public TintPanel getTintPanel() {
		return tintPanel;
	}

	public PartsPanel getPartPanel() {
		return partsPanel;
	}

	public PreviewPanel getPreviewPanel() {
		return previewPanel;
	}

	public TexturePanel getTexturePanel() {
		return texturePanel;
	}

	public ControlsPanel getControlsPanel() {
		return controlsPanel;
	}

	public LibraryPanel getLibraryPanel() {
		return libraryPanel;
	}

	public LibraryInfoPanel getLibraryInfoPanel() {
		return libraryInfoPanel;
	}

	public LibraryImportPanel getLibraryImportPanel() {
		return libraryImportPanel;
	}
}