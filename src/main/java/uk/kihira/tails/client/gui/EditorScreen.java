/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.client.ClientUtils;
import uk.kihira.tails.client.gui.panel.ControlsPanel;
import uk.kihira.tails.client.gui.panel.LibraryImportPanel;
import uk.kihira.tails.client.gui.panel.LibraryInfoPanel;
import uk.kihira.tails.client.gui.panel.LibraryPanel;
import uk.kihira.tails.client.gui.panel.PartsPanel;
import uk.kihira.tails.client.gui.panel.PreviewPanel;
import uk.kihira.tails.client.gui.panel.TexturePanel;
import uk.kihira.tails.client.gui.panel.TintPanel;
import uk.kihira.tails.client.part.AttachmentPoint;
import uk.kihira.tails.client.part.AttachmentPoints;
import uk.kihira.tails.client.part.ClientPartInfo;
import uk.kihira.tails.client.part.ClientPartsData;
import uk.kihira.tails.client.part.ClientPlayerPartManager;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.client.part.RootAttachmentPoint;
import uk.kihira.tails.client.texture.TextureHelper;

/**
 * The editor screen.
 */
@Internal
public class EditorScreen extends LayeredScreen {

	private RootAttachmentPoint rootAttachment;
	private AttachmentPoint attachment;
	private final ClientPartsData originalPartsData;
	private ClientPartsData partsData;
	private ClientPartInfo editingPartInfo;
	private ClientPartInfo originalPartInfo;

	private final UUID playerUUID;
	public final boolean isLocalPlayer;
	public final LivingEntity renderingEntity;

	private final Consumer<EditorScreen> onSave;

	protected TintPanel tintPanel;
	protected PartsPanel partsPanel;
	protected PreviewPanel previewPanel;
	protected TexturePanel texturePanel;
	protected ControlsPanel controlsPanel;
	protected LibraryPanel libraryPanel;
	protected LibraryInfoPanel libraryInfoPanel;
	protected LibraryImportPanel libraryImportPanel;

	public EditorScreen(ClientPartsData original, UUID uuid, LivingEntity renderingEntity, Consumer<EditorScreen> onSave) {
		super(4, Component.empty());
		this.onSave = onSave;

		// Default to Tail.
		attachment = AttachmentPoints.get("body/tail");
		rootAttachment = attachment.root();
		playerUUID = uuid;
		this.renderingEntity = renderingEntity;
		isLocalPlayer = uuid.equals(ClientUtils.getPlayerUUID());

		// Backup original PartInfo or create default one.
		if (original == null)
			original = new ClientPartsData();

		final ClientPartInfo partInfo = original.getPartInfo(attachment);

		originalPartInfo = partInfo.clone();
		editingPartInfo = originalPartInfo.clone();
		originalPartsData = original;
		setPartsData(original.deepCopy());
	}

	public static EditorScreen openDefault() {
		ClientPartsData data = LocalPartManager.getLocalPartsData();

		if (data == null)
			LocalPartManager.setLocalPartsData(data = new ClientPartsData());

		return new EditorScreen(data, ClientUtils.getPlayerUUID(), Minecraft.getInstance().player, screen -> {
			// Update part info, set local and send it to the server.
			final ClientPartsData partsData = screen.getPartsData();

			LocalPartManager.setLocalPartsData(partsData);
			ClientPlayerPartManager.get().set(ClientUtils.getPlayerUUID(), partsData);

			LocalPartManager.syncToServer();

			screen.minecraft.popGuiLayer();
		});
	}

	@Override
	public void init() {
		final int previewLeft = 110 + 4;
		final int previewRight = width - previewLeft;
		final int previewBottom = height - 30;
		final int texSelectHeight = 50;

		// Not an ideal solution but keeps everything from resetting on resize.
		if (tintPanel == null) {
			getLayer(0).add(previewPanel = new PreviewPanel(this, previewLeft, 0, previewRight - previewLeft, previewBottom));
			getLayer(1).add(partsPanel = new PartsPanel(this, 0, 0, previewLeft, height - texSelectHeight));
			getLayer(1).add(texturePanel = new TexturePanel(this, 0, height - texSelectHeight, previewLeft, 58));
			getLayer(1).add(tintPanel = new TintPanel(this, previewRight, 0, width - previewRight, height));
			getLayer(1).add(libraryPanel = new LibraryPanel(this, 0, 0, previewLeft, height));
			getLayer(1).add(libraryImportPanel = new LibraryImportPanel(this, previewRight, height - 60, width - previewRight, 60));
			getLayer(1).add(libraryInfoPanel = new LibraryInfoPanel(this, previewRight, 0, width - previewRight, height - 60));
			getLayer(1).add(controlsPanel = new ControlsPanel(this, previewLeft, previewBottom, previewRight - previewLeft, height - previewBottom));

			libraryInfoPanel.enabled = false;
			libraryImportPanel.enabled = false;
			libraryPanel.enabled = false;
		}
		else {
			previewPanel.resize(previewLeft, 0, previewRight - previewLeft, previewBottom);
			partsPanel.resize(0, 0, previewLeft, height - texSelectHeight);
			texturePanel.resize(0, height - texSelectHeight, previewLeft, 58);
			tintPanel.resize(previewRight, 0, width - previewRight, height);
			libraryPanel.resize(0, 0, previewLeft, height);
			libraryImportPanel.resize(previewRight, height - 60, width - previewRight, 60);
			libraryInfoPanel.resize(previewRight, 0, width - previewRight, height - 60);
			controlsPanel.resize(previewLeft, previewBottom, previewRight - previewLeft, height - previewBottom);
		}

		super.init();
	}

	@Override
	public void removed() {
		setPartsData(originalPartsData);
		super.removed();
		TextureHelper.logLeaks();
	}

	public void close() {
		onSave.accept(this);
	}

	public void setPartsInfo(ClientPartInfo newPartInfo, boolean force) {
		if (!force && editingPartInfo == newPartInfo) return;

		editingPartInfo.clearGlTexture(); // Clear texture data as we will no longer need it.
		editingPartInfo = newPartInfo;

		editingPartInfo.checkTexture(playerUUID, true);

		getPartsData().setPartInfo(attachment, editingPartInfo);

		texturePanel.updateButtons();
	}

	public void setPartsInfo(ClientPartInfo newPartInfo) {
		setPartsInfo(newPartInfo, true);
	}

	public ClientPartInfo getEditingPartInfo() {
		return editingPartInfo;
	}

	public void setPartsData(ClientPartsData newPartsData) {
		if (partsData == newPartsData) return;
		partsData = newPartsData;
		ClientPlayerPartManager.get().set(playerUUID, partsData);
	}

	public ClientPartsData getPartsData() {
		return partsData;
	}

	public void setRootAttachmentPoint(RootAttachmentPoint root) {
		rootAttachment = root;
		setAttachmentPoint(root.children().first());
	}

	public void setAttachmentPoint(AttachmentPoint attachment) {
		this.attachment = attachment;

		ClientPartInfo newPartInfo = ClientPartInfo.coerce(getPartsData().getPartInfo(attachment));
		originalPartInfo = newPartInfo.clone();
		final ClientPartInfo partInfo = originalPartInfo.clone();

		tintPanel.setEditingTint(0);
		setPartsInfo(partInfo);
		partsPanel.initPartList();
		texturePanel.updateButtons();
	}

	public AttachmentPoint getAttachmentPoint() {
		return attachment;
	}

	public ClientPartInfo getOriginalPartInfo() {
		return originalPartInfo;
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