/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.panel;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;

import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.common.UsernameCache;

import uk.kihira.tails.client.gui.EditorScreen;
import uk.kihira.tails.client.gui.widget.RelativeTextBox;
import uk.kihira.tails.client.part.ClientPartsData;
import uk.kihira.tails.client.part.LocalPartManager;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.part.PartsData;

@Internal
public final class LibraryImportPanel extends Panel<EditorScreen> {

	private EditBox inputField;

	public LibraryImportPanel(EditorScreen parent, int left, int top, int width, int height) {
		super(parent, left, top, width, height);
	}

	@Override
	public void init() {
		addRenderableWidget(new ExtendedButton(3, 21, right - left - 6, 18, Component.translatable("tails.gui.library.import.string"), this::importFromString));

		inputField = new RelativeTextBox(font, 3, 41, right - left - 6, 15, null);
		inputField.setMaxLength(5000);
		addRenderableWidget(inputField);
	}

	private static final Pattern PATTERN = Pattern.compile("(^.*):([0-9a-f\\-]+):(\\{.+\\})$");

	private void importFromString(Button b) {
		final String input = inputField.getValue();
		if (Strings.isNullOrEmpty(input)) return;

		final Matcher m = PATTERN.matcher(input);

		if (!m.matches()) {
			toast(Component.translatable("tails.gui.library.import.toast.invalid").withStyle(ChatFormatting.RED));
			return;
		}

		final String name = m.group(1);
		final String rawCreatorId = m.group(2);
		final String json = m.group(3);

		final UUID creatorId;

		try {
			creatorId = UUID.fromString(rawCreatorId);
		} catch (Exception e) {
			toast(Component.translatable("tails.gui.library.import.toast.invalid.uuid").withStyle(ChatFormatting.RED));
			Tails.LOGGER.error("Exception parsing import UUID \"{}\":", rawCreatorId, e);
			return;
		}

		final ClientPartsData partData;

		try {
			partData = (ClientPartsData) LocalPartManager.GSON.fromJson(json, PartsData.class);
		} catch (Exception e) {
			toast(Component.translatable("tails.gui.library.import.toast.invalid.parts").withStyle(ChatFormatting.RED));
			Tails.LOGGER.error("Exception parsing import part data:", e);
			return;
		}

		final LibraryEntryData entry = new LibraryEntryData(creatorId, fetchUsername(creatorId), name, partData);
		Tails.PROXY.getLibraryManager().addEntry(entry);
		parent.getLibraryPanel().initList();

		toast(Component.translatable("tails.gui.library.import.toast.success", name).withStyle(ChatFormatting.GREEN));
	}

	private static Services services;

	private static String fetchUsername(UUID uuid) {
		// So, there are a few different places we can try first...

		// Option A - Forge's "username cache"
		String username = UsernameCache.getLastKnownUsername(uuid);
		if (username != null) return username;

		// Option B - The user cache (some assembly required)
		if (services == null) {
			final MinecraftSessionService session = Minecraft.getInstance().getMinecraftSessionService();
			if (session instanceof HttpMinecraftSessionService http && http.getAuthenticationService() instanceof YggdrasilAuthenticationService auth) {
				services = Services.create(auth, Minecraft.getInstance().gameDirectory);
				services.profileCache().setExecutor(Minecraft.getInstance());
				GameProfileCache.setUsesAuthentication(false);
			}
		}

		if (services != null) {
			username = services.profileCache().get(uuid).map(GameProfile::getName).orElse(null);
			if (username != null) return username;
		}

		// Option C - "Just query it lol"
		GameProfile prof = new GameProfile(uuid, null);

		prof = Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(prof, false);
		username = prof.getName();

		// Incredible, we actually got a username. Let's let the caches know, shall we?
		if (username != null) {
			// Unfortunately, it looks like Forge's username cache is and I quote "too good for manipulation."
			// So instead we are only able to let our little profile cache know.
			if (services != null)
				services.profileCache().add(prof);
			return username;
		}

		// Option D - Just use the UUID
		return uuid.toString();
	}

	private void toast(Component text) {
		ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 50, parent.width / 2, text);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		fillGradient(poseStack, 0, 0, right - left, bottom - top, 0xDE000000, 0xDE000000);

		super.render(poseStack, mouseX, mouseY, partialTick);
	}
}