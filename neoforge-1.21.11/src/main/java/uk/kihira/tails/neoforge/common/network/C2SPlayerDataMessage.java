/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.neoforge.common.network;

import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import uk.kihira.tails.common.TailsPlatform;
import uk.kihira.tails.common.network.BaseC2SPlayerDataMessage;
import uk.kihira.tails.common.part.PartsData;
import uk.kihira.tails.common.part.ServerPlayerPartManager;

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
	public static void handle(C2SPlayerDataMessage message, IPayloadContext context) {
		if (message.partsData == null) return;

		final ServerPlayer sender = (ServerPlayer) context.player();
		final UUID uuid = sender.getUUID();

		ServerPlayerPartManager.get().set(uuid, message.partsData);

		// Tell other clients about the change.
		// TODO: This sends the user's part data to themself, which is an unnecessary packet (they already have this data).
		PacketDistributor.sendToAllPlayers(new S2CPlayerDataMessage(uuid, message.partsData));
	}
}