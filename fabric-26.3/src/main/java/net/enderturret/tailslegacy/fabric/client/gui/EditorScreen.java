/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import net.enderturret.tailslegacy.common.client.TailsClientPlatform;
import net.enderturret.tailslegacy.common.client.duck.TailsEntity;
import net.enderturret.tailslegacy.common.client.part.AttachmentPoint;
import net.enderturret.tailslegacy.common.client.part.AttachmentPoints;
import net.enderturret.tailslegacy.common.client.part.ClientPartInfo;
import net.enderturret.tailslegacy.common.client.part.ClientPartsData;
import net.enderturret.tailslegacy.common.client.part.ClientPlayerPartManager;
import net.enderturret.tailslegacy.common.client.part.LocalPartManager;
import net.enderturret.tailslegacy.common.client.part.RootAttachmentPoint;
import net.enderturret.tailslegacy.fabric.client.gui.panel.ControlsPanel;
import net.enderturret.tailslegacy.fabric.client.gui.panel.LibraryImportPanel;
import net.enderturret.tailslegacy.fabric.client.gui.panel.LibraryInfoPanel;
import net.enderturret.tailslegacy.fabric.client.gui.panel.LibraryPanel;
import net.enderturret.tailslegacy.fabric.client.gui.panel.Panel;
import net.enderturret.tailslegacy.fabric.client.gui.panel.PartsPanel;
import net.enderturret.tailslegacy.fabric.client.gui.panel.PreviewPanel;
import net.enderturret.tailslegacy.fabric.client.gui.panel.TexturePanel;
import net.enderturret.tailslegacy.fabric.client.gui.panel.TintPanel;

/**
 * The editor screen.
 */
@Internal
public class EditorScreen extends BaseScreen {

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
	private boolean saving = false;

	protected final List<Panel> panels = new ArrayList<>();
	protected TintPanel tintPanel;
	protected PartsPanel partsPanel;
	protected PreviewPanel previewPanel;
	protected TexturePanel texturePanel;
	protected ControlsPanel controlsPanel;
	protected LibraryPanel libraryPanel;
	protected LibraryInfoPanel libraryInfoPanel;
	protected LibraryImportPanel libraryImportPanel;

	public EditorScreen(ClientPartsData original, UUID uuid, LivingEntity renderingEntity, Consumer<EditorScreen> onSave) {
		super(Component.empty());
		Objects.requireNonNull(original, "original");

		this.onSave = Objects.requireNonNull(onSave, "onSave");

		// Default to Tail.
		attachment = Objects.requireNonNull(AttachmentPoints.get("body/tail"), "Attachment point body/tail not found!");
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
					screen.minecraft.gui.setScreen(null);
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
			panels.add(previewPanel = new PreviewPanel(this, previewLeft, 0, previewRight - previewLeft, previewBottom));
			panels.add(partsPanel = new PartsPanel(this, 0, 0, previewLeft, height - texSelectHeight));
			panels.add(texturePanel = new TexturePanel(this, 0, height - texSelectHeight, previewLeft, 58));
			panels.add(tintPanel = new TintPanel(this, previewRight, 0, width - previewRight, height));
			panels.add(libraryPanel = new LibraryPanel(this, 0, 0, previewLeft, height));
			panels.add(libraryImportPanel = new LibraryImportPanel(this, previewRight, height - 60, width - previewRight, 60));
			panels.add(libraryInfoPanel = new LibraryInfoPanel(this, previewRight, 0, width - previewRight, height - 60));
			panels.add(controlsPanel = new ControlsPanel(this, previewLeft, previewBottom, previewRight - previewLeft, height - previewBottom));
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

		for (Panel panel : panels) panel.init();

		super.init();

		for (Panel panel : panels)
			addRenderableWidget(panel);
	}

	@Override
	public void removed() {
		if (!saving)
			setPartsData(originalPartsData);

		for (Panel panel : panels) panel.removed();
		super.removed();
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		for (Panel panel : panels)
			if (panel.visible)
				panel.extractBackground(gui, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
		if (previewPanel.mouseDragged(event, mouseX, mouseY)) return true;
		return super.mouseDragged(event, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
		if (tintPanel.isSelectingColour()) return tintPanel.mouseClicked(event, isDoubleClick);
		return super.mouseClicked(event, isDoubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		previewPanel.mouseReleased(event);
		return super.mouseReleased(event);
	}

	@Override
	public void tick() {
		for (ClientPartInfo part : getPartsData().getParts())
			part.tickAnimator((TailsEntity) renderingEntity);

		partsPanel.tick();
	}

	public void close() {
		saving = true;
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

	public void setPartsData(ClientPartsData newPartsData) {
		if (partsData == newPartsData) return;
		partsData = newPartsData;
		editingPartInfo = partsData.getPartInfo(attachment);
		ClientPlayerPartManager.get().set(playerUUID, partsData);
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

	public ClientPartInfo getEditingPartInfo() { return editingPartInfo; }
	public ClientPartsData getPartsData() { return partsData; }
	public AttachmentPoint getAttachmentPoint() { return attachment; }
	public ClientPartInfo getOriginalPartInfo() { return originalPartInfo; }

	public PreviewPanel getPreviewPanel() { return previewPanel; }
	public PartsPanel getPartPanel() { return partsPanel; }
	public TexturePanel getTexturePanel() { return texturePanel; }
	public TintPanel getTintPanel() { return tintPanel; }
	public LibraryPanel getLibraryPanel() { return libraryPanel; }
	public LibraryImportPanel getLibraryImportPanel() { return libraryImportPanel; }
	public LibraryInfoPanel getLibraryInfoPanel() { return libraryInfoPanel; }
	public ControlsPanel getControlsPanel() { return controlsPanel; }
}