/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.forge.common.network;

import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import net.enderturret.tailslegacy.common.network.BaseC2SPlayerDataMessage;
import net.enderturret.tailslegacy.common.part.PartsData;
import net.enderturret.tailslegacy.common.part.ServerPlayerPartManager;

@Internal
public record C2SPlayerDataMessage(PartsData partsData) implements BaseC2SPlayerDataMessage {

	public static C2SPlayerDataMessage decode(FriendlyByteBuf buf) {
		return new C2SPlayerDataMessage(BaseC2SPlayerDataMessage.decodeJson(buf.readUtf(Short.MAX_VALUE)));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(BaseC2SPlayerDataMessage.encodeJson(partsData), Short.MAX_VALUE);
	}

	@Internal
	public static void handle(C2SPlayerDataMessage message, Supplier<NetworkEvent.Context> context) {
		if (message.partsData == null) return;

		final ServerPlayer sender = context.get().getSender();
		final UUID uuid = sender.getUUID();

		ServerPlayerPartManager.get().set(uuid, message.partsData);

		// Tell other clients about the change.
		final S2CPlayerDataMessage msg = new S2CPlayerDataMessage(uuid, message.partsData);
		for (ServerPlayer player : sender.server.getPlayerList().getPlayers())
			if (player != sender)
				TailsNetworkManager.get().send(PacketDistributor.PLAYER.with(() -> player), msg);

		context.get().setPacketHandled(true);
	}
}