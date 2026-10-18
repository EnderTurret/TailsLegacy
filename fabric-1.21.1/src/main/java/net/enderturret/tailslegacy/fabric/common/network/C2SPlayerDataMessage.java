/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.common.network;

import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.netty.buffer.ByteBuf;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.network.BaseC2SPlayerDataMessage;
import net.enderturret.tailslegacy.common.part.PartsData;
import net.enderturret.tailslegacy.common.part.ServerPlayerPartManager;

@Internal
public record C2SPlayerDataMessage(PartsData partsData) implements CustomPacketPayload, BaseC2SPlayerDataMessage {

	public static final Type<C2SPlayerDataMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "sync_to_server"));

	public static final StreamCodec<ByteBuf, C2SPlayerDataMessage> STREAM_CODEC = ByteBufCodecs.stringUtf8(Short.MAX_VALUE)
			.map(C2SPlayerDataMessage::decode, C2SPlayerDataMessage::encode);

	@Override
	public Type<C2SPlayerDataMessage> type() {
		return TYPE;
	}

	private static C2SPlayerDataMessage decode(String tailInfoJson) {
		return new C2SPlayerDataMessage(BaseC2SPlayerDataMessage.decodeJson(tailInfoJson));
	}

	private static String encode(C2SPlayerDataMessage msg) {
		return BaseC2SPlayerDataMessage.encodeJson(msg.partsData);
	}

	@Internal
	public static void handle(C2SPlayerDataMessage message, ServerPlayNetworking.Context context) {
		if (message.partsData == null) return;

		final ServerPlayer sender = context.player();
		final UUID uuid = sender.getUUID();

		ServerPlayerPartManager.get().set(uuid, message.partsData);

		// Tell other clients about the change.
		final S2CPlayerDataMessage msg = new S2CPlayerDataMessage(uuid, message.partsData);
		for (ServerPlayer player : context.server().getPlayerList().getPlayers())
			if (player != sender && ServerPlayNetworking.canSend(player, S2CPlayerDataMessage.TYPE))
				ServerPlayNetworking.send(player, msg);
	}
}