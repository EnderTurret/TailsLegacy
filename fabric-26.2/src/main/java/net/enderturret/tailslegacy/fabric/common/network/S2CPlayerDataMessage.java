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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import net.enderturret.tailslegacy.common.TailsPlatform;
import net.enderturret.tailslegacy.common.network.BaseS2CPlayerDataMessage;
import net.enderturret.tailslegacy.common.part.PartsData;

@Internal
public record S2CPlayerDataMessage(UUID uuid, PartsData partsData) implements CustomPacketPayload, BaseS2CPlayerDataMessage {

	public static final Type<S2CPlayerDataMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TailsPlatform.MOD_ID, "sync_to_client"));

	public static final StreamCodec<FriendlyByteBuf, S2CPlayerDataMessage> STREAM_CODEC = StreamCodec.of(S2CPlayerDataMessage::encode, S2CPlayerDataMessage::decode);

	@Override
	public Type<S2CPlayerDataMessage> type() {
		return TYPE;
	}

	private static S2CPlayerDataMessage decode(FriendlyByteBuf buf) {
		final UUID uuid = buf.readUUID();
		final String tailInfoJson = buf.readUtf(Short.MAX_VALUE);

		return new S2CPlayerDataMessage(uuid, BaseS2CPlayerDataMessage.decodeJson(uuid, tailInfoJson));
	}

	private static void encode(FriendlyByteBuf buf, S2CPlayerDataMessage msg) {
		buf.writeUUID(msg.uuid);
		buf.writeUtf(BaseS2CPlayerDataMessage.encodeJson(msg.partsData), Short.MAX_VALUE);
	}
}