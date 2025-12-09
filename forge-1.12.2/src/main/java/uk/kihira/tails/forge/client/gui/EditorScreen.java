/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.forge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityLivingBase;

import uk.kihira.tails.common.client.TailsClientPlatform;
import uk.kihira.tails.common.client.part.AttachmentPoint;
import uk.kihira.tails.common.client.part.AttachmentPoints;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.ClientPartsData;
import uk.kihira.tails.common.client.part.ClientPlayerPartManager;
import uk.kihira.tails.common.client.part.LocalPartManager;
import uk.kihira.tails.common.client.part.RootAttachmentPoint;
import uk.kihira.tails.forge.client.gui.panel.ControlsPanel;
import uk.kihira.tails.forge.client.gui.panel.LibraryImportPanel;
import uk.kihira.tails.forge.client.gui.panel.LibraryInfoPanel;
import uk.kihira.tails.forge.client.gui.panel.LibraryPanel;
import uk.kihira.tails.forge.client.gui.panel.Panel;
import uk.kihira.tails.forge.client.gui.panel.PartsPanel;
import uk.kihira.tails.forge.client.gui.panel.PreviewPanel;
import uk.kihira.tails.forge.client.gui.panel.TexturePanel;
import uk.kihira.tails.forge.client.gui.panel.TintPanel;

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
	public final EntityLivingBase renderingEntity;

	private final Consumer<EditorScreen> onSave;

	protected final List<Panel> panels = new ArrayList<>();
	protected TintPanel tintPanel;
	protected PartsPanel partsPanel;
	protected PreviewPanel previewPanel;
	protected TexturePanel texturePanel;
	protected ControlsPanel controlsPanel;
	protected LibraryPanel libraryPanel;
	protected LibraryInfoPanel libraryInfoPanel;
	protected LibraryImportPanel libraryImportPanel;

	public EditorScreen(ClientPartsData original, UUID uuid, EntityLivingBase renderingEntity, Consumer<EditorScreen> onSave) {
		super();
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
				Minecraft.getMinecraft().player,
				screen -> {
					// Update part info, set local and send it to the server.
					LocalPartManager.setLocalPartsDataFromEditorAndSync(screen.getPartsData());
					screen.mc.displayGuiScreen(null);
				});
	}

	@Override
	public void initGui() {
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

		super.initGui();

		for (Panel panel : panels)
			addRenderableWidget(panel);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id >= 800) tintPanel.actionPerformed(button);
		else if (button.id >= 700) texturePanel.actionPerformed(button);
		else if (button.id >= 600) previewPanel.actionPerformed(button);
		else if (button.id >= 500) partsPanel.actionPerformed(button);
		else if (button.id >= 400) libraryPanel.actionPerformed(button);
		else if (button.id >= 300) libraryInfoPanel.actionPerformed(button);
		else if (button.id >= 200) libraryImportPanel.actionPerformed(button);
		else if (button.id >= 100) controlsPanel.actionPerformed(button);
	}

	@Override
	public void onGuiClosed() {
		setPartsData(originalPartsData);
		super.onGuiClosed();
	}

	@Override
	public void drawDefaultBackground() {
		for (Panel panel : panels)
			if (panel.visible)
				panel.renderBackground();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (tintPanel.isSelectingColour()) {
			tintPanel.mouseClicked(mouseX, mouseY, mouseButton);
			return;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
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