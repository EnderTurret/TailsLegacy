/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import uk.kihira.tails.client.gui.panel.ControlsPanel;
import uk.kihira.tails.client.gui.panel.LibraryImportPanel;
import uk.kihira.tails.client.gui.panel.LibraryInfoPanel;
import uk.kihira.tails.client.gui.panel.LibraryPanel;
import uk.kihira.tails.client.gui.panel.PartsPanel;
import uk.kihira.tails.client.gui.panel.PreviewPanel;
import uk.kihira.tails.client.gui.panel.TexturePanel;
import uk.kihira.tails.client.gui.panel.TintPanel;
import uk.kihira.tails.client.texture.TextureHelper;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;
import uk.kihira.tails.common.part.PartsData;

/**
 * The editor screen.
 */
public class EditorScreen extends LayeredScreen {

	private int textureId;
	private PartType partType;
	private PartsData partsData;
	private PartInfo editingPartInfo;
	private PartInfo originalPartInfo;
	private final UUID playerUUID;

	private int guiScale;

	protected TintPanel tintPanel;
	protected PartsPanel partsPanel;
	protected PreviewPanel previewPanel;
	protected TexturePanel texturePanel;
	protected ControlsPanel controlsPanel;
	protected LibraryPanel libraryPanel;
	protected LibraryInfoPanel libraryInfoPanel;
	protected LibraryImportPanel libraryImportPanel;

	public EditorScreen() {
		super(4, new StringTextComponent(""));
		// Backup original PartInfo or create default one.
		if (Tails.localPartsData == null)
			Tails.setLocalPartsData(new PartsData(), null);

		// Default to Tail.
		partType = PartType.TAIL;
		for (PartType partType : PartType.values())
			if (!Tails.localPartsData.hasPartInfo(partType))
				Tails.localPartsData.setPartInfo(partType, PartInfo.none(partType));

		final PartInfo partInfo = Tails.localPartsData.getPartInfo(partType);
		playerUUID = PlayerEntity.getUUID(Minecraft.getInstance().getSession().getProfile());

		originalPartInfo = partInfo.deepCopy();
		setPartsData(Tails.localPartsData.deepCopy());
		editingPartInfo = originalPartInfo.deepCopy();
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
	public void onClose() {
		Tails.PROXY.addPartsData(playerUUID, Tails.localPartsData);
		super.onClose();
	}

	public void refreshTintPane() {
		tintPanel.refreshTintPane();
	}

	public void setPartsInfo(PartInfo newPartInfo) {
		//editingPartInfo.setTexture(null); // Clear texture data as we will no longer need it.
		editingPartInfo = newPartInfo;

		if (!editingPartInfo.isEmpty())
			editingPartInfo.setTexture(TextureHelper.generateTexture(playerUUID, editingPartInfo));

		partsData.setPartInfo(partType, editingPartInfo);
		setPartsData(partsData);

		texturePanel.updateButtons();
	}

	public PartInfo getEditingPartInfo() {
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

		PartInfo newPartInfo = partsData.getPartInfo(partType);
		if (newPartInfo == null)
			newPartInfo = PartInfo.none(partType);
		originalPartInfo = newPartInfo.deepCopy();
		final PartInfo partInfo = originalPartInfo.deepCopy();

		tintPanel.setEditingTint(0);
		setPartsInfo(partInfo);
		partsPanel.initPartList();
		refreshTintPane();
		textureId = partInfo.getTextureId();
		texturePanel.updateButtons();
	}

	public PartType getPartType() {
		return partType;
	}

	private void setScale(int scale) {
		Minecraft.getInstance().gameSettings.guiScale = scale;
		resize(Minecraft.getInstance(), Minecraft.getInstance().getMainWindow().getScaledWidth(), Minecraft.getInstance().getMainWindow().getScaledHeight());
	}

	public PartInfo getOriginalPartInfo() {
		return originalPartInfo;
	}

	public int getTextureId() {
		return textureId;
	}

	public void setTextureId(int value) {
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