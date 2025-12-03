/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2024 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.client.gui;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.part.AttachmentPoint;
import uk.kihira.tails.common.client.part.AttachmentPoints;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common.client.part.LocalPartManager;
import uk.kihira.tails.common.client.part.RootAttachmentPoint;
import uk.kihira.tails.neoforge.client.gui.panel.ControlsPanel;
import uk.kihira.tails.neoforge.client.gui.panel.LibraryImportPanel;
import uk.kihira.tails.neoforge.client.gui.panel.LibraryInfoPanel;
import uk.kihira.tails.neoforge.client.gui.panel.LibraryPanel;
import uk.kihira.tails.neoforge.client.gui.panel.PartsPanel;
import uk.kihira.tails.neoforge.client.gui.panel.PreviewPanel;
import uk.kihira.tails.neoforge.client.gui.panel.TexturePanel;
import uk.kihira.tails.neoforge.client.gui.panel.TintPanel;

/**
 * The editor screen.
 */
@Internal
public class EditorScreen extends LayeredScreen {

	private RootAttachmentPoint rootAttachment;
	private AttachmentPoint attachment;
	private final ClientPartsData originalPartsData;
	protected ClientPartsData partsData;
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
		Objects.requireNonNull(original, "original");

		this.onSave = Objects.requireNonNull(onSave, "onSave");

		// Default to Tail.
		attachment = AttachmentPoints.get("body/tail");
		rootAttachment = attachment.root();
		playerUUID = Objects.requireNonNull(uuid, "uuid");
		isLocalPlayer = uuid.equals(TailsClientPlatform.get().getLocalUUID());
		this.renderingEntity = Objects.requireNonNull(renderingEntity, "renderingEntity");

		final ClientPartInfo partInfo = original.getPartInfo(attachment);

		originalPartInfo = partInfo.clone();
		editingPartInfo = originalPartInfo.clone();
		originalPartsData = original;
		setPartsData(original.deepCopy());
	}

	public static EditorScreen openDefault() {
		return new EditorScreen(
				LocalPartManager.getOrCreateLocalPartsData(),
				TailsClientPlatform.get().getLocalUUID(),
				Minecraft.getInstance().player,
				screen -> {
					// Update part info, set local and send it to the server.
					LocalPartManager.setLocalPartsDataFromEditorAndSync(screen.getPartsData());
					screen.minecraft.popGuiLayer();
				});
	}

	@Override
	public void init() {
		final boolean firstInit = tintPanel == null;

		final int previewLeft = 110 + 4;
		final int previewRight = width - previewLeft;
		final int previewBottom = height - 30;
		final int texSelectHeight = 50;

		// Not an ideal solution but keeps everything from resetting on resize.
		if (firstInit) {
			previewPanel = new PreviewPanel(this, previewLeft, 0, previewRight - previewLeft, previewBottom);
			partsPanel = new PartsPanel(this, 0, 0, previewLeft, height - texSelectHeight);
			texturePanel = new TexturePanel(this, 0, height - texSelectHeight, previewLeft, 58);
			tintPanel = new TintPanel(this, previewRight, 0, width - previewRight, height);
			libraryPanel = new LibraryPanel(this, 0, 0, previewLeft, height);
			libraryImportPanel = new LibraryImportPanel(this, previewRight, height - 60, width - previewRight, 60);
			libraryInfoPanel = new LibraryInfoPanel(this, previewRight, 0, width - previewRight, height - 60);
			controlsPanel = new ControlsPanel(this, previewLeft, previewBottom, previewRight - previewLeft, height - previewBottom);

			previewPanel.init();
			partsPanel.init();
			texturePanel.init();
			tintPanel.init();
			libraryPanel.init();
			libraryImportPanel.init();
			libraryInfoPanel.init();
			controlsPanel.init();

			libraryInfoPanel.setVisible(false);
			libraryImportPanel.setVisible(false);
			libraryPanel.setVisible(false);
		} else {
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

		if (firstInit) {
			addRenderableWidget(previewPanel);
			addRenderableWidget(partsPanel);
			addRenderableWidget(texturePanel);
			addRenderableWidget(tintPanel);
			addRenderableWidget(libraryPanel);
			addRenderableWidget(libraryImportPanel);
			addRenderableWidget(libraryInfoPanel);
			addRenderableWidget(controlsPanel);
		}
	}

	@Override
	public void removed() {
		setPartsData(originalPartsData);
		super.removed();
	}

	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		previewPanel.renderBackground(gui, mouseX, mouseY, partialTick);
		partsPanel.renderBackground(gui, mouseX, mouseY, partialTick);
		texturePanel.renderBackground(gui, mouseX, mouseY, partialTick);
		tintPanel.renderBackground(gui, mouseX, mouseY, partialTick);
		libraryPanel.renderBackground(gui, mouseX, mouseY, partialTick);
		libraryImportPanel.renderBackground(gui, mouseX, mouseY, partialTick);
		libraryInfoPanel.renderBackground(gui, mouseX, mouseY, partialTick);
		controlsPanel.renderBackground(gui, mouseX, mouseY, partialTick);

		super.render(gui, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (previewPanel.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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